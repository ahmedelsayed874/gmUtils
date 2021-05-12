//
//  AppDelegate.swift
// Ahmed El-Sayed
//
//  Created by Imac on 2/11/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import UIKit


@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    private (set) var appClock: Clock? = nil
    private (set) var viewControllerLifecycleRecorder = ViewControllerLifecycleRecorder()
    
    static var isAppUsingArabic: Bool {
        return StringResource.Language == "ar"
    }
      
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        appClock = Clock()
        appClock?.start()
        
        setupFirebase(application)
        
        //changePreferenceLanguage()
                
        return true
    }
    
    func changePreferenceLanguage() {
        guard let account = AccountResource().retriveAccount() else {
            return
        }
        
        let accountId = account.AccountId
        let lang = StringResource.Language
        
        if account.preferenceLanguage == lang && LanguagePreference().isSavedFor(userId: accountId) {
            return
        }
        
        APIsPool.newInstance.accountAPIs.changePreferenceLanuage(token: account.Token, accountId: accountId, language: lang) { (response) in
            if response.isSuccess {
                LanguagePreference().setSavedFor(userId: accountId)
            }
            
            Log.p(response)
        }
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        AccountResource.removeAllObservers()
        
        appClock?.stopAndReset()
        appClock = nil
        
        viewControllerLifecycleRecorder.dispose()
    }
    
    
    // MARK: UISceneSession Lifecycle
    
    @available(iOS 13.0, *)
    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }
    
    @available(iOS 13.0, *)
    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }
    
}

