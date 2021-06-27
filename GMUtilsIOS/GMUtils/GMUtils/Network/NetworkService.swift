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

struct ResponseArgs<T> {
    let requestId: String
    let data: T?
    let error: String
    let responseCode: Int
}

typealias OnResponseReady<T> = (ResponseArgs<T>) -> Void

class NetworkService {
    
    static func checkIsConnectedToNetwork(result: @escaping (Bool, String)-> Void) {
        if APIsPool.newInstance is APIsPool.FakeAPIs {
            result(true, "")
            return
        }
        
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
            completion(.init(requestId: requestId, data: nil, error: "URL Corruption", responseCode: -1))
            return
        }
        
        var headers = headers
        if method == .post {
            headers?.updateValue("application/x-www-form-urlencoded", forKey: "Content-Type")
        }
        
        let request = AF.request(url, method: method, parameters: params, encoding: URLEncoding.default, headers: .init(headers ?? [String:String]()))
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
            completion(.init(requestId: requestId, data: nil, error: "URL Corruption", responseCode: -1))
            return
        }
            
        /*var request = try? URLRequest(url: url, method: method, headers: .init(headers ?? [String:String]()))
        request?.cachePolicy = .reloadIgnoringLocalAndRemoteCacheData
        request?.httpBody = params?.percentEncoded()
        request?.timeoutInterval = 60
        
        if let r = request {
            let req = AF.request(r)
            performAlamofire(dataRequest: req, responseType: type, requestId: requestId, completion: completion)
        } else {
            completion(requestId, nil, "Data Corruption")
        }*/
        
        
        var request: DataRequest
        
        do {
            var urlRequest = try URLRequest(url: url, method: method, headers: .init(headers ?? [String:String]()))
            urlRequest.cachePolicy = .reloadIgnoringCacheData // <<== Cache disabled
            let encodedURLRequest = try URLEncoding.default.encode(urlRequest, with: params)
            
            request = AF.request(encodedURLRequest)
            
        } catch {
            // TODO: find a better way to handle error
            print(error)
            request = AF.request(URLRequest(url: URL(string: "http://example.com/wrong_request")!))
        }
        
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
            completion(.init(requestId: requestId, data: nil, error: "URL Corruption", responseCode: -1))
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
        
        let request = AF.request(urlRequest)
        performAlamofire(dataRequest: request, responseType: type, requestId: requestId, completion: completion)
        
    }
    
    private static func performAlamofire<T>(dataRequest : DataRequest, responseType type: T.Type, requestId: String, completion: @escaping OnResponseReady<T>) where T : Codable {
        
        dataRequest
            .validate()
            .responseJSON { response in
                handleReponse(response: response, responseType: type, requestId: requestId, completion: completion)
        }
    }
    
    private static func handleReponse<T>(response: AFDataResponse<Any>, responseType type: T.Type, requestId: String, completion: @escaping OnResponseReady<T>) where T : Codable {
        
        let code = response.response?.statusCode ?? 0
        
        switch response.result {
        case .success(let result):
            Log.p("response -> requestId: \(requestId)")
            Log.p(result)
            
            do {
                let data = try JSONDecoder().decode(type, from: response.data!)
                completion(.init(requestId: requestId, data: data, error: "", responseCode: code))
                return
            } catch let e {
                Log.p(".")
                Log.p("--\"JSON\"--")
                Log.p("response esrror -> requestId: \(requestId)")
                
                let jsonString = String(data: json ?? Data(), encoding: .utf8) ?? ""
                Log.p(jsonString)
                
                Log.p(".")
                Log.p(e)
                
                completion(.init(requestId: requestId, data: nil, error: "Error: Couldn't read response, refresh", responseCode: code))
            }
            
        case .failure(let error):
            Log.p("Error while fetching: \(String(describing: error)), requestId: \(requestId)")
            
            var error2 = error.localizedDescription
            
            if response.response?.statusCode == 405 {
                Log.p("response code 405: check your quota")
                error2 = error2 + "\nThe problem may return to your Connection Quota"
            }
            
            completion(.init(requestId: requestId, data: nil, error: error2, responseCode: code))
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
            completion(.init(requestId: requestId, data: nil, error: "URL Corruption", responseCode: -1))
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
        
        var urlRequest = try? URLRequest(url: url, method: .post, headers: .init(headers ?? [String:String]()))
        urlRequest?.timeoutInterval = 200
        
        guard urlRequest != nil else {
            completion(.init(requestId: requestId, data: nil, error: "Unknown Error", responseCode: -1))
            return
        }
        
        let uploadRequest = AF.upload(multipartFormData: multipartFormData, with: urlRequest!)
        
        uploadRequest.responseJSON { (response) in
            switch response.result {
            case .success(let value):
                Log.p(value)
                handleReponse(response: response, responseType: type, requestId: requestId, completion: completion)
                
            case .failure(let error):
                Log.p("requestId: \(requestId)")
                Log.p(error)
                
                let code = response.response?.statusCode ?? 0
                completion(.init(requestId: requestId, data: nil, error: error.failureReason ?? ("Unknown error: \(error.responseCode ?? 0)"), responseCode: code))
            }
        }
        
    }
    
    static func dowloadFile(url: String, progress: ((Double) -> Void)? = nil, completion: @escaping (URL?/*file path*/) -> Void) {
        let destination = DownloadRequest.suggestedDownloadDestination(for: .documentDirectory)

        AF.download(
            url,
            method: .get,
            parameters: nil,
            encoding: JSONEncoding.default,
            headers: nil,
            to: destination).downloadProgress(closure: { (prgrs) in
                progress?(prgrs.fractionCompleted)
                
            }).response(completionHandler: { (downloadResponse) in
                completion(downloadResponse.fileURL)
                //completion(downloadResponse.temporaryURL)
            })
    }
    
}

extension Dictionary {
    func percentEncoded() -> Data? {
        return map { key, value in
            let escapedKey = "\(key)".addingPercentEncoding(withAllowedCharacters: .urlQueryValueAllowed) ?? ""
            let escapedValue = "\(value)".addingPercentEncoding(withAllowedCharacters: .urlQueryValueAllowed) ?? ""
            return escapedKey + "=" + escapedValue
        }
        .joined(separator: "&")
        .data(using: .utf8)
    }
}

extension CharacterSet {
    static let urlQueryValueAllowed: CharacterSet = {
        let generalDelimitersToEncode = ":#[]@" // does not include "?" or "/" due to RFC 3986 - Section 3.4
        let subDelimitersToEncode = "!$&'()*+,;="
        
        var allowed = CharacterSet.urlQueryAllowed
        allowed.remove(charactersIn: "\(generalDelimitersToEncode)\(subDelimitersToEncode)")
        return allowed
    }()
}

/*extension AF.SessionManager{
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
}*/
