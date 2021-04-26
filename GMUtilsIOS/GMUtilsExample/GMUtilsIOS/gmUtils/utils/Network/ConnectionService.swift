//
//  ConnectionService.swift
//  Choueifat
//
//  Created by Imac on 9/3/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import Foundation
import Alamofire

/**
 * https://www.raywenderlich.com/35-alamofire-tutorial-getting-started
 * https://www.stencia.com/blog/2018/12/26/alamofire5
 */

typealias OnResponseReady<T> = (String /*requestId*/, T? /*data*/, String/*error*/) -> Void

class ConnectionService {
    
    static func checkIsConnectedToNetwork(result: @escaping (Bool, String)-> Void) {
//        if APIsPool.newInstance is APIsPool.FakeAPIs {
//            result(true, "")
//            return
//        }
        
        let hostUrl: String = "https://google.com"
        if let url = URL(string: hostUrl) {
            var request = URLRequest(url: url)
            request.httpMethod = "HEAD"
            
            URLSession(configuration: .default)
                .dataTask(with: request) { (_, response, error) -> Void in
                    guard error == nil else {
                        Log.p("Error:", error ?? "")
                        DispatchQueue.main.async {
                            result(false, "\(error?.localizedDescription ?? "")")
                        }
                        return
                    }
                    
                    guard (response as? HTTPURLResponse)?.statusCode == 200 else {
                        Log.p("The host is down")
                        DispatchQueue.main.async {
                            result(false, "The host is down")
                        }
                        return
                    }
                    
                    print("The host is up and running")
                    DispatchQueue.main.async {
                        result(true, "The host is up and running")
                    }
            }
            .resume()
        }
    }
    
    private static func generateRequestId(url: String) -> String {
        return url
    }
    
    static func doRequest<T>(url : String, method : HTTPMethod, headers : [String : String]?, params : [String : Any]?, responseType type: T.Type, requestId: String? = nil, completion: @escaping OnResponseReady<T>) where T : Codable {
        
        let requestId: String = requestId ?? generateRequestId(url: url)
        
        Log.p(".")
        Log.p("requestId: \(requestId)")
        Log.p("URL: \(url)")
        Log.p("Method: \(method)")
        Log.p("Header: \(String(describing: headers))")
        Log.p("Params: \(String(describing: params))")
        
        guard let url = URL(string: url) else {
            completion(requestId, nil, "URL Corruption")
            return
        }
        
        var headers = headers
        if method == .post {
            headers?.updateValue("application/x-www-form-urlencoded", forKey: "Content-Type")
        }
        
        let request = Alamofire.request(url, method: method, parameters: params, encoding: URLEncoding.default, headers: headers)
        performAlamofire(dataRequest: request, responseType: type, requestId: requestId, completion: completion)
    }
    
    static func doRequestWithoutCache<T>(url : String, method : HTTPMethod, headers : [String : String]?, params : [String : Any]?, responseType type: T.Type, requestId: String? = nil, completion: @escaping OnResponseReady<T>) where T : Codable {
    
        let requestId: String = requestId ?? generateRequestId(url: url)
        
        Log.p(".")
        Log.p("requestId: \(requestId)")
        Log.p("URL: \(url)")
        Log.p("Method: \(method)")
        Log.p("Header: \(String(describing: headers))")
        Log.p("Params: \(String(describing: params))")
        
        guard let url = URL(string: url) else {
            completion(requestId, nil, "URL Corruption")
            return
        }
        
        let request = Alamofire.SessionManager.default.requestWithoutCache(url, method: method, parameters: params, encoding: URLEncoding.default, headers: headers)
        performAlamofire(dataRequest: request, responseType: type, requestId: requestId, completion: completion)
    }
    
    static func postDataRequest<T>(url : String, headers : [String : String]?, bodyJson : Data?, responseType type: T.Type, requestId: String? = nil, completion: @escaping OnResponseReady<T>) where T : Codable {
        
        let requestId: String = requestId ?? generateRequestId(url: url)
        
        Log.p(".")
        Log.p("requestId: \(requestId)")
        Log.p("URL: \(url)")
        Log.p("Method: POST")
        Log.p("Header: \(String(describing: headers))")
        if (bodyJson != nil) {
            Log.p("Body: \(String(data: bodyJson!, encoding: .utf8) ?? "")")
        }
        
        guard let url = URL(string: url) else {
            completion(requestId, nil, "URL Corruption")
            return
        }
        
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.cachePolicy = .reloadIgnoringLocalAndRemoteCacheData
        
        if (headers != nil) {
            for (k, v) in headers! {
                urlRequest.addValue(v, forHTTPHeaderField: k)
            }
        }
        
        urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type")
        
        if bodyJson != nil {
            urlRequest.httpBody = bodyJson
        }
        
        let request = Alamofire.request(urlRequest)
        performAlamofire(dataRequest: request, responseType: type, requestId: requestId, completion: completion)
        
    }
    
    private static func performAlamofire<T>(dataRequest : DataRequest, responseType type: T.Type, requestId: String, completion: @escaping OnResponseReady<T>) where T : Codable {
        
        dataRequest
            .validate()
            .responseJSON { response in
                handleReponse(response: response, responseType: type, requestId: requestId, completion: completion)
        }
    }
    
    private static func handleReponse<T>(response: DataResponse<Any>, responseType type: T.Type, requestId: String, completion: @escaping OnResponseReady<T>) where T : Codable {
        guard response.result.isSuccess else {
            let error = response.result.error
            
            Log.p("Error while fetching: \(String(describing: response.result.error)), requestId: \(requestId)")
            
            completion(requestId, nil, error?.localizedDescription ?? "err")
            return
        }
        
        guard let value = response.result.value else {
            Log.p("Malformed data received from service, , requestId: \(requestId)")
            completion(requestId, nil, "Data Corruption")
            return
        }
        
        let res = value as! NSDictionary
        Log.p("response -> requestId: \(requestId)")
        Log.p(res)
        
        let json = try? JSONSerialization.data(withJSONObject: res, options: .sortedKeys)
        
        do {
            let data = try JSONDecoder().decode(type, from: json!)
            completion(requestId, data, "")
            return
        } catch let e {
            Log.p(".")
            Log.p("--\"JSON\"--")
            Log.p("response esrror -> requestId: \(requestId)")
            let jsonString = String(data: json ?? Data(), encoding: .utf8) ?? ""
            Log.p(jsonString)
            
            Log.p(".")
            Log.p(e)
            
            completion(requestId, nil, "Error: Couldn't read response, refresh")
        }
    }
    
    //---------------------------------------------------------------------------------------------------------
    
    static func postFilesRequest<T>(url: String, headers : [String : String]?, parameters : [String : String]?, files : [String : URL], responseType type: T.Type, requestId: String? = nil, completion: @escaping OnResponseReady<T>) where T : Codable {
        
        let requestId: String = requestId ?? generateRequestId(url: url)
        
        Log.p(".")
        Log.p("requestId: \(requestId)")
        Log.p("URL: \(url)")
        Log.p("Headers: \(String(describing: headers))")
        Log.p("Parameters: \(String(describing: parameters))")
        Log.p("Files: \(files)")
        
        //Content-type should be multipart-formdata in Headers.
        //In body 'form-data' option should be remain as default.
        
        // Use Alamofire to upload the image
        guard let url = URL(string: url) else {
            completion(requestId, nil, "URL Corruption")
            return
        }
        
        let multipartFormData: (MultipartFormData) -> Void = {multipartFormData in
            
            if parameters != nil {
                for (key, val) in parameters! {
                    multipartFormData.append(val.data(using: String.Encoding.utf8)!, withName: key)
                }
            }
            
            for (param, fileURL) in files {
                multipartFormData.append(fileURL, withName: param)
            }
        }
        
        let encodingCompletion: ((SessionManager.MultipartFormDataEncodingResult) -> Void) = {encodingResult in
            switch encodingResult {
            case .success(let upload, _, _):
                /*upload.responseString { (dataResponse) in
                    Log.p(dataResponse)
                }*/
                upload.responseJSON { response in
                    handleReponse(response: response, responseType: type, requestId: requestId, completion: completion)
                    
//                    if let jsonResponse = response.result.value as? [String: Any] {
//                        Log.p("jsonResponse, requestId: \(requestId)")
//                        Log.p(jsonResponse)
//
//                        let json = try? JSONSerialization.data(withJSONObject: jsonResponse, options: .sortedKeys)
//                        Log.p(String(data: json ?? Data(), encoding: .utf8) ?? "")
//
//                        do {
//                            let data = try JSONDecoder().decode(type, from: json!)
//                            completion(requestId, data, "")
//                        } catch let e {
//                            Log.p(".")
//                            Log.p("--\"JSON\"--")
//                            Log.p("response esrror -> requestId: \(requestId)")
//                            let jsonString = String(data: json ?? Data(), encoding: .utf8) ?? ""
//                            Log.p(jsonString)
//
//                            Log.p(".")
//                            Log.p(e)
//
//                            completion(requestId, nil, "Error: Couldn't read response, refresh")
//                        }
//                    }
            }
            case .failure(let encodingError):
                Log.p("requestId: \(requestId)")
                Log.p(encodingError)
                completion(requestId, nil, encodingError.localizedDescription)
            }
        }
        
        Alamofire.upload(
            multipartFormData: multipartFormData,
            to: url,
            headers: headers,
            encodingCompletion: encodingCompletion)
        
    }
    
    static func dowloadFile(url: String, progress: ((Double) -> Void)? = nil, completion: @escaping (URL?/*file path*/) -> Void) {
        let destination = DownloadRequest.suggestedDownloadDestination(for: .documentDirectory)

        Alamofire.download(
            url,
            method: .get,
            parameters: nil,
            encoding: JSONEncoding.default,
            headers: nil,
            to: destination).downloadProgress(closure: { (prgrs) in
                progress?(prgrs.fractionCompleted)
                
            }).response(completionHandler: { (downloadResponse) in
                completion(downloadResponse.destinationURL)
                //completion(downloadResponse.temporaryURL)
            })
    }
    
}

extension Alamofire.SessionManager{
    @discardableResult
    open func requestWithoutCache(
        _ url: URLConvertible,
        method: HTTPMethod = .get,
        parameters: Parameters? = nil,
        encoding: ParameterEncoding = URLEncoding.default,
        headers: HTTPHeaders? = nil)// also you can add URLRequest.CachePolicy here as parameter
        -> DataRequest
    {
        do {
            var urlRequest = try URLRequest(url: url, method: method, headers: headers)
            urlRequest.cachePolicy = .reloadIgnoringCacheData // <<== Cache disabled
            let encodedURLRequest = try encoding.encode(urlRequest, with: parameters)
            return request(encodedURLRequest)
        } catch {
            // TODO: find a better way to handle error
            print(error)
            return request(URLRequest(url: URL(string: "http://example.com/wrong_request")!))
        }
    }
}
