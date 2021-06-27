//
//  WaitPopup.swift
//

import UIKit

class WaitPopupViewController: UIViewController {
    
    var textColor = UIColor.white
    
    var message : String = "Please wait..."
    var dismissed = false
    var disappeared = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let activityIndicator = UIActivityIndicatorView()
        let msgLabel = UILabel()
        
        let dismissButton = UIButton()
        let dismissButtonContainer = UIView()
        dismissButtonContainer.addSubview(dismissButton)
        
        let horizontalStack = UIStackView(arrangedSubviews: [activityIndicator, msgLabel])
        let verticalStack = UIStackView(arrangedSubviews: [horizontalStack, dismissButtonContainer])
        
        self.view.addSubview(verticalStack)
        self.view.backgroundColor = .init(white: 0, alpha: 0.3)
        
        /// activityIndicator setup
        activityIndicator.addConstraint(.init(item: activityIndicator, attribute: .width, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1, constant: 25))
        activityIndicator.addConstraint(.init(item: activityIndicator, attribute: .height, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1, constant: 25))
        
        /// msgLabel
        msgLabel.text = message
        msgLabel.textColor = textColor
        
        /// dismissButton
        //dismissButton.setTitle(StringResource.Dissmiss, for: .normal)
        dismissButton.isHidden = true
        //dismissButton.textColor = textColor
        
        dismissButton.addConstraint(.init(item: dismissButton, attribute: .centerX, relatedBy: .equal, toItem: dismissButtonContainer, attribute: .centerX, multiplier: 1, constant: 0))
        dismissButton.addConstraint(.init(item: dismissButton, attribute: .centerY, relatedBy: .equal, toItem: dismissButtonContainer, attribute: .centerY, multiplier: 1, constant: 0))
        
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            if self.dismissed {
                self.dismiss(animated: true, completion: nil)
            }
            
            dismissButton.isHidden = false
        }
        
    }
    
    @IBAction func onDismissClick(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    override func dismiss(animated flag: Bool, completion: (() -> Void)? = nil) {
        super.dismiss(animated: flag, completion: completion)
        dismissed = true
        print("WaitPopupViewController dismissed")
        
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        disappeared = true
        print("WaitPopupViewController disappeared")
    }
}
