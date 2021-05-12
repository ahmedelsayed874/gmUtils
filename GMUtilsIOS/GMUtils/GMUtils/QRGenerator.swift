//
//  QRGenerator.swift
//  Ahmed El-Sayed
//
//  Created by Imac on 2/13/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import UIKit

class TextEncoder {
    
    class Barcode {
        private var text: String
        
        init(text: String) {
            self.text = text
        }
        
        //let image = generateBarcode(from: "Hacking with Swift")
        func generateBarcode() -> UIImage? {
            let data = self.text.data(using: String.Encoding.ascii)

            if let filter = CIFilter(name: "CICode128BarcodeGenerator") {
                filter.setValue(data, forKey: "inputMessage")
                let transform = CGAffineTransform(scaleX: 3, y: 3)

                if let output = filter.outputImage?.transformed(by: transform) {
                    return UIImage(ciImage: output)
                }
            }

            return nil
        }
    }
    
    
    class QRCode {
        private var text: String
        
        init(text: String) {
            self.text = text
        }
        
        //let image = generateQRCode(from: "Hacking with Swift is the best iOS coding tutorial I've ever read!")
        func generateQRCode() -> UIImage? {
            let uri = self.text
            let data = uri.data(using: String.Encoding.ascii)

            if let filter = CIFilter(name: "CIQRCodeGenerator") {
                
                filter.setValue(data, forKey: "inputMessage")
                
                let transform = CGAffineTransform(scaleX: 3, y: 3)

                if let output = filter.outputImage?.transformed(by: transform) {
                    return UIImage(ciImage: output)
                }
            }

            return nil
        }
    }
    
    class QRCodeWay2 {
        private var text: String
        
        init(text: String) {
            self.text = text
        }
        
        /// The QRCode's UIImage representation
        public func getQRUIImage(size: CGSize) -> UIImage? {
            guard let ciImage = getQRCIImage() else { return nil }
            
            // Size
            let ciImageSize = ciImage.extent.size
            let widthRatio = size.width / ciImageSize.width
            let heightRatio = size.height / ciImageSize.height
            
            return ciImage.nonInterpolatedImage(withScale: Scale(dx: widthRatio, dy: heightRatio))
        }
        
        /// The QRCode's CIImage representation
        public func getQRCIImage() -> CIImage? {
            let uri = self.text
            let data = uri.data(using: String.Encoding.ascii)
            
            // Generate QRCode
            guard let qrFilter = CIFilter(name: "CIQRCodeGenerator") else { return nil }
            
            qrFilter.setDefaults()
            qrFilter.setValue(data, forKey: "inputMessage")
            qrFilter.setValue(self.errorCorrection.rawValue, forKey: "inputCorrectionLevel")
            
            // Color code and background
            guard let colorFilter = CIFilter(name: "CIFalseColor") else { return nil }
            
            colorFilter.setDefaults()
            colorFilter.setValue(qrFilter.outputImage, forKey: "inputImage")
            colorFilter.setValue(color, forKey: "inputColor0")
            colorFilter.setValue(backgroundColor, forKey: "inputColor1")
            
            return colorFilter.outputImage
        }
        
        /// Foreground color of the output
        /// Defaults to black
        public var color = CIColor(red: 0, green: 0, blue: 0)
        
        /// Background color of the output
        /// Defaults to white
        public var backgroundColor = CIColor(red: 1, green: 1, blue: 1)
        
        /// The error correction. The default value is `.Low`.
        public var errorCorrection = ErrorCorrection.Low
        
        /**
        The level of error correction.
        
        - Low:      7%
        - Medium:   15%
        - Quartile: 25%
        - High:     30%
        */
        public enum ErrorCorrection: String {
            case Low = "L"
            case Medium = "M"
            case Quartile = "Q"
            case High = "H"
        }
    }
    
}

internal typealias Scale = (dx: CGFloat, dy: CGFloat)

internal extension CIImage {
    
    /// Creates an `UIImage` with interpolation disabled and scaled given a scale property
    ///
    /// - parameter withScale:  a given scale using to resize the result image
    ///
    /// - returns: an non-interpolated UIImage
    func nonInterpolatedImage(withScale scale: Scale = Scale(dx: 1, dy: 1)) -> UIImage? {
        guard let cgImage = CIContext(options: nil).createCGImage(self, from: self.extent) else { return nil }
        let size = CGSize(width: self.extent.size.width * scale.dx, height: self.extent.size.height * scale.dy)
        
        UIGraphicsBeginImageContextWithOptions(size, true, 0)
        guard let context = UIGraphicsGetCurrentContext() else { return nil }
        context.interpolationQuality = .none
        context.translateBy(x: 0, y: size.height)
        context.scaleBy(x: 1.0, y: -1.0)
        context.draw(cgImage, in: context.boundingBoxOfClipPath)
        let result = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return result
    }
}
