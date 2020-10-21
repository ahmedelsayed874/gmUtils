//
//  ToastViewController.swift
//  Choueifat
//
//  Created by Imac on 8/29/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import UIKit

class ToastViewController: UIViewController {
    
    @IBOutlet weak var labelContainerView: UIView!
    @IBOutlet weak var label: UILabel!
    
    var message : String = "?"
    var deadline: DispatchTime = .now() + 1.8
    var complete : (() -> Void)?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        labelContainerView.layer.cornerRadius = labelContainerView.frame.height / 2 - 3
        
        label.text = message
        
        DispatchQueue.main.asyncAfter(deadline: deadline) {
            self.dismiss(animated: false, completion: self.complete)
        }
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.dismiss(animated: false, completion: complete)
    }
    
}
