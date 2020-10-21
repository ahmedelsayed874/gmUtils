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
    
    static func presentViewController(viewController vc : UIViewController, disableNav: Bool = false) {
        let targetVC = initViewController(storyboard : vc.storyboard!, withIdentifier: "ViewController") as! ViewController
        
        //targetVC.modalPresentationStyle = .overCurrentContext
        //targetVC.modalPresentationStyle = .fullScreen
//        if #available(iOS 13.0, *) {
//            targetVC.modalPresentationStyle = .automatic
//        }
        
        vc.present(targetVC, animated: true, completion: nil)
    }
    
}
