//
//  TextFieldExtension.swift
//  Choueifat
//
//  Created by Imac on 8/26/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import UIKit

extension UITextField{
   @IBInspectable var placeHolderColor: UIColor? {
        get {
            return self.placeHolderColor
        }
        set {
            self.attributedPlaceholder = NSAttributedString(string: self.placeholder != nil ? self.placeholder! : "", attributes:[NSAttributedString.Key.foregroundColor: newValue!])
        }
    }
}
