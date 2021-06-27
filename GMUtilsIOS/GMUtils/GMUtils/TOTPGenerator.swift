//
//  TOTPGenerator.swift
//  Ahmed El-Sayed
//

import UIKit
/*import SwiftOTP

//https://github.com/lachlanbell/SwiftOTP
class TOTPGenerator: NSObject {
    public static var defaultAlgorithm = OTPAlgorithm.sha1
    public static var defaultCodeLength = 8
    public static var defaultTimeInterval = 100 //sec
    
    private let totp : TOTP
    private let codeLength : Int
    private let _timeInterval: Int
    private let algorithm: OTPAlgorithm
    
    init(
        secret: String,
        algorithm: OTPAlgorithm = TOTPGenerator.defaultAlgorithm,
        codeLength : Int = TOTPGenerator.defaultCodeLength,
        timeInterval: Int
    ) {
        let data = base32DecodeToData(secret)!
        
        self.totp = TOTP(
            secret: data,
            digits: codeLength,
            timeInterval: timeInterval,
            algorithm: algorithm
        )!
        
        self.codeLength = codeLength
        self._timeInterval = timeInterval
        self.algorithm = algorithm
    }
    
    var timeInterval: Int { return _timeInterval }
    
    public func generatePassword() -> String {
        let date = Date()
        
//        if Log.inDebugMode {
//            Log.p("totp----------------")
//            Log.p("totp-time0: \(date.description)")
//            Log.p("totp-time0: \(Int(floor(date.timeIntervalSince1970)))")
//        }
        
        let secret = totp.generate(time: date)!
        
//        if Log.inDebugMode {
//            Log.p("totp-scrt-: \(secret)")
//            Log.p(".")
//        }
        
        return secret
    }
    
    public func getRemainTime() -> Int {
        let time = Date()
        let intervalsCount = Int(time.timeIntervalSince1970 / Double(timeInterval))
        let nextIntervalStart = intervalsCount * timeInterval + timeInterval
        let diffenceSecond = nextIntervalStart - Int(time.timeIntervalSince1970)
        
        return diffenceSecond
    }
    
    override var description: String {
        return "Time Interval: \(timeInterval), Code Length: \(codeLength), Algorithm: \(algorithm)"
    }
    
    public static func algorithmName(algorithm: OTPAlgorithm) -> String {
        switch algorithm {
            case .sha256: return "SHA256"
            case .sha512: return "SHA512"
            default: return "SHA1"
        }
    }
}*/
