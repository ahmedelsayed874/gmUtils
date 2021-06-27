//
//  ImageLoader.swift
//  Choueifat
//
//  Created by Imac on 9/4/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import Foundation
import Kingfisher
import Alamofire

class ImageLoader {
    static var instance: ImageLoader = {
        return ImageLoader()
    }()
    
    private class Request {
        var url: String
        var imageView: UIImageView?
        var successImageMode: UIView.ContentMode?
        var failureImageMode: UIView.ContentMode?
        var onComplete: ((UIImageView, String, Bool)-> Void)?
        
        init(url: String,
             imageView: UIImageView,
             successImageMode: UIView.ContentMode? = nil,
             failureImageMode: UIView.ContentMode? = nil,
             onComplete: ((UIImageView, String, Bool)-> Void)? = nil) {
            
            self.url = url
            self.imageView = imageView
            self.successImageMode = successImageMode
            self.failureImageMode = failureImageMode
            self.onComplete = onComplete
        }
        
        func dispose() {
            self.imageView = nil
            self.successImageMode = nil
            self.failureImageMode = nil
            self.onComplete = nil
        }
    }
    
    private static var requests = [String: [Request]?]()
    
    
    //---------------------------------------------------------------------------------------------------
    
    ///return true if not the first registration
    private func registerRequest(url : String,
                                 imageView: UIImageView,
                                 placeHolderImage : UIImage? = nil,
                                 successImageMode: UIView.ContentMode? = nil,
                                 failureImageMode: UIView.ContentMode? = nil,
                                 onComplete: ((UIImageView, String, Bool)-> Void)? = nil) -> Bool {
        
        let request = Request(
            url: url,
            imageView: imageView,
            successImageMode: successImageMode,
            failureImageMode: failureImageMode,
            onComplete: onComplete
        )
        
        //print("ImageLoader", "REQUEST: \(url)")
        
        var requests: [Request]?? = ImageLoader.requests[url]
        if requests == nil {
            requests = [Request]()
            //print("ImageLoader", "IS_NEW")
            
        } else {
            imageView.image = placeHolderImage
            imageView.kf.indicatorType = .activity
            //print("ImageLoader", "ALREADY_REGISTERED")
        }
        
        requests!!.append(request)
        ImageLoader.requests[url] = requests
        
        return requests!!.count > 1
    }
    
    private func handleRequests(url : String, image: UIImage?, isSuccess: Bool) {
        //print("ImageLoader", "RESULT-HANDLED-FOR: \(url)")
        
        ImageLoader.requests[url]??.forEach({ (request) in
            //print("ImageLoader", "RESULT-HANDLED-FOR: iv=\(request.imageView?.hash ?? 0)")
            request.imageView?.image = image
            
            if isSuccess {
                if request.successImageMode != nil {
                    request.imageView?.contentMode = request.successImageMode!
                }
            } else {
                if request.failureImageMode != nil {
                    request.imageView?.contentMode = request.failureImageMode!
                }
            }
            
            request.onComplete?(request.imageView!, request.url, isSuccess)
            
            request.dispose()
        })
        
        ImageLoader.requests.removeValue(forKey: url)
    }
    
    //---------------------------------------------------------------------------------------------------
    
    func load(url : String?, imageView: UIImageView, placeHolderImage : UIImage? = nil,
                     initImageMode: UIView.ContentMode? = nil,
                     successImageMode: UIView.ContentMode? = nil,
                     failureImageMode: UIView.ContentMode? = nil,
                     onComplete: ((UIImageView, String, Bool)-> Void)? = nil) {
        
        guard let url = url, !url.isEmpty else {
            imageView.image = placeHolderImage
            if failureImageMode != nil {
                    imageView.contentMode = failureImageMode!
            }
            onComplete?(imageView, "", false)
            return
        }
        
        Log.p(".......")
        Log.p("Getting Image from: \(url)")
        
        if initImageMode != nil {
            imageView.contentMode = initImageMode!
        }
        
        if registerRequest(url: url, imageView: imageView, successImageMode: successImageMode, failureImageMode: failureImageMode, onComplete: onComplete) {
            return
        }
        
        let urlObj = URL(string: url)
        let processor = DownsamplingImageProcessor(size: imageView.frame.size)
            |> RoundCornerImageProcessor(cornerRadius: 0)
        imageView.kf.indicatorType = .activity
        imageView.kf.setImage(
            with: urlObj,
            placeholder: placeHolderImage,
            options: [
                .processor(processor),
                .scaleFactor(UIScreen.main.scale),
                .transition(.fade(1)),
                .cacheOriginalImage
            ], completionHandler: { result in
                
                let image = imageView.image
                
                var succeeded: Bool = true
                
                switch result {
                case .success( _):
                    succeeded = true
                    
                case .failure( _):
                    succeeded = false
                }
                
                self.handleRequests(url: url, image: image, isSuccess: succeeded)
                
            }
        )
        
    }
    
    func load(url : String?, imageView: UIImageView) {
        guard let url = url else { return  }
        
        Log.p(".......")
        Log.p("Getting Image from: \(url)")
        
        let urlObj = URL(string: url)
        
        if registerRequest(url: url, imageView: imageView) {
            return
        }
        
        imageView.kf.setImage(with: urlObj, placeholder: nil, options: nil, progressBlock: nil) { (result) in
            
            let image = imageView.image
            
            var succeeded: Bool = true
            
            switch result {
            case .success( _):
                succeeded = true
                
            case .failure( _):
                succeeded = false
            }
            
            self.handleRequests(url: url, image: image, isSuccess: succeeded)
        }
    }
    
    func loadByURLSession(url: String?, imageView: UIImageView) {
        guard let url = url else { return  }
        
        Log.p(".......")
        Log.p("Getting Image from: \(url)")
        
        if let urlObj = URL(string: url) {
            
            if registerRequest(url: url, imageView: imageView) {
                return
            }
            
            let request = URLRequest(url: urlObj)
            URLSession.shared.dataTask(with: request) {(data,response,error) in
                var image: UIImage? = nil
                
                if let imageData = data as Data? {
                    if let img = UIImage(data: imageData) { image = img }
                }
                
                DispatchQueue.main.async {
                    self.handleRequests(url: url, image: image, isSuccess: image != nil)
                }
            }
        }
    }
    
    //https://solarianprogrammer.com/2017/05/02/swift-alamofire-tutorial-uploading-downloading-images/
    func loadByAlamofire(url: String?, imageView: UIImageView) {

        guard let url = url else { return  }
        let remoteImageURL = URL(string: url)!
        
        Log.p(".......")
        Log.p("Getting Image from: \(url)")
        
        if registerRequest(url: url, imageView: imageView) {
            return
        }
        
        AF.request(remoteImageURL).responseData { (response) in
            var image: UIImage? = nil
            
            if response.error == nil {
                if let data = response.data {
                    image = UIImage(data: data)
                }
            }
            
            self.handleRequests(url: url, image: image, isSuccess: image != nil)
        }
    }
    
}
