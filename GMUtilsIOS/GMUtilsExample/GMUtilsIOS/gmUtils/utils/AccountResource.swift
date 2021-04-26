//
//  AccountResource.swift
//  Choueifat
//
//  Created by Imac on 8/27/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import UIKit

struct Account: Codable {}

typealias AccountResourceDelegate = (String/*Id*/, AccountResource, String/*KEY*/, Any?/*Data*/) -> Void

class AccountResource: NSObject {
    static let KEY_ACCOUNT = "ACCOUNT"
    static let KEY_PASSWORD = "PASSWORD"
    
    private static var observer : [String : AccountResourceDelegate]? = [String : AccountResourceDelegate]()
    
    static func addObserver(id: String, observer: @escaping AccountResourceDelegate) {
        AccountResource.observer?.updateValue(observer, forKey: id)
    }
    
    static func removeObserver(id: String) {
        AccountResource.observer?.removeValue(forKey: id)
    }
    
    static func removeAllObservers() {
        AccountResource.observer?.removeAll()
    }
    
    static func callObserver(accRes: AccountResource, key: String, data: Any?) {
        for (id, observer) in AccountResource.observer ?? [String : AccountResourceDelegate]() {
            observer(id, accRes, key, data)
        }
    }
    
    //----------------------------------------------------------------------------------------------------------
    
    private static var accountObj : Account? = nil
    
    private var DB: UserDefaults {
        return UserDefaults.init(suiteName: "Account")!
    }
    
    
    func save(account : Account, password: String? = nil) {        
        AccountResource.accountObj = account
        
        let res = JSONManipulations.encodeToString(account)
        
        DB.set(res, forKey: AccountResource.KEY_ACCOUNT)

        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            AccountResource.callObserver(accRes: self, key: AccountResource.KEY_ACCOUNT, data: account)
        }
        
        if password != nil {
            savePassword(password: password!)
        }
        
    }
    
    func savePassword(password: String) {
        
        let successful = KeychainService()//(servicePath: ServicePath(serviceName: "ACC", accessGroup: "UPW"))
            .save(key: AccountResource.KEY_PASSWORD, value: password)
        if !successful { DB.set(password, forKey: AccountResource.KEY_PASSWORD) }
        
        AccountResource.callObserver(accRes: self, key: AccountResource.KEY_PASSWORD, data: password)
    }
    
    func retriveAccount() -> Account? {
        if (AccountResource.accountObj != nil) {return AccountResource.accountObj}

        let json = DB.string(forKey: AccountResource.KEY_ACCOUNT)
        guard json != nil, json != "" else { return nil }
        
        let data = json!.data(using: .utf8)!
        let res = JSONManipulations.decode(type: Account.self, data: data)
    
        AccountResource.accountObj = res
        
        return AccountResource.accountObj
    }
    
    func retrivePassword() -> String {
        
        var password = KeychainService()//(servicePath: ServicePath(serviceName: "ACC", accessGroup: "UPW"))
            .retrieve(key: AccountResource.KEY_PASSWORD)
        
        if password == nil { password = DB.string(forKey: AccountResource.KEY_PASSWORD) }
        
        return password ?? ""
    }
    
    func clear(allowObserver : Bool = true) {
        AccountResource.accountObj = nil
        DB.set(nil, forKey: AccountResource.KEY_ACCOUNT)
        DB.set(nil, forKey: AccountResource.KEY_PASSWORD)
        savePassword(password: "")
        
        if allowObserver {
            AccountResource.callObserver(accRes: self, key: "", data: nil)
        }
        
    }
    
}
