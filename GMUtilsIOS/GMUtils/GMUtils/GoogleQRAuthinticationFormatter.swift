//
//  BarcodeGenerator.swift
//  Ahmed El-Sayed
//
//  Created by Imac on 3/19/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import UIKit

class GoogleQRAuthinticationFormatter {
    
    private var type: String = "totp" // or hotp

    private var secret: String
    private var label: String  //username
    private var issuer: String //appname
    
    private var algorithm: String
    private var digits: Int
    private var period: Int
    
    
    init(secret: String, userName: String, appName: String, algorithm: String, digits: Int, period: Int) {
        self.secret = secret
        self.label = userName
        self.issuer = appName
        
        self.algorithm = algorithm
        self.digits = digits
        self.period = period
    }
    
    
    /**
     * @return The URI/message to encode into the QR image, in the format specified here:
     * https://github.com/google/google-authenticator/wiki/Key-Uri-Format
     * otpauth://TYPE/LABEL?PARAMETERS
     *
     * otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example
     * otpauth://totp/ACME%20Co:john.doe@email.com?secret=HXDMVJECJJWSRB3HWIZR4IFUGFTMXBOZ&issuer=ACME%20Co&algorithm=SHA1&digits=6&period=30
     */
    public func getQRText() -> String {
        let url = "otpauth://" +
            uriEncode(type, .urlHostAllowed) + "/" +
            uriEncode(label, .urlHostAllowed) + "?" +
            "secret=" + uriEncode(secret, .urlQueryAllowed) +
            "&issuer=" + uriEncode(issuer, .urlQueryAllowed) +
            "&algorithm=" + uriEncode(algorithm, .urlQueryAllowed) +
            "&digits=" + "\(digits)" +
            "&period=" + "\(period)"
        
        return url
    }
    
    private func uriEncode(_ text: String?, _ charset: CharacterSet) -> String {
        if (text == nil) {  return "";  }

        let result = text!.addingPercentEncoding(withAllowedCharacters: charset)

        return result!
    }
    
}
