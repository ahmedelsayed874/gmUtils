//
//  PDFUtilities.swift
//  Ahmed El-Sayed
//
//  Created by GloryMaker on 5/11/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import UIKit
import WebKit
import PDFKit


class PDFUtilities {
    
    static func createFilePath(fileName: String) -> URL {
        let fm = FileManager.default
        let docsurl = try! fm.url(
            for: .documentDirectory, in: .userDomainMask,
            appropriateFor: nil, create: true)
        return docsurl.appendingPathComponent("\(fileName).pdf")
    }
    
    static func downloadFile(url: URL, fileName: String, callback: @escaping (URL?, Bool) -> Void) {
        let sess = URLSession.shared
        sess.downloadTask(with: url) { (url, resp, err) in
            if let url = url {
                let filePath = PDFUtilities.createFilePath(fileName: fileName)
                
                let fm = FileManager.default
                try? fm.removeItem(at: filePath)
                try? fm.moveItem(at: url, to: filePath)
                
                DispatchQueue.main.async {
                    callback(filePath, true)
                }
            } else {
                DispatchQueue.main.async {
                    callback(nil, false)
                }
            }
        }.resume()
    }
    
    //-----------------------------------------------------------------------------------------------------------
    
    static func displayIntoWebView(from: URL, inside view: UIView) -> Any {
//        if #available(iOS 12.0, *) {
            let frame = CGRect(origin: .init(x: 0, y: 0), size: view.frame.size)
            let webView = WKWebView(frame: frame)
            view.addSubview(webView)
            return PDFUtilities.displayIntoWebView(from: from, webview: webView)

//        } else {
//            let frame = CGRect(origin: .init(x: 0, y: 0), size: view.frame.size)
//            let webView = UIWebView(frame: frame)
//            view.addSubview(webView)
//            return PDFUtilities.displayIntoWebView(from: from, webview: webView)
//        }
    }
    
//    @available(iOS, introduced: 2.0, deprecated: 12.0)
//    static func displayIntoWebView(from: URL, webview: UIWebView) -> UIWebView {
//        let request = URLRequest.init(url: from)
//        webview.loadRequest(request)
//        return webview
//    }
    
    static func displayIntoWebView(from: URL, webview: WKWebView) -> WKWebView {
        let request = URLRequest.init(url: from)
        webview.load(request)
        return webview
    }

    static func displayIntoPDFView(from: URL, inside view: UIView) {
        let pdfview = PDFView(frame: view.bounds)
        pdfview.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        pdfview.autoScales = true
        view.addSubview(pdfview)
        
        let doc = PDFDocument(url: from)
        pdfview.document = doc
    }
}

