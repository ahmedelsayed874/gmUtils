//
//  BaseViewController.swift
//  Choueifat
//
//  Created by Imac on 8/27/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import UIKit

class BaseViewController: UIViewController {
    static var instance: BaseViewController? = nil

    
    var waitPopupVC : WaitPopupViewController?
    var waitPopupVCCount = 0
    var enableAutoHidingKeyboard = false
    private var keyboardHeightDelegate: ((CGFloat) -> Void)? = nil

    override func viewDidLoad() {
        super.viewDidLoad()
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        BaseViewController.instance = self
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        BaseViewController.instance = nil
    }
    
    @IBAction func onBackPressed(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @objc func keyboardWillShow(notification: NSNotification) {
        if notification.name == UIResponder.keyboardWillShowNotification {
            if let keyboardRect = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
                self.keyboardHeightDelegate?(keyboardRect.height)
                self.keyboardHeightDelegate = nil
            }
        }
    }
    
    func getKeyboardHeight(delegate: ((CGFloat) -> Void)?) {
        self.keyboardHeightDelegate = delegate
        self.keyboardHeightDelegate?(320)
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    func makeRoundEdges(view : UIView) {
        makeRoundEdges(view : view, radius : view.frame.height / 2)
    }
    
    func makeRoundEdges(view : UIView, radius : CGFloat) {
        view.layer.cornerRadius = radius
    }
    
    func showWaitPopup(msg: String?=nil, delay : Bool) {
        if delay {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                self.showWaitPopupImmediatily(msg: msg)
            }
        } else {
            showWaitPopupImmediatily(msg: msg)
        }
        
    }
    
    func showWaitPopupImmediatily(msg: String? = nil) {
        self.waitPopupVCCount += 1
        Log.p("-------Show wait popup (\(self.waitPopupVCCount))")
        Log.p("-------waitPopupVC == nil: \(waitPopupVC == nil)")
        
        if self.waitPopupVCCount == 1 {
            self.waitPopupVC?.dismiss(animated: false, completion: nil)
            self.waitPopupVC = VCPool.Popups.presentWait(viewController: self, msg: msg)
        }
    }
    
    func hideWaitPopup() {
        self.waitPopupVCCount -= 1
        Log.p("-------Hide wait popup (\(self.waitPopupVCCount))")
        Log.p("-------waitPopupVC == nil: \(waitPopupVC == nil)")
        
        if self.waitPopupVCCount <= 0 {
            self.waitPopupVC?.dismiss(animated: false, completion: nil)
            self.waitPopupVC = nil
        }
    }
    
    func showMessage(title: String, msg: String, defaultButtonTitle:String = StringResource.Ok, cancelButtonTitle:String? = nil, defaultButtonAction:(()->Void)? = nil, cancelButtonAction: (()->Void)? = nil)  {
        let alert = UIAlertController(title: title, message: msg, preferredStyle: .alert)
        
        alert.setupForIPad()
        
        alert.addAction(UIAlertAction(
            title: defaultButtonTitle,
            style: .default,
            handler: { (action) in
                alert.dismiss(animated: true, completion: nil)
                defaultButtonAction?()
        }))
        
        if cancelButtonTitle != nil || cancelButtonAction != nil {
            let cancelTitle = cancelButtonTitle ?? StringResource.Cancel
            
            alert.addAction(UIAlertAction(
                title: cancelTitle,
                style: .cancel,
                handler: { (action) in
                    alert.dismiss(animated: true, completion: nil)
                    cancelButtonAction?()
            }))
        }
        
        present(alert, animated: true, completion: nil)
    }
    
    func showRetryMessage(title: String, msg: String, retry:(()->Void)?, cancel: (()->Void)? = nil)  {
        showMessage(title: title, msg: msg, defaultButtonTitle: StringResource.Retry, cancelButtonTitle: StringResource.Cancel, defaultButtonAction: retry, cancelButtonAction: cancel)
    }
    
    func showErrorMessage(title: String = StringResource.Error, msg: String, action:(()->Void)? = nil)  {
//        let alert = UIAlertController(title: title, message: msg, preferredStyle: .alert)
        //alert.setupForIPad()
//
//        alert.addAction(UIAlertAction(title: StringResource.Ok, style: .default, handler: {(a) in
//            action?()
//        }))
//
//        present(alert, animated: true, completion: nil)
        showMessage(title: title, msg: msg, defaultButtonTitle: StringResource.Ok, defaultButtonAction: action)
    }
    
    func showInsureMessage(title: String = StringResource.Confirmation, msg: String, removeCancelButton: Bool = true, answer: @escaping (Bool) -> Void) {
        let alert = UIAlertController(title: title, message: msg, preferredStyle: .alert)
        
        alert.setupForIPad()
        
        alert.addAction(UIAlertAction(
            title: StringResource.Yes,
            style: .destructive,
            handler: { (action) in
                alert.dismiss(animated: true, completion: nil)
                answer(true)
        }))
        
        alert.addAction(UIAlertAction(
            title: StringResource.No,
            style: .default,
            handler: { (action) in
                alert.dismiss(animated: true, completion: nil)
                answer(false)
        }))
        
        if !removeCancelButton {
            alert.addAction(UIAlertAction(
                title: StringResource.Cancel,
                style: .cancel,
                handler: { (action) in
                    alert.dismiss(animated: true, completion: nil)
                    
            }))
        }
        
        present(alert, animated: true, completion: nil)
    }
    
    func showReloginRecommenditionMessage() {
        showMessage(title: StringResource.Error, msg: StringResource.SomeImportantDataAreMissingItsRecommendToSignoutAndLoginAgain)
    }
    
    func showToast(msg: String, deadline: DispatchTime? = nil, complete : (() -> Void)? = nil) {
        VCPool.Popups.presentToast(viewController: self, msg: msg, deadline: deadline, complete: complete)
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if (enableAutoHidingKeyboard) { view.endEditing(true) }
        super.touchesBegan(touches, with: event)
    }
    
    func isAuthorized(role: String, showMsg: Bool, action: (() -> Void)? = nil) -> Bool {
        let roles = AccountResource().retriveAccount()?.roles ?? [String]()
        var authorized = false

        for r in roles {
            if r == role {
                authorized = true
                break
            }
        }

        if !authorized && showMsg {
            showErrorMessage(title: StringResource.Message, msg: StringResource.YouAreNotAuthorized_ReferToFinancial, action: action)
        }

        return authorized
    }
   
    func addTapGesture(for: UIView, action: Selector) {
        let tap = UITapGestureRecognizer(target: self, action: action)
        `for`.addGestureRecognizer(tap)
        `for`.isUserInteractionEnabled = true
    }
}

extension BaseViewController: UITextFieldDelegate {
    
    func setTextFieledToDismissKeyboardOnReturn(_ textField: UITextField) {
        textField.delegate = self
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
}
