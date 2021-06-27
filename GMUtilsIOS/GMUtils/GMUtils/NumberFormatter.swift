//
//  NumberFormatter.swift
//
//  Created by GloryMaker on 5/15/21.
//

import Foundation

class NumberFormatter {
    
    var numberStyle: Foundation.NumberFormatter.Style = .decimal
    var groupingSize: Int = 3
    
    func format(number: Double, fractionSize: Int = 2) -> String {
        let numberFormatter = Foundation.NumberFormatter()
        numberFormatter.numberStyle = numberStyle
        numberFormatter.groupingSize = groupingSize
        numberFormatter.minimumFractionDigits = fractionSize
        numberFormatter.maximumFractionDigits = fractionSize
        let formattedNumber = numberFormatter.string(from: NSNumber(value:number))
        return formattedNumber ?? "\(number)"
    }
    
    func format(_ num: Double) -> String {
        return format(number: Double(num))
    }
    
    func format(_ num: Float) -> String {
        return format(number: Double(num), fractionSize: 1)
    }
    
    func format(_ num: Int) -> String {
        return format(number: Double(num), fractionSize: 0)
    }
    
    func format(numString: String, integer: Bool) -> String {
        if (integer) {
            if let num = Int(numString) {
                return format(num)
            } else {
                return numString
            }
        } else {
            if let num = Double(numString) {
                return format(num)
            } else {
                return numString
            }
        }
    }
    
}
