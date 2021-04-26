//
//  WaitPopup.swift
//  Choueifat
//
//  Created by Imac on 8/27/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import UIKit

class WaitPopupViewController: UIViewController {

    @IBOutlet weak var msgLabel: UILabel!
    @IBOutlet weak var dismissButton: UIButton!
    
    
    var message : String? = StringResource.WaitMoments
    var dismissed = false
    var disappeared = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        msgLabel.text = message
        dismissButton.isHidden = true
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            if self.dismissed {
                self.dismiss(animated: true, completion: nil)
            }
            if let db = self.dismissButton {
                db.isHidden = false
            }
        }
        
    }
    
    @IBAction func onDismissClick(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    override func dismiss(animated flag: Bool, completion: (() -> Void)? = nil) {
        super.dismiss(animated: flag, completion: completion)
        dismissed = true
        Log.p("WaitPopupViewController dismissed")
        
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        disappeared = true
        Log.p("WaitPopupViewController disappeared")
    }
}
