//
//  LanguagePreference.swift
//  Ahmed El-Sayed
//
//  Created by GloryMaker on 4/16/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import Foundation

class LanguagePreference {
    
    let file = UserDefaults(suiteName: "LanguagePreference") ?? UserDefaults.standard
    
    func setSavedFor(userId: Int) {
        file.set(userId, forKey: "userid")
    }
    
    func isSavedFor(userId: Int) -> Bool {
        return file.integer(forKey: "userid") == userId
    }
}
