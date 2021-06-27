//
//  BaseViewController.swift
//  Ahmed El-Sayed
//

import UIKit

class BaseViewController: UIViewController {
    
    var waitPopupVC : WaitPopupViewController?
    var waitPopupVCCount = 0
    var enableAutoHidingKeyboard = false
    private var keyboardHeight: CGFloat = 320
    private var keyboardHeightDelegate: ((CGFloat) -> Void)? = nil
    
    var appDelegate:AppDelegate? {
        return  UIApplication.shared.delegate as? AppDelegate
    }

    
    //MARK:- lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        print("LIFECYCLE", "\(type(of: self)):\(hashValue) -> viewDidLoad()")
        
        appDelegate?.viewControllerLifecycleRecorder.recordeViewDidLoad(viewController: self)
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
                
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        print("LIFECYCLE", "\(type(of: self)):\(hashValue) -> viewWillAppear()")
        appDelegate?.viewControllerLifecycleRecorder.recordeViewWillAppear(viewController: self, animated: animated)
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        print("LIFECYCLE", "\(type(of: self)):\(hashValue) -> viewDidAppear()")
        appDelegate?.viewControllerLifecycleRecorder.recordeViewDidAppear(viewController: self, animated: animated)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        print("LIFECYCLE", "\(type(of: self)):\(hashValue) -> viewWillDisappear()")
        appDelegate?.viewControllerLifecycleRecorder.recordeViewWillDisappear(viewController: self, animated: animated)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        print("LIFECYCLE", "\(type(of: self)):\(hashValue) -> viewDidDisappear()")
        appDelegate?.viewControllerLifecycleRecorder.recordeViewDidDisappear(viewController: self, animated: animated)
    }
    
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        super.viewWillTransition(to: size, with: coordinator)
        print("LIFECYCLE", "\(type(of: self)):\(hashValue) -> viewWillTransition()")
        appDelegate?.viewControllerLifecycleRecorder.recordeViewWillTransition(viewController: self, size: size, coordinator: coordinator)
    }
    
    override func dismiss(animated flag: Bool, completion: (() -> Void)? = nil) {
        super.dismiss(animated: flag, completion: completion)
        print("LIFECYCLE", "\(type(of: self)):\(hashValue) -> dismiss()")
        appDelegate?.viewControllerLifecycleRecorder.recordeDismiss(viewController: self)
        didDismiss()
    }
    
    func didDismiss() {}

    
    
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
    
    //MARK:- keyboard
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
    
    //MARK:- helpers
    override var preferredStatusBarStyle: UIStatusBarStyle {
        if #available(iOS 13.0, *) {
            return .darkContent
        } else {
            return .default
        }
    }
    
    func makeRoundEdges(view : UIView) {
        makeRoundEdges(view : view, radius : view.frame.height / 2)
    }
    
    func makeRoundEdges(view : UIView, radius : CGFloat) {
        view.layer.cornerRadius = radius
    }
    
    func drawBorderAroundView(view : UIView, color: UIColor?, width : Float = 2) {
        view.layer.borderWidth = CGFloat(width)
        view.layer.borderColor = color?.cgColor
    }
    
    func addShadowAroundView(view: UIView, shadowColor: UIColor = UIColor.black, shadowOpacity: Float = 0.4, shadowWidth: Int = 1) {
        view.layer.shadowColor = shadowColor.cgColor
        view.layer.shadowOpacity = shadowOpacity
        view.layer.shadowOffset = .zero
        view.layer.shadowRadius = CGFloat(shadowWidth)
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
        print("-------Show wait popup (\(self.waitPopupVCCount))")
        print("-------waitPopupVC == nil: \(waitPopupVC == nil)")
        
        if self.waitPopupVCCount == 1 {
            self.waitPopupVC?.dismiss(animated: false, completion: nil)
            self.waitPopupVC = WaitPopupViewController()
                //VCPool.Popups.presentWait(viewController: self, msg: msg)
        }
    }
    
    func hideWaitPopup() {
        self.waitPopupVCCount -= 1
        print("-------Hide wait popup (\(self.waitPopupVCCount))")
        print("-------waitPopupVC == nil: \(waitPopupVC == nil)")
        
        if self.waitPopupVCCount <= 0 {
            self.waitPopupVC?.dismiss(animated: false, completion: nil)
            self.waitPopupVC = nil
        }
    }
    
    func showMessage(title: String, msg: String, defaultButtonTitle:String = "Ok", cancelButtonTitle:String? = nil, defaultButtonAction:(()->Void)? = nil, cancelButtonAction: (()->Void)? = nil)  {
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
            let cancelTitle = cancelButtonTitle ?? "Cancel"
            
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
        showMessage(title: title, msg: msg, defaultButtonTitle: "Retry", cancelButtonTitle: "Cancel", defaultButtonAction: retry, cancelButtonAction: cancel)
    }
    
    func showErrorMessage(title: String = "Error", msg: String, action:(()->Void)? = nil)  {
        showMessage(title: title, msg: msg, defaultButtonTitle: "Ok", defaultButtonAction: action)
    }
    
    func showInsureMessage(title: String = "Confirmation", msg: String, removeCancelButton: Bool = true, answer: @escaping (Bool) -> Void) {
        let alert = UIAlertController(title: title, message: msg, preferredStyle: .alert)
        
        alert.setupForIPad()
        
        alert.addAction(UIAlertAction(
            title: "Yes",
            style: .destructive,
            handler: { (action) in
                alert.dismiss(animated: true, completion: nil)
                answer(true)
        }))
        
        alert.addAction(UIAlertAction(
            title: "No",
            style: .default,
            handler: { (action) in
                alert.dismiss(animated: true, completion: nil)
                answer(false)
        }))
        
        if !removeCancelButton {
            alert.addAction(UIAlertAction(
                title: "Cancel",
                style: .cancel,
                handler: { (action) in
                    alert.dismiss(animated: true, completion: nil)
                    
            }))
        }
        
        present(alert, animated: true, completion: nil)
    }
    
    func showToast(msg: String, deadline: DispatchTime? = nil, complete : (() -> Void)? = nil) {
        VCPool.Popups.presentToast(viewController: self, msg: msg, deadline: deadline, complete: complete)
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if (enableAutoHidingKeyboard) { view.endEditing(true) }
        super.touchesBegan(touches, with: event)
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
