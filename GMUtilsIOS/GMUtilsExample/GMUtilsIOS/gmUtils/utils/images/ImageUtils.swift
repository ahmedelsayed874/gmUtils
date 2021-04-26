//
//  ImageUtils.swift
//  EasyIn
//
//  Created by GloryMaker on 4/16/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import UIKit

class ImageUtils {
    
    static func resizeImage(image: UIImage, maxDimensionLength: CGFloat = 1500) -> UIImage {
        let size = image.size
        let length = maxDimensionLength
        
        var newSize: CGSize
        
        if (size.width * size.height) > (length * length) {
            var nW: CGFloat
            var nH: CGFloat

            if (size.width > size.height) {
                nW = length;
                nH = size.height / size.width * length;
            } else {
                nW = size.width / size.height * length;
                nH = length;
            }
            
            newSize = CGSize(width: nW, height: nH)
        } else {
            return image
        }
        
        // This is the rect that we've calculated out and this is what is actually used below
        let rect = CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height)

        // Actually do the resizing to the rect using the ImageContext stuff
        UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
        image.draw(in: rect)
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return newImage!
    }

    static func resizeImage(image: UIImage, targetSize: CGSize) -> UIImage {
        let size = image.size
        
        let widthRatio  = targetSize.width  / size.width
        let heightRatio = targetSize.height / size.height

        // Figure out what our orientation is, and use that to form the rectangle
        var newSize: CGSize
        if(widthRatio > heightRatio) {
            newSize = CGSize(width: size.width * heightRatio, height: size.height * heightRatio)
        } else {
            newSize = CGSize(width: size.width * widthRatio,  height: size.height * widthRatio)
        }

        // This is the rect that we've calculated out and this is what is actually used below
        let rect = CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height)

        // Actually do the resizing to the rect using the ImageContext stuff
        UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
        image.draw(in: rect)
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return newImage!
    }
            
    static func saveImage(image: UIImage) -> URL? {
        let documentDirectoryPath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0] as NSString

        let imgPath = URL(fileURLWithPath: documentDirectoryPath.appendingPathComponent("\(Date().timeIntervalSince1970.description).png"))

        let imgPNGDate = image.pngData()
            
        do {
            try imgPNGDate?.write(to: imgPath, options: .atomic)
            return imgPath
        } catch {
            Log.p(error.localizedDescription)
            return nil
        }
    }
    
}
