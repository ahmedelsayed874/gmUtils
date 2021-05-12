//
//  BaseViewController.swift
//  Ahmed El-Sayed
//
//  Created by Imac on 8/27/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import UIKit

class BaseViewController: UIViewController {
    static var instance: BaseViewController? = nil

    
    @IBOutlet weak var appLogo: UIImageView!
    var waitPopupVC : WaitPopupViewController?
    var waitPopupVCCount = 0
    var enableAutoHidingKeyboard = false
    private var keyboardHeight: CGFloat = 320
    private var keyboardHeightDelegate: ((CGFloat) -> Void)? = nil
    
    var appDelegate:AppDelegate? = nil

    
    //MARK:- lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        Log.p("LIFECYCLE", "\(type(of: self)):\(hashValue) -> viewDidLoad()")
        
        appDelegate = UIApplication.shared.delegate as? AppDelegate
        appDelegate?.viewControllerLifecycleRecorder.recordeViewDidLoad(viewController: self)
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        
        BaseViewController.instance = self
        
        fetchCompoundPhoto()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        Log.p("LIFECYCLE", "\(type(of: self)):\(hashValue) -> viewWillAppear()")
        appDelegate?.viewControllerLifecycleRecorder.recordeViewWillAppear(viewController: self, animated: animated)
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        Log.p("LIFECYCLE", "\(type(of: self)):\(hashValue) -> viewDidAppear()")
        appDelegate?.viewControllerLifecycleRecorder.recordeViewDidAppear(viewController: self, animated: animated)
        BaseViewController.instance = self
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        Log.p("LIFECYCLE", "\(type(of: self)):\(hashValue) -> viewWillDisappear()")
        appDelegate?.viewControllerLifecycleRecorder.recordeViewWillDisappear(viewController: self, animated: animated)
        BaseViewController.instance = nil
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        Log.p("LIFECYCLE", "\(type(of: self)):\(hashValue) -> viewDidDisappear()")
        appDelegate?.viewControllerLifecycleRecorder.recordeViewDidDisappear(viewController: self, animated: animated)
    }
    
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        super.viewWillTransition(to: size, with: coordinator)
        Log.p("LIFECYCLE", "\(type(of: self)):\(hashValue) -> viewWillTransition()")
        appDelegate?.viewControllerLifecycleRecorder.recordeViewWillTransition(viewController: self, size: size, coordinator: coordinator)
    }
    
    
    //MARK:- lifecycle observer
    func registerLifecycleObserver(name: String, delegate: @escaping ViewControllerLifecycleRecorder.Delegate) {
        appDelegate?.viewControllerLifecycleRecorder.registerDelegate(name: name){name, vc, state in
            if vc == self {return}
            else {delegate(name, vc, state)}
        }
    }
    
    func unregisterLifecycleObserver(name: String) {
        appDelegate?.viewControllerLifecycleRecorder.unregisterDelegate(name: name)
    }
    
    
    //MARK:- fetchCompoundPhoto
    private func fetchCompoundPhoto(){
        if appLogo == nil { return }
        guard let account = AccountResource().retriveAccount() else { return }
        
        let accountId = account.AccountId
        
        APIsPool.newInstance.miscAPIs.getCompoundPhoto(accountId: accountId) { (response) in
            if response.isSuccess {
                ImageLoader.load(url: response.Data!, imageView: self.appLogo, placeHolderImage: #imageLiteral(resourceName: "paradise_logo_with_bg"), onComplete: {iv, url, suc in
                    if !suc {
                        self.appLogo.image = #imageLiteral(resourceName: "paradise_logo_with_bg")
                    }
                })
            } else {
                self.appLogo.image = #imageLiteral(resourceName: "paradise_logo_with_bg")
            }
        }
    }
    
    //MARK:- Close Button - START
    @IBOutlet weak var closeButton: UIButton!
    
    @IBAction func onBackPressed(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    //MARK:- Close Button - END
    
    @objc func keyboardWillShow(notification: NSNotification) {
        if notification.name == UIResponder.keyboardWillShowNotification {
            if let keyboardRect = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
                self.keyboardHeight = keyboardRect.height
                self.keyboardHeightDelegate?(keyboardRect.height)
                self.keyboardHeightDelegate = nil
            }
        }
    }
    
    func getKeyboardHeight(delegate: ((CGFloat) -> Void)?) {
        self.keyboardHeightDelegate = delegate
        self.keyboardHeightDelegate?(keyboardHeight)
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
    
    func drawBorderAroundEdges(view : UIView, color: UIColor?, width : CGFloat = 2) {
        view.layer.borderWidth = width
        view.layer.borderColor = color?.cgColor
    }
    
    
    var isAppUsingArabic: Bool {
        return AppDelegate.isAppUsingArabic
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

extension UIAlertController {
    
    func setupForIPad() {
        if let popoverController = popoverPresentationController {
            popoverController.sourceView = self.view
            popoverController.sourceRect = CGRect(x: self.view.bounds.midX, y: self.view.bounds.midY, width: 0, height: 0)
            popoverController.permittedArrowDirections = [] //to hide the arrow of any particular direction
        }
    }
}

extension UIActivityViewController {
    func setupForIPad() {
        if let popoverController = popoverPresentationController {
            popoverController.sourceView = self.view
            popoverController.sourceRect = CGRect(x: self.view.bounds.midX, y: self.view.bounds.midY, width: 0, height: 0)
            popoverController.permittedArrowDirections = [] //to hide the arrow of any particular direction
        }
    }
}
