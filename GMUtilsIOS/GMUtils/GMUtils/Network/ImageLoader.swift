//
//  ImageLoader.swift
//  Ahmed El-Sayed
//
//  Created by Imac on 9/4/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import Foundation
import Kingfisher
import Alamofire

class ImageLoader {
    
    static func load(url : String?, imageView: UIImageView) {
        guard let url = url else { return  }
        
        Log.p(".......")
        Log.p("Getting Image from: \(url)")
        
        let urlObj = URL(string: url)
        imageView.kf.setImage(with: urlObj)
    }
    
    static func load(url : String?, imageView: UIImageView, placeHolderImage : UIImage? = nil,
                     initImageMode: UIView.ContentMode? = nil,
                     successImageMode: UIView.ContentMode? = nil,
                     failureImageMode: UIView.ContentMode? = nil,
                     onComplete: ((UIImageView, String, Bool)-> Void)? = nil) {
        
        guard let url = url else {
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
            ])
        {
            result in
            var succeeded = true
            
            switch result {
            case .success( _):
                    if successImageMode != nil {
                        imageView.contentMode = successImageMode!
                }
            case .failure( _):
                    succeeded = false
                    if failureImageMode != nil {
                            imageView.contentMode = failureImageMode!
                    }
            }
            
            onComplete?(imageView, url, succeeded)
        }
        
    }
    
    static func loadByURLSession(url: String?, imageView: UIImageView) {
        guard let url = url else { return  }
        
        Log.p(".......")
        Log.p("Getting Image from: \(url)")
        
        if let urlObj = URL(string: url) {
            let request = URLRequest(url: urlObj)
            URLSession.shared.dataTask(with: request) {(data,response,error) in
                if let imageData = data as Data? {
                    if let img = UIImage(data: imageData){
                        DispatchQueue.main.async {
                            imageView.image = img
                        }
                    }
                }
            }
        }
    }
    
    func loadByDispatchQueue(url: String?, imageView: UIImageView) {
        
        guard let url = url else { return  }
        let urlObj = URL(string: url)!
        
        Log.p(".......")
        Log.p("Getting Image from: \(url)")
        
        DispatchQueue.global(qos: .userInitiated).async {
            do{
                let imageData: Data = try Data(contentsOf: urlObj)
                
                DispatchQueue.main.async {
                    let image = UIImage(data: imageData)
                    imageView.image = image
                    imageView.sizeToFit()
                }
            }catch{
                Log.p("Unable to load data: \(error)")
            }
        }
    }
    
    //https://solarianprogrammer.com/2017/05/02/swift-alamofire-tutorial-uploading-downloading-images/
    static func loadByAlamofire(url: String?, imageView: UIImageView) {

        guard let url = url else { return  }
        let remoteImageURL = URL(string: url)!
        
        Log.p(".......")
        Log.p("Getting Image from: \(url)")
        
        AF.request(remoteImageURL).responseData { (response) in
            if response.error == nil {
                Log.p(response.result)

                // Show the downloaded image:
                if let data = response.data {
                    imageView.image = UIImage(data: data)
                }
            }
        }
    }
    
}
