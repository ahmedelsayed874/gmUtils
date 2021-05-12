//
//  ValidationChecker.swift
//  Ahmed El-Sayed
//
//  Created by Imac on 3/2/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import Foundation


class ValidationChecker {
    
    func isEmailValid(_ email: String) -> Bool {
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"

        let emailPred = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
        return emailPred.evaluate(with: email)
    }
    
    func isPasswordValid(_ password: String) -> Bool {
        let pw = password.trimmingCharacters(in: .whitespacesAndNewlines)
        return pw.count > 5
    }
    
    func isNameValid(_ name: String) -> Bool {
        return name.count >= 3
    }
    
    func isFullNameValid(_ name: String) -> Bool {
        var valid = false
        
        let parts = name.split(separator: " ")
        
        if parts.count > 1 {
            valid = true
            
            for p in parts {
                if p.count < 3 {
                    valid = false
                }
            }
        }
        
        return valid
    }
    
    func isPhoneNumberValid(_ phone: String) -> Bool {
        let phoneRegex = "^[0-9+]{0,1}+[0-9]{5,16}$"
        let phoneTest = NSPredicate(format: "SELF MATCHES %@", phoneRegex)
        return phoneTest.evaluate(with: phone)
    }
}
