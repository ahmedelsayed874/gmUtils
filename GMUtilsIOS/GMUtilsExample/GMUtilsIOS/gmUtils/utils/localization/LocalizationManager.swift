//
//  LocalizationManager.swift
//  EasyIn
//
//  Created by GloryMaker on 4/14/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import UIKit

//https://www.codebales.com/how-programmatically-change-app-language-without-restarting-app
//https://medium.com/@abhimuralidharan/method-swizzling-in-ios-swift-1f38edaf984f

class LocalizationManager {
    
    static var currentLanguage: String {
        get {
            return UserDefaults.standard.string(forKey: "AppleLanguage") ?? "en"
        }
    }
    
    static func initLanguage() {
        UserDefaults.standard.set(currentLanguage, forKey: "AppleLanguage")
        Bundle.swizzleLocalization()
    }
    
    
}

extension Bundle {
    static func swizzleLocalization() {
        let orginalSelector = #selector(localizedString(forKey:value:table:))
        guard let orginalMethod = class_getInstanceMethod(self, orginalSelector) else { return }

        let mySelector = #selector(myLocaLizedString(forKey:value:table:))
        guard let myMethod = class_getInstanceMethod(self, mySelector) else { return }

        if class_addMethod(self, orginalSelector, method_getImplementation(myMethod), method_getTypeEncoding(myMethod)) {
            class_replaceMethod(self, mySelector, method_getImplementation(orginalMethod), method_getTypeEncoding(orginalMethod))
        } else {
            method_exchangeImplementations(orginalMethod, myMethod)
        }
    }

    @objc private func myLocaLizedString(forKey key: String,value: String?, table: String?) -> String {
        guard let appDelegate = UIApplication.shared.delegate as? AppDelegate,
            let bundlePath = Bundle.main.path(forResource: LocalizationManager.currentLanguage, ofType: "lproj"),
            let bundle = Bundle(path: bundlePath) else {
                return Bundle.main.myLocaLizedString(forKey: key, value: value, table: table)
        }
        return bundle.myLocaLizedString(forKey: key, value: value, table: table)
    }
}

class x {
    
    func d() -> String {
        let localizedTextKey = "hello"
        guard let bundlePath = Bundle.main.path(forResource: "he", ofType: "lproj"), let bundle = Bundle(path: bundlePath) else {
            return NSLocalizedString(localizedTextKey, comment: "")
        }
        return NSLocalizedString(localizedTextKey, tableName: nil, bundle: bundle, comment: "")

    }
}
