//
//  ToastViewController.swift
//

import UIKit

class ToastViewController: UIViewController {
    
    var backgroundColor = UIColor.black
    var textColor = UIColor.white
    
    var message : String = "?"
    var deadline: DispatchTime = .now() + 1.8
    var complete : (() -> Void)?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    
        let label = UILabel()
        
        let labelContainerView = UIView()
        labelContainerView.addSubview(label)
        
        self.view.addSubview(labelContainerView)
        self.view.backgroundColor = .init(white: 0, alpha: 0)
        
        /// setup label
        label.text = message
        label.textColor = textColor
        label.textAlignment = .center
        
        let labelToContainerMargin: CGFloat = 10
        label.addConstraint(.init(item: label, attribute: .left, relatedBy: .equal, toItem: labelContainerView, attribute: .left, multiplier: 1, constant: labelToContainerMargin))
        label.addConstraint(.init(item: label, attribute: .right, relatedBy: .equal, toItem: labelContainerView, attribute: .right, multiplier: 1, constant: labelToContainerMargin))
        label.addConstraint(.init(item: label, attribute: .top, relatedBy: .equal, toItem: labelContainerView, attribute: .top, multiplier: 1, constant: labelToContainerMargin))
        label.addConstraint(.init(item: label, attribute: .bottom, relatedBy: .equal, toItem: labelContainerView, attribute: .bottom, multiplier: 1, constant: labelToContainerMargin))
        
        
        /// setup label container
        labelContainerView.backgroundColor = backgroundColor
        labelContainerView.layer.cornerRadius = labelContainerView.frame.height / 2 - 3
        
        labelContainerView.addConstraint(.init(item: labelContainerView, attribute: .centerX, relatedBy: .equal, toItem: self.view, attribute: .centerX, multiplier: 1, constant: 0))
        
        
        let labelContainerToContainerMargin: CGFloat = 30
        labelContainerView.addConstraint(.init(item: labelContainerView, attribute: .left, relatedBy: .equal, toItem: self.view, attribute: .left, multiplier: 1, constant: labelContainerToContainerMargin))
        labelContainerView.addConstraint(.init(item: labelContainerView, attribute: .right, relatedBy: .equal, toItem: self.view, attribute: .right, multiplier: 1, constant: labelContainerToContainerMargin))
        labelContainerView.addConstraint(.init(item: labelContainerView, attribute: .bottom, relatedBy: .equal, toItem: self.view, attribute: .bottom, multiplier: 1, constant: labelContainerToContainerMargin))
        
        
        DispatchQueue.main.asyncAfter(deadline: deadline) {
            self.dismiss(animated: false, completion: self.complete)
        }
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.dismiss(animated: false, completion: complete)
    }
    
}
