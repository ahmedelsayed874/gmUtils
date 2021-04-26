//
//  UIActivityViewControllerExtension.swift
//  GMUtilsIOS
//
//  Created by GloryMaker on 10/21/20.
//  Copyright © 2020 GloryMaker. All rights reserved.
//

import UIKit


extension UIActivityViewController {
    func setupForIPad() {
        if let popoverController = popoverPresentationController {
            popoverController.sourceView = self.view
            popoverController.sourceRect = CGRect(x: self.view.bounds.midX, y: self.view.bounds.midY, width: 0, height: 0)
            popoverController.permittedArrowDirections = [] //to hide the arrow of any particular direction
        }
    }
}
