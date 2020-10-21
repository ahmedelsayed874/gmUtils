//
//  FirebaseNotificationService.swift
//  EasyIn
//
//  Created by GloryMaker on 4/6/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import Foundation
/*import Firebase

 /*
 pod 'Firebase/Analytics'
 pod 'Firebase/Messaging'
 */

let firebaseNotificationService = FirebaseNotificationService()

extension AppDelegate : MessagingDelegate {
    
    func setupFirebase(_ application: UIApplication) {
        FirebaseApp.configure()
        registerForRemoteNotifications(application: application)
        Messaging.messaging().delegate = self
        
    }
    
    func registerForRemoteNotifications(application: UIApplication) {
        
        if #available(iOS 10.0, *) {
            // For iOS 10 display notification (sent via APNS)
            UNUserNotificationCenter.current().delegate = self
            
            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {_, _ in }
            )
        } else {
            let settings: UIUserNotificationSettings = UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            application.registerUserNotificationSettings(settings)
        }
        
        application.registerForRemoteNotifications()
    }
    
    //---- token -------------------------------------------------------------------------------------------------------
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }
    
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String) {
        print("Firebase registration token: \(fcmToken)")
        
        let dataDict:[String: String] = ["token": fcmToken]
        NotificationCenter.default.post(name: NSNotification.Name("FCMToken"), object: nil, userInfo: dataDict)
    }
    
    func getFirebaseToken(complete: @escaping (String, Bool) -> Void) {
        InstanceID.instanceID().instanceID { (result, error) in
            if let error = error {
                complete("Error fetching remote instance ID: \(error)", false)
                
            } else if let result = result {
                complete("Remote instance ID token: \(result.token)", true)
            }
        }
    }
    
    
    //--- receiving ------------------------------------------------------------------------------------------------
    
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any],
                     fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        onNotificationReceived(userInfo: userInfo)
        completionHandler(UIBackgroundFetchResult.newData)
    }
    
    func onNotificationReceived(userInfo: [AnyHashable: Any]) {
        // With swizzling disabled you must let Messaging know about the message, for Analytics
        // Messaging.messaging().appDidReceiveMessage(userInfo)
        
        // Print full message.
        print(userInfo)
        
        NotificationProcessor.handlePushNotification(userInfo: userInfo)
    }
    
}

@available(iOS 10, *)
extension AppDelegate : UNUserNotificationCenterDelegate {

  // Receive displayed notifications for iOS 10 devices.
  func userNotificationCenter(_ center: UNUserNotificationCenter,
                              willPresent notification: UNNotification,
                              withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
    let userInfo = notification.request.content.userInfo

    // With swizzling disabled you must let Messaging know about the message, for Analytics
    // Messaging.messaging().appDidReceiveMessage(userInfo)

    // Print full message.
    print(userInfo)

    // Change this to your preferred presentation option
    completionHandler([[.alert, .sound, .badge]])
  }

  func userNotificationCenter(_ center: UNUserNotificationCenter,
                              didReceive response: UNNotificationResponse,
                              withCompletionHandler completionHandler: @escaping () -> Void) {
    let userInfo = response.notification.request.content.userInfo
    
    // Print full message.
    print(userInfo)
    
    NotificationCountController.checkNotificationCount()
    NotificationProcessor.handlePushNotification(userInfo: userInfo)

    completionHandler()
  }
}

class FirebaseNotificationService {
    
    static func subscripeToTopics(oldTopics: [String]?, newTopics: [String]?) {
        FirebaseNotificationService.unsubscripeFromTopics(topics: oldTopics)
        
        newTopics?.forEach({ (topic) in
            Messaging.messaging().subscribe(toTopic: topic) { error in
                if error == nil {
                    Log.p("Firebase: Subscribed to \(topic) topic")
                } else {
                    Log.p("Firebase: Subscribe error= \(error ?? "?")")
                }
            }
        })
        
    }
    
    static func unsubscripeFromTopics(topics: [String]?) {
        topics?.forEach({ (topic) in
            Messaging.messaging().unsubscribe(fromTopic: topic) { error in
                if error == nil {
                    Log.p("Firebase: UnSubscribed to \(topic) topic")
                } else {
                    Log.p("Firebase: Unsubscribe error= \(error ?? "?")")
                }
            }
        })
    }
    
}
*/
