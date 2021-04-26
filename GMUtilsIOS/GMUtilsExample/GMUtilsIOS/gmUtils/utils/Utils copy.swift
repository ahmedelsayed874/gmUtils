//
//  Utils.swift
//  EasyIn
//
//  Created by Imac on 3/18/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import UIKit
import WebKit


class Utils {
    
    static func convertArabicNumbersToEnglishNumber(_ text: String) -> String {
        return text
            .replacingOccurrences(of: "٠", with: "0")
            .replacingOccurrences(of: "١", with: "1")
            .replacingOccurrences(of: "٢", with: "2")
            .replacingOccurrences(of: "٣", with: "3")
            .replacingOccurrences(of: "٤", with: "4")
            .replacingOccurrences(of: "٥", with: "5")
            .replacingOccurrences(of: "٦", with: "6")
            .replacingOccurrences(of: "٧", with: "7")
            .replacingOccurrences(of: "٨", with: "8")
            .replacingOccurrences(of: "٩", with: "9")
    }
    
    static func convertEnglishNumbersToArabicNumber(_ text: String) -> String {
        return text
            .replacingOccurrences(of: "0", with: "٠")
            .replacingOccurrences(of: "1", with: "١")
            .replacingOccurrences(of: "2", with: "٢")
            .replacingOccurrences(of: "3", with: "٣")
            .replacingOccurrences(of: "4", with: "٤")
            .replacingOccurrences(of: "5", with: "٥")
            .replacingOccurrences(of: "6", with: "٦")
            .replacingOccurrences(of: "7", with: "٧")
            .replacingOccurrences(of: "8", with: "٨")
            .replacingOccurrences(of: "9", with: "٩")
    }
    
    static func formatNumber(number: Double) -> String {
        let numberFormatter = NumberFormatter()
        numberFormatter.numberStyle = .decimal
        numberFormatter.groupingSize = 3
        numberFormatter.maximumFractionDigits = 2
        numberFormatter.minimumFractionDigits = 2
        let formattedNumber = numberFormatter.string(from: NSNumber(value:number))
        return formattedNumber ?? "\(number)"
    }
    
    static func parseHTML(html: String) throws -> NSAttributedString {
        guard let data = html.data(using: .utf8) else { fatalError("Can't get data from sent text") }
        do {
            return try NSAttributedString(
                data: data,
                options: [
                    .documentType: NSAttributedString.DocumentType.html,
                    .characterEncoding: String.Encoding.utf8.rawValue],
                documentAttributes: nil)
        } catch {
            fatalError("Can't convert plain text to html \(error)")
        }
    }
    
    static func tryParseHTMLString(html: String, intoTextView: UITextView?) {
        if intoTextView == nil { return }
        
        do {
            try intoTextView?.attributedText = parseHTML(html: html)
        } catch {
            intoTextView?.text = html
            .replacingOccurrences(of: "<p>", with: "\n")
            .replacingOccurrences(of: "</p>", with: "\n")
                
            .replacingOccurrences(of: "<h1>", with: "\n")
            .replacingOccurrences(of: "</h1>", with: "\n")
                
            .replacingOccurrences(of: "<h2>", with: "\n")
            .replacingOccurrences(of: "</h2>", with: "\n")
                
            .replacingOccurrences(of: "<h3>", with: "\n")
            .replacingOccurrences(of: "</h3>", with: "\n")
                
            .replacingOccurrences(of: "<h4>", with: "\n")
            .replacingOccurrences(of: "</h4>", with: "\n")
                
            .replacingOccurrences(of: "<h5>", with: "\n")
            .replacingOccurrences(of: "</h5>", with: "\n")
                
            .replacingOccurrences(of: "<h6>", with: "\n")
            .replacingOccurrences(of: "</h6>", with: "\n")
            
            
            .replacingOccurrences(of: "<br>", with: "")
            .replacingOccurrences(of: "<br />", with: "")
                
            .replacingOccurrences(of: "<strong>", with: "\"")
            .replacingOccurrences(of: "</strong>", with: "\"")

            .replacingOccurrences(of: "<b>", with: "\"")
            .replacingOccurrences(of: "</b>", with: "\"")

            .replacingOccurrences(of: "<i>", with: "\"")
            .replacingOccurrences(of: "</i>", with: "\"")
                                        
            .replacingOccurrences(of: "<strike>", with: "")
            .replacingOccurrences(of: "</strike>", with: "")
                
            .replacingOccurrences(of: "<del>", with: "")
            .replacingOccurrences(of: "</del>", with: "")
                
            .replacingOccurrences(of: "<s>", with: "")
            .replacingOccurrences(of: "</s>", with: "")
                
            .replacingOccurrences(of: "<small>", with: "")
            .replacingOccurrences(of: "</small>", with: "")
                
            .replacingOccurrences(of: "<q>", with: "\"")
            .replacingOccurrences(of: "</q>", with: "\"")
                
            .replacingOccurrences(of: "<center>", with: "")
            .replacingOccurrences(of: "<center>", with: "")
            
        }
        
        Utils.tryParseHTMLString(html: "", intoTextView: nil)
    }
    
    static func setBackgroundForStackView(color: UIColor, stackView: UIStackView?) {
        if stackView == nil { return }
        
        let subView = UIView(frame: stackView!.bounds)
        subView.backgroundColor = color
        subView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        stackView!.insertSubview(subView, at: 0)
        
        setBackgroundForStackView(color: color, stackView: nil)
    }
    
    static func clearWebViewCache() {
        let websiteDataTypes = NSSet(array: [WKWebsiteDataTypeDiskCache, WKWebsiteDataTypeMemoryCache])
        let date = Date(timeIntervalSince1970: 0)
        WKWebsiteDataStore.default().removeData(ofTypes: websiteDataTypes as! Set<String>, modifiedSince: date, completionHandler:{ })
    }
    
    static func appVersion() -> String? {
        let info = Bundle.main.infoDictionary?["CFBundleShortVersionString"]
        let version = info as? String
        return version
    }
    
    static func checkAppVersion(newVersion: String, currentVersion: String) -> Int {
        var newVer = newVersion.lowercased().replacingOccurrences(of: ".", with: "")
        var curVer = currentVersion.lowercased().replacingOccurrences(of: ".", with: "")
        
        let length = max(newVer.count, curVer.count)
        
        while newVer.count < length { newVer.append("0") }
        while curVer.count < length { curVer.append("0") }
        
        if Int(newVer) ?? 0 > Int(curVer) ?? 0 { return 1 }
        else if Int(newVer) ?? 0 < Int(curVer) ?? 0 { return -1 }
        else { return 0 }
        
    }
    
    static func openURL(url: String?) {
        if let _url = URL(string: url ?? "") {
            UIApplication.shared.open(_url, options: [:], completionHandler: nil)
        }
    }
    
    static func openAppleStore(appId: String) {
        Utils.openURL(url: "itms-apps://itunes.apple.com/app/\(appId)")
        //Utils.openURL(url: "itms://itunes.apple.com/de/app/x-gift/\(appId)?mt=8&uo=4")
        //Utils.openURL(url: "itms-apps://itunes.apple.com/developer/\(appId)")
        //itms-apps://itunes.apple.com/developer/id<developer-id>
        //itms-apps://geo.itunes.apple.com/developer/<developer-name>/id<developer-id>
        //itms-apps://itunes.apple.com/<store name>/developer/<developer name>/id<id number>
    }
    
    static func configureScrollViewToRTL(scrollview: UIScrollView, subview: UIView) {
        scrollview.transform = CGAffineTransform(rotationAngle: .pi);
        subview.transform =  CGAffineTransform(rotationAngle: .pi)
    }
    
}
