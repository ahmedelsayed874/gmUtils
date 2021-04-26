//
//  KeychainService.swift
//  EasyIn
//
//  Created by Imac on 3/8/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import Foundation
import SwiftKeychainWrapper


/// https://cocoapods.org/pods/SwiftKeychainWrapper
/// https://medium.com/ios-os-x-development/securing-user-data-with-keychain-for-ios-e720e0f9a8e2

struct ServicePath {
    let serviceName: String
    let accessGroup: String
}

class KeychainService {
    let keychainWrapper: KeychainWrapper
    
    init(servicePath: ServicePath? = nil) {
        
        if let servicePath = servicePath {
            keychainWrapper = KeychainWrapper(serviceName: servicePath.serviceName, accessGroup: servicePath.accessGroup)
        } else {
            keychainWrapper = KeychainWrapper.standard
        }
    }
    
    func save(key: String, value: String) -> Bool {
        let saveSuccessful: Bool = keychainWrapper.set(value, forKey: key)
        return saveSuccessful
    }

    func retrieve(key: String) -> String? {
        let retrievedString: String? = keychainWrapper.string(forKey: key)
        return retrievedString
    }
    
    func remove(key: String) -> Bool {
        let removeSuccessful: Bool = keychainWrapper.removeObject(forKey: key)
        return removeSuccessful
    }
    
}
