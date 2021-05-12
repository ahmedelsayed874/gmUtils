//
//  Log.swift
//  Ahmed El-Sayed
//
//  Created by Imac on 2/26/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import Foundation

class Log {
    
    static var inDebugMode : Bool {
        get {
            let expDate = try! DateOp().parse(date: "25-04-2021 00:00:00", dateFormatPattern: DateOp.FORMAT_PATTERN_dd_MM_yyyy_HH_mm_ss)
            
            return Date() < expDate
        }
    }
    
    static func p(_ m: Any...) {
        if inDebugMode { print(m) }
    }
    
}
