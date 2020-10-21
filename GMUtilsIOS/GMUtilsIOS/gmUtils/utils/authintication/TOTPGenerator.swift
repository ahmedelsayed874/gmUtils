//
//  TOTPGenerator.swift
//  EasyIn
//
//  Created by Imac on 2/12/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import UIKit
/*import SwiftOTP

 //pod 'SwiftOTP'
//https://github.com/lachlanbell/SwiftOTP
class TOTPGenerator: NSObject {
    public static var algorithm = OTPAlgorithm.sha1
    public static var codeLength = 8
    public static var timeInterval = 30
    
    private let totp : TOTP
    
    init(secret: String) {
        let data = base32DecodeToData(secret)!
        //let secret = secret.data(using: .utf8)!
        
        totp = TOTP(
            secret: data,
            digits: TOTPGenerator.codeLength,
            timeInterval: TOTPGenerator.timeInterval,
            algorithm: TOTPGenerator.algorithm)!
    }
    
    public func generatePassword() -> String {
        return totp.generate(time: Date())!
    }
    
    public func getRemainTime() -> Int {
        let time = Date()
        let intervalsCount = Int(time.timeIntervalSince1970 / Double(TOTPGenerator.timeInterval))
        let nextIntervalStart = intervalsCount * TOTPGenerator.timeInterval + TOTPGenerator.timeInterval
        let diffenceSecond = nextIntervalStart - Int(time.timeIntervalSince1970)
        
        return diffenceSecond
    }
    
    override class func description() -> String {
        return "Time Interval: \(timeInterval), Code Length: \(codeLength), Algorithm: \(algorithm)"
    }
    
    public static func algorithmName() -> String {
        switch algorithm {
            case .sha256: return "SHA256"
            case .sha512: return "SHA512"
            default: return "SHA1"
        }
    }
}*/
