//
//  NotificationProcessor.swift
//  Ahmed El-Sayed
//
//  Created by GloryMaker on 4/5/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import UIKit

class NotificationProcessor {
    
    static let KEY_ObjectId = "ObjectId"
    static let KEY_NotificationReceiverId = "NotificationReceiverId"
    static let KEY_NotificationType = "NotificationType"
    static let KEY_FROM_MESSAGE_NOTIFICATION = "FROM_MESSAGE_NOTIFICATION"
    
    static var tappedNotification: Notification?  = nil
    
    static func openCorrespondingViewController(vc: UIViewController, notification: Notification) {
        let objectId = notification.Payload!.ObjectId
        let notificationType = notification.Payload!.NotificationType
        
        if (notificationType != 0) {
            
            var targetVC: UIViewController?
            
            switch notificationType {
            case NotificationTypes.Broadcast:
                targetVC = vc.storyboard?.instantiateViewController(withIdentifier: "NotificationsViewController") as! NotificationsViewController
                (targetVC as! NotificationsViewController).showBroadcastsOnly = true
                
            case NotificationTypes.ServiceRequestChangeStatus,
                 NotificationTypes.ServiceRequestReply,
                 NotificationTypes.ServiceRequestCreated :
                targetVC = vc.storyboard?.instantiateViewController(withIdentifier: "ServiceRequestDetailsViewController") as! ServiceRequestDetailsViewController
                (targetVC as! ServiceRequestDetailsViewController).serviceRequestId = objectId
                
            case NotificationTypes.RegistrationRequestCreated:
                targetVC = vc.storyboard?.instantiateViewController(withIdentifier: "ProfileViewController") as! ProfileViewController
                
            case NotificationTypes.AcceptRequestSubOwner,
                 NotificationTypes.RejectSubOwnerRequest,
                 NotificationTypes.AcceptRequestSubTenant,
                 NotificationTypes.RejectSubTenantRequest:
                targetVC = vc.storyboard?.instantiateViewController(withIdentifier: "SubAccountsViewController") as! SubAccountsViewController
                
            case NotificationTypes.AcceptRequestTenant,
                 NotificationTypes.RejectTenantRequest :
                targetVC = vc.storyboard?.instantiateViewController(withIdentifier: "TenantsViewController") as! TenantsViewController
                
                
            case NotificationTypes.InvoiceCreated,
                 NotificationTypes.InvoiceUpdated :
                targetVC = vc.storyboard?.instantiateViewController(withIdentifier: "InvoiceDetailsViewController") as! InvoiceDetailsViewController
                (targetVC as! InvoiceDetailsViewController).invoiceId = objectId
                
            default:
                //print("unknown")
                targetVC = vc.storyboard?.instantiateViewController(withIdentifier: "NotificationsViewController") as! NotificationsViewController
                (targetVC as! NotificationsViewController).showBroadcastsOnly = false
            }
            
            if targetVC != nil {
                vc.present(targetVC!, animated: true, completion: nil)
            }
        } else {
            (vc as? BaseViewController)?.showToast(msg: "Can't open")
        }
        
        let notificationReceiverId = notification.Payload!.NotificationReceiverId
        updateNotificationStatus(notificationReceiverId)
    }
    
    static func updateNotificationStatus(_ id: Int) {
        APIsPool.newInstance.broadcastAPIs.updateNotificationReadStatus(notificationReceiverIds: [id]) { (response) in
            if !response.isSuccess { Log.p(response.Message ?? "updateNotificationStatus - err") }
        }
    }
    
    static func handlePushNotification(userInfo: [AnyHashable: Any]) {
        if let objectId = userInfo[NotificationProcessor.KEY_ObjectId] {
            if let notificationId = userInfo[NotificationProcessor.KEY_NotificationReceiverId] {
                if let notificationType = userInfo[NotificationProcessor.KEY_NotificationType] {
                    
                    Log.p("notification-> objectId: \(objectId)")
                    Log.p("notification-> notificationId: \(notificationId)")
                    Log.p("notification-> notificationType: \(notificationType)")
                    
                    let notification = createNotificationObject(
                        objectId: Int(objectId as? String ?? "0") ?? 0,
                        notificationId: Int(notificationId as? String ?? "0") ?? 0,
                        notificationType: Int(notificationType as? String ?? "0") ?? 0
                    )
                    
                    NotificationProcessor.tappedNotification = notification
                }
            }
        }
    }
    
    static func createNotificationObject(objectId: Int, notificationId: Int, notificationType: Int) -> Notification {
        let payload = Payload(
            ObjectId: objectId,
            NotificationReceiverId: notificationId,
            NotificationType: notificationType
        )
        
        let notification = Notification(
            Title: "",
            Details: "",
            CreatedDate: "",
            Payload: payload,
            isSeen: false
        )
        
        return notification
    }
    
    static func updateAppIconBadgeCount(count: Int) {
        UIApplication.shared.applicationIconBadgeNumber = count
    }
}
