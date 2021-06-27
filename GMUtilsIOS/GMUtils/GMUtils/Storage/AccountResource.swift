//
//  AccountResource.swift
//  Choueifat
//
//  Created by Imac on 8/27/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import UIKit


/*class AccountResource<T : Codable> {

    typealias AccountResourceDelegate = (String/*Id*/, AccountResource<T>, String/*KEY*/, Any?/*Data*/) -> Void
    
    let KEY_ACCOUNT = "ACCOUNT"
    let KEY_USER_NAME = "USER_NAME"
    let KEY_PASSWORD = "PASSWORD"
    
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
    
    
    func saveAccount(account : Account) -> AccountResource {
        let oldAccount = retriveAccount()
        
        AccountResource.accountObj = account
        
        let res = JSONManipulations.encodeToString(account)
        
        DB.set(res, forKey: AccountResource.KEY_ACCOUNT)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            AccountResource.callObserver(accRes: self, key: AccountResource.KEY_ACCOUNT, data: account)
        }
        
        FirebaseNotificationService.subscripeToTopics(oldTopics: oldAccount?.topics, newTopics: account.topics)
        
        return self
    }
    
    func saveCredentials(userName: String, password: String) -> AccountResource {
        let kc = KeychainService()
        
        if !kc.save(key: AccountResource.KEY_USER_NAME, value: userName) {
            DB.set(userName, forKey: AccountResource.KEY_USER_NAME)
        }
        
        if !kc.save(key: AccountResource.KEY_PASSWORD, value: password) {
            DB.set(password, forKey: AccountResource.KEY_PASSWORD)
        }
        
        AccountResource.callObserver(accRes: self, key: "Credentials", data: [userName, password])
        
        return self
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
    
    func retriveUserName() -> String {
        
        var userName = KeychainService()
            .retrieve(key: AccountResource.KEY_USER_NAME)
        
        if userName == nil { userName = DB.string(forKey: AccountResource.KEY_USER_NAME) }
        
        return userName ?? ""
    }
    
    func retrivePassword() -> String {
        
        var password = KeychainService()//(servicePath: ServicePath(serviceName: "ACC", accessGroup: "UPW"))
            .retrieve(key: AccountResource.KEY_PASSWORD)
        
        if password == nil { password = DB.string(forKey: AccountResource.KEY_PASSWORD) }
        
        return password ?? ""
    }
    
    func clear(allowObserver : Bool = true) {
        let account = retriveAccount()
        
        AccountResource.accountObj = nil
        DB.set(nil, forKey: AccountResource.KEY_ACCOUNT)
        DB.set(nil, forKey: AccountResource.KEY_USER_NAME)
        DB.set(nil, forKey: AccountResource.KEY_PASSWORD)
        
        let _ = saveCredentials(userName: "", password: "")
        
        if allowObserver {
            AccountResource.callObserver(accRes: self, key: "", data: nil)
        }
        
        FirebaseNotificationService.unsubscripeFromTopics(topics: account?.topics)
    }
    
    //----------------------------------------------------------------------------------
    
    
    class TempUser {
        
        //        private static let ACCOUNT_TYPE = "TempUser-AccountType"
        private static let DISTRICT = "TempUser-District"
        private static let COMMUNITY = "TempUser-Community"
        private static let UNIT_NUMBERS = "TempUser-UnitNumbers"
        private static let PHASES = "TempUser-PHASES"
        private static let ACCOUNT = "TempUser-Account"
        
        init(clearExistance: Bool = false) {
            if clearExistance { clear() }
            
        }
        
        private var DB: UserDefaults {
            return UserDefaults.init(suiteName: "TempAccount")!
        }
        
        //MARK:- Account Type
        //        func saveAccountType(_ type: String) {
        //            DB.set(type, forKey: TempUser.ACCOUNT_TYPE)
        //        }
        //
        //        func getAccountType() -> String {
        //            return DB.string(forKey: TempUser.ACCOUNT_TYPE) ?? ""
        //        }
        
        //MARK:- District
        func saveDistrict(_ obj: District) {
            let res = JSONManipulations.encodeToString(obj)
            DB.set(res, forKey: TempUser.DISTRICT)
        }
        
        func getDistrict() -> District? {
            let json = DB.string(forKey: TempUser.DISTRICT)
            guard json != nil, json != "" else { return nil }
            
            let data = json!.data(using: .utf8)!
            let res = JSONManipulations.decode(type: District.self, data: data)
            return res
        }
        
        //MARK:- Community
        func saveCommunity(_ obj: Community) {
            let res = JSONManipulations.encodeToString(obj)
            DB.set(res, forKey: TempUser.COMMUNITY)
        }
        
        func getCommunity() -> Community? {
            let json = DB.string(forKey: TempUser.COMMUNITY)
            guard json != nil, json != "" else { return nil }
            
            let data = json!.data(using: .utf8)!
            let res = JSONManipulations.decode(type: Community.self, data: data)
            return res
        }
        
        //MARK:- Unit Numbers
        func saveUnitNumbers(_ numbers: [String]) {
            //            var unitNumbers = numbers[0]
            //            for i in 1 ..< numbers.count {
            //                unitNumbers = "-\(numbers[i])"
            //            }
            //            DB.set(unitNumbers, forKey: TempUser.UNIT_NUMBERS)
            
            DB.set(numbers, forKey: TempUser.UNIT_NUMBERS)
        }
        
        func getUnitNumbers() -> [String]? {
            //            let n = DB.string(forKey: TempUser.UNIT_NUMBERS)
            //            let list = n?.split(separator: "-")
            //            let numbers = list?.map({ (str) -> String in
            //                str.description
            //            })
            
            let numbers = DB.array(forKey: TempUser.UNIT_NUMBERS) as? [String]
            return numbers
        }
        
        
        //MARK:- Phases
        func savePhases(_ phasesIds: [Int]) {
            DB.set(phasesIds, forKey: TempUser.PHASES)
        }
        
        func getPhases() -> [Int]? {
            let numbers = DB.array(forKey: TempUser.PHASES) as? [Int]
            return numbers
        }
        
        
        //MARK:- Account
        func saveAccount(_ account: Account) {
            let res = JSONManipulations.encodeToString(account)
            DB.set(res, forKey: TempUser.ACCOUNT)
        }
        
        func getAccount() -> Account? {
            let json = DB.string(forKey: TempUser.ACCOUNT)
            guard json != nil, json != "" else { return nil }
            
            let data = json!.data(using: .utf8)!
            let res = JSONManipulations.decode(type: Account.self, data: data)
            return res
        }
        
        
        func clear() {
            //            DB.set(nil, forKey: TempUser.ACCOUNT_TYPE)
            
            DB.set(nil, forKey: TempUser.DISTRICT)
            DB.set(nil, forKey: TempUser.COMMUNITY)
            DB.set(nil, forKey: TempUser.UNIT_NUMBERS)
            
            DB.set(nil, forKey: TempUser.ACCOUNT)
        }
        
    }
    
}*/
