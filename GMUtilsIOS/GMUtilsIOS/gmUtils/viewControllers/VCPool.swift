//
//  ViewControllersPool.swift
//  Choueifat
//
//  Created by Imac on 8/27/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import UIKit

class VCPool {

    class Popups {
        
        static func presentWait(viewController vc : UIViewController, msg: String?=nil) -> WaitPopupViewController {
        
            let storyboard = UIStoryboard(name: "PopupStoryboard", bundle: nil)

            let targetVC = storyboard.instantiateViewController(withIdentifier: "WaitPopupViewController") as! WaitPopupViewController
            targetVC.modalPresentationStyle = .overCurrentContext

            targetVC.message = msg ?? StringResource.WaitMoments

            vc.present(targetVC, animated: false, completion: nil)
            
            return targetVC
            
        }
        
        static func presentToast(viewController vc: UIViewController, msg: String, deadline: DispatchTime? = nil, complete : (() -> Void)? = nil) {
            let storyboard = UIStoryboard(name: "PopupStoryboard", bundle: nil)
            
            let targetVC = storyboard.instantiateViewController(withIdentifier: "ToastViewController") as! ToastViewController
            targetVC.modalPresentationStyle = .overCurrentContext
            
            targetVC.message = msg
            if deadline != nil { targetVC.deadline = deadline! }
            targetVC.complete = complete
            
            vc.present(targetVC, animated: false, completion: nil)
            
        }
        
    }
    
    private static func initViewController(storyboard : UIStoryboard, withIdentifier:String) -> UIViewController {
        let targetVC = storyboard.instantiateViewController(withIdentifier: withIdentifier)
        targetVC.modalPresentationStyle = .fullScreen
        
        return targetVC
    }
    
    static func presentMain(viewController vc : UIViewController, disableNav: Bool = false) {
        let targetVC = initViewController(storyboard : vc.storyboard!, withIdentifier: "MainViewController") as! MainCommunityViewController
        
        //targetVC.disableNav = disableNav
        
        vc.present(targetVC, animated: true, completion: nil)
    }
    
    static func presentDatePicker(viewController vc: UIViewController, title: String?, mode: UIDatePicker.Mode = .date, minAge: Int? = nil, maxAge: Int? = nil, onDateSelected: @escaping ((Date) -> Void)) {
        //
        let targetVC = initViewController(storyboard : vc.storyboard!, withIdentifier: "DatePickerViewController") as! DatePickerViewController
        targetVC.modalPresentationStyle = .overCurrentContext
        
        targetVC.subject = title
        targetVC.mode = mode
        if minAge != nil { targetVC.minAge = minAge! }
        if maxAge != nil { targetVC.maxAge = maxAge! }
        targetVC.onDateSelected = onDateSelected
        
        vc.present(targetVC, animated: true, completion: nil)
    }
    
    static func presentVerifyAccount(viewController vc: UIViewController, account: Account) {
        let targetVC = initViewController(storyboard : vc.storyboard!, withIdentifier: "VerifyingAccountViewController") as! VerifyingAccountViewController
        targetVC.modalPresentationStyle = .fullScreen
        
        targetVC.account = account
        
        vc.present(targetVC, animated: true, completion: nil)
    }
    
    static func presentRegisterProfile3(viewController vc: UIViewController, basedAccount: Account?, role: Role, onAccountCreated: ((Account) -> Void)? = nil) {
        let targetVC = initViewController(storyboard : vc.storyboard!, withIdentifier: "Register3ProfileViewController") as! Register3ProfileViewController
        if #available(iOS 13.0, *) {
            targetVC.modalPresentationStyle = .automatic
        }
        
        targetVC.basedAccount = basedAccount
        targetVC.role = role
        targetVC.onAccountCreated = onAccountCreated
        
        vc.present(targetVC, animated: true, completion: nil)
    }
    
    static func presentProfile(viewController vc: UIViewController, account: Account? = nil) {
        let targetVC = initViewController(storyboard : vc.storyboard!, withIdentifier: "ProfileViewController") as! ProfileViewController
        targetVC.modalPresentationStyle = .fullScreen
        
        targetVC.account = account
        
        vc.present(targetVC, animated: true, completion: nil)
    }
    
    static func presentVisitRequestDetails(viewController vc: UIViewController, visitRequest: VisitRequest) {
        let targetVC = initViewController(storyboard : vc.storyboard!, withIdentifier: "VisitRequestDetailsViewController") as! VisitRequestDetailsViewController
        if #available(iOS 13.0, *) {
            targetVC.modalPresentationStyle = .automatic
        }
        
        targetVC.visitRequest = visitRequest
        
        vc.present(targetVC, animated: true, completion: nil)
    }
    
    static func presentServiceRequestDetails(viewController vc: UIViewController, serviceRequest: ServiceRequest) {
        let targetVC = initViewController(storyboard : vc.storyboard!, withIdentifier: "ServiceRequestDetailsViewController") as! ServiceRequestDetailsViewController
        if #available(iOS 13.0, *) {
            targetVC.modalPresentationStyle = .automatic
        }
        
        targetVC.serviceRequest = serviceRequest
        
        vc.present(targetVC, animated: true, completion: nil)
    }
    
    static func presentImageViewer(viewController vc: UIViewController, imageURL: String?, image: UIImage?) {
        
        let targetVC = initViewController(storyboard : vc.storyboard!, withIdentifier: "ImageViewerViewController") as! ImageViewerViewController

        targetVC.modalPresentationStyle = .fullScreen
        
        targetVC.imageURL = imageURL
        targetVC.image = image

        vc.present(targetVC, animated: true, completion: nil)
    }
    
    static func presentCompoundRoles(viewController vc: UIViewController) {
        
        let targetVC = initViewController(storyboard : vc.storyboard!, withIdentifier: "CompoundRolesViewController") as! CompoundRolesViewController

        targetVC.modalPresentationStyle = .fullScreen
        
        vc.present(targetVC, animated: true, completion: nil)
    }
}
