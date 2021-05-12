//
//  SharingHelper.swift
//  Ahmed El-Sayed
//
//  Created by GloryMaker on 3/12/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import UIKit

class SharingHelper {
    //https://developer.apple.com/documentation/uikit/uiactivityviewcontroller
    //https://nshipster.com/uiactivityviewcontroller/
    
    class SharingOption {
        let imageName: String
        let image: UIImage
        let destImageWidth: Int
        let destImageHeight: Int
        
        init(imageName: String, image: UIImage) {
            self.imageName = imageName
            self.image = image
            self.destImageWidth = Int(image.size.width)
            self.destImageHeight = Int(image.size.height)
        }
        
        init(imageName: String, image: UIImage, destImageWidth: Int, destImageHeight: Int) {
            self.imageName = imageName
            self.image = image
            self.destImageWidth = destImageWidth
            self.destImageHeight = destImageHeight
        }
    }
    
    static func tryShareSingleImageWithAlert(vc: BaseViewController, alertTitle: String?, alertMessage: String,  sharedText: String?, images: [SharingOption?]) {
        
        let sharedText: String? = nil //to prevent sharing text
        
        var c = 0
        
        images.forEach { (opt) in
            if opt != nil { c += 1 }
        }
        
        if c == 0 {
            vc.showErrorMessage(msg: StringResource.CantShareData)
            
        } else {
            if images.count == 1 {
                let img = ImageUtils.drawImage(image: images[0]!.image, destWidth: images[0]!.destImageWidth, destHeight: images[0]!.destImageHeight)
                SharingHelper.share(vc: vc, text: sharedText, image: img)
                
            } else {
                SharingHelper.shareSingleImageWithAlert(
                    vc: vc,
                    alertTitle: StringResource.visitRequest,
                    alertMessage: StringResource.doYouWantToShareQR_Barcode,
                    sharedText: sharedText,
                    images: images)
            }
        }
        
    }
    
    static func shareSingleImageWithAlert(vc: UIViewController, alertTitle: String?, alertMessage: String, sharedText: String?, images: [SharingOption?]) {
        
        var imgs: [String: SharingOption]? = [String: SharingOption]()
        
        images.forEach { (opt) in
            if let opt = opt {
                imgs?.updateValue(opt, forKey: opt.imageName)
            }
        }
        
        let handler: ((UIAlertAction) -> Void) = {action in
            guard let imgs = imgs else {
                return
            }
            let img = imgs[action.title!]
            let newImg = ImageUtils.drawImage(image: img!.image, destWidth: img!.destImageWidth, destHeight: img!.destImageHeight)
            SharingHelper.share(vc: vc, text: sharedText, image: newImg)
        }
        
        let alert = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)

        imgs?.forEach({ (k: String, v: SharingOption) in
            alert.addAction(UIAlertAction(title: v.imageName, style: .default, handler: handler))
        })
        
        alert.addAction(UIAlertAction(title: StringResource.all, style: .default, handler: { (action) in
            var imgList = [UIImage]()
            imgs?.forEach({ (key: String, value: SharingOption) in
                imgList.append(value.image)
            })
            
            SharingHelper.share(vc: vc, texts: nil, images: imgList, links: nil)
            
        }))
        
        alert.addAction(UIAlertAction(title: StringResource.Cancel, style: .cancel, handler: nil))
        
        vc.present(alert, animated: true, completion: nil)
        
    }
    
    
    //This app has crashed because it attempted to access privacy-sensitive data without a usage description.  The app's Info.plist must contain an NSPhotoLibraryAddUsageDescription key with a string value explaining to the user how the app uses this data.
    static func share(vc: UIViewController, texts: [String]? = nil, images: [UIImage]? = nil, links: [URL]? = nil) {
        var shareItems = [Any]()
        
        if let texts = texts {
            for text in texts {
                shareItems.append(text)
            }
        }
        if let images = images {
            for image in images {
                if let img = image.jpegData(compressionQuality: 1.0) {
                    shareItems.append(img)
                    
                } else if let ciImage = image.ciImage {
                    let i = ciImage.nonInterpolatedImage()
                    let d = i?.pngData()
                    shareItems.append(d as Any)
                }
            }
        }
        if let links = links {
            for link in links {
                shareItems.append(link)
            }
        }
        
        share(vc: vc, shareItems: shareItems)
    }
    
    static func share(vc: UIViewController, text: String? = nil, image: UIImage? = nil) {
        var shareItems = [Any]()
        
        //if text != nil { shareItems.append(text!) }
        if image != nil {
            if let img = image!.jpegData(compressionQuality: 1.0) {
                shareItems.append(img)
                
            } else if let ciImage = image!.ciImage {
                let i = ciImage.nonInterpolatedImage()
                let d = i?.pngData()
                shareItems.append(d as Any)
            }
        }
                
        share(vc: vc, shareItems: shareItems)
    }
    
    private static func share(vc: UIViewController, shareItems: [Any]) {
        
        if shareItems.isEmpty {
            return
        }
        
        let activityViewController = UIActivityViewController(activityItems: shareItems, applicationActivities: nil)
        activityViewController.setupForIPad()
        activityViewController.completionWithItemsHandler = {activityType, completed, returnedItems, activityError in
            print("--SHARING FEEDBACK:")
            print("> activityType: \(String(describing: activityType))")
            print("> completed: \(completed)")
            print("> returnedItems: \(String(describing: returnedItems))")
            print("> activityError \(activityError ?? "nil")")
        }
        
        vc.present(activityViewController, animated: true, completion: nil)
    }
    
    
    //-------------------------------------------------------------------------------------------------------------------
    
    
    //https://faq.whatsapp.com/en/iphone/23559013
    /**
     add this to [info.plist] file:
     <key>LSApplicationQueriesSchemes</key>
     <array>
     <string>whatsapp</string>
     </array>
     
     */
    static func shareToWhatsapp(msg: String) {
        let urlWhats = "whatsapp://send?text=\(msg)"
        
        if let urlString = urlWhats.addingPercentEncoding(withAllowedCharacters: NSCharacterSet.urlQueryAllowed) {
            if let whatsappURL = NSURL(string: urlString) {
                if UIApplication.shared.canOpenURL(whatsappURL as URL) {
                    UIApplication.shared.open(whatsappURL as URL)
                } else {
                    print("please install watsapp")
                }
            }
        }
    }
    
    /*
     <key>LSApplicationQueriesSchemes</key>
     <array>
      <string>whatsapp</string>
     </array>
     */
    static func shareWithWhatsApp(image: UIImage) {
//        let urlWhats = "whatsapp://app"
//        if let urlString = urlWhats.addingPercentEncoding(withAllowedCharacters: NSCharacterSet.urlQueryAllowed) {
//            if let whatsappURL = URL(string: urlString) {
//                if UIApplication.shared.canOpenURL(whatsappURL) {
//                    if let imageData = image.jpegData(compressionQuality: 1.0) {
//                            let tempFile = NSURL(fileURLWithPath: NSHomeDirectory()).URLByAppendingPathComponent("Documents/whatsAppTmp.wai")
//                            do {
//                                try imageData.writeToURL(tempFile, options: .DataWritingAtomic)
//                                self.documentInteractionController = UIDocumentInteractionController(URL: tempFile)
//                                self.documentInteractionController.UTI = "net.whatsapp.image"
//                                self.documentInteractionController.presentOpenInMenuFromRect(CGRectZero, inView: self.view, animated: true)
//                            } catch {
//                                print(error)
//                            }
//                        }
//
//
//                } else {
//                    // Cannot open whatsapp
//                }
//            }
//        }
    }
    
    
    //-------------------------------------------------------------------------------------------------------------------
    
    
    
    static func shareImageAfterSaving(vc: UIViewController, image: UIImage, photoName: String) {
        guard let imageUrl = saveActivityControllerImage(image: image, photoName: "\(photoName).png") else { return }
        
        var sharingData:Array<NSURL> = [NSURL]()
        sharingData.append(imageUrl)
        
        let activityViewController:UIActivityViewController = UIActivityViewController(
            activityItems: sharingData as [Any],
            applicationActivities: nil)
        activityViewController.setupForIPad()
        
        vc.present(activityViewController, animated: true, completion: nil)
    }
    
    private static func saveActivityControllerImage(image: UIImage, photoName: String) -> NSURL? {
        
        var name:String = photoName.capitalized
        
        print(name.components(separatedBy: ".").last as Any)
        
        if name.components(separatedBy: ".").last!.lowercased() != "png" {
            name = name.replacingOccurrences(of: ".\(name.components(separatedBy: ".").last!)", with: ".jpg")
            
        } else {
            name = name.replacingOccurrences(of: ".\(name.components(separatedBy: ".").last!)", with: ".png")
        }
        
        let urlString : NSURL = getDocumentDirectoryPath(fileName: name)
        
        print("Image path : \(urlString)")
        
        if !FileManager.default.fileExists(atPath: urlString.absoluteString!) {
            var isSaved : Bool = false
            
            print(urlString.pathExtension as Any)
            
            if urlString.pathExtension?.lowercased() == "png" {
                do {
                    try image.pngData()?.write(to: urlString as URL, options: Data.WritingOptions.atomic)
                    isSaved = true
                } catch {
                    isSaved = false
                }
                
            } else {
                do {
                    try  image.jpegData(compressionQuality: 1.0)?.write(to: urlString as URL, options: Data.WritingOptions.atomic)
                    isSaved = true
                } catch {
                    isSaved = false
                }
            }
            
            if (isSaved) {
                return urlString
                
            } else {
                return nil
            }
        }
        
        return nil
    }
    
    private static func getDocumentDirectoryPath(fileName:String) -> NSURL {
        let paths:NSArray = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true) as NSArray
        
        let docuementDir:NSString = paths.object(at: 0) as! NSString
        
        return NSURL.fileURL(withPath: docuementDir.appendingPathComponent("Images/\(fileName)")) as NSURL
    }
    
}
