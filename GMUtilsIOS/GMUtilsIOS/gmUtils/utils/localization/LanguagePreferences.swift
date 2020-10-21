//
//  LanguagePreferences.swift
//  EasyIn
//
//  Created by GloryMaker on 4/14/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import Foundation
import LanguageManager_iOS //https://github.com/Abedalkareem/LanguageManager-iOS

class LanguagePreferences {
    private static let DATASET_NAME = String(describing: UIApplication.shared)
    private static let LANGUAGE_KEY = "PREFERENCE_LANGUAGE"
    
    
    static var currentLanguage: Languages {
        get {
            return LanguageManager.shared.currentLanguage
        }
        set(value) {
            LanguageManager.shared.currentLanguage = value
        }
    }
    
    static func applyLanguage() {
        if currentLanguage == .ar {
            LanguageManager.shared.defaultLanguage = .ar
        } else {
            LanguageManager.shared.defaultLanguage = .en
        }
    }
    
    static var isArabic: Bool {
        get {
            return LanguageManager.shared.isRightToLeft
        }
    }
    
//    static var isArabic2: Bool {
//        get {
//            return StringResource.Language == "ar"
//        }
//    }
    
    static func changeLanguageRuntime(en: Bool) {
        
        let selectedLanguage: Languages = en ? .en : .ar
        
        // change the language.
        LanguageManager.shared.setLanguage(language: selectedLanguage,
                                           viewControllerFactory: { title -> UIViewController in
                                            // you can check the title to set a specific for specific scene.
                                            print(title ?? "")
                                            // get the storyboard.
                                            let storyboard = UIStoryboard(name: "Main", bundle: nil)
                                            // instantiate the view controller that you want to show after changing the language.
                                            return storyboard.instantiateInitialViewController()!
        }) { view in
            // do custom animation
            view.transform = CGAffineTransform(scaleX: 2, y: 2)
            view.alpha = 0
        }
    }
    
}
