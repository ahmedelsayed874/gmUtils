//
//  ImagePicker.swift
//  EasyIn
//
//  Created by Imac on 3/5/20.
//  Copyright © 2020 OGTech. All rights reserved.
//

import UIKit

///
/// https://developer.apple.com/documentation/uikit/uiimagepickercontroller
///
class ImagePicker {
    private weak var vc: UIViewController? = nil
    private weak var delegate: (UIImagePickerControllerDelegate & UINavigationControllerDelegate)? = nil
    
    init(vc: UIViewController) throws {
        if !(vc is UIImagePickerControllerDelegate || vc is UINavigationControllerDelegate) {
            throw NSError(domain: "ImagePicker", code: 0, userInfo: ["Protocols" : "UIViewController must conforms UIImagePickerControllerDelegate & UINavigationControllerDelegate"])
        } else {
            self.vc = vc
            self.delegate = (vc as! (UIImagePickerControllerDelegate & UINavigationControllerDelegate))
        }
        
    }
    
    func pick(source: UIImagePickerController.SourceType, photo: Bool, id: Int) throws {
        
        if !UIImagePickerController.isSourceTypeAvailable(source) {
            throw NSError(domain: "ImagePicker", code: 0, userInfo: ["ImageSource" : "taking photo from \(source), is not available"])
        }
        
        let picker = UIImagePickerController()
        picker.sourceType = source
        
        var mediaTypes = UIImagePickerController.availableMediaTypes(for: source)
        if mediaTypes == nil {
            if source == .camera {
                mediaTypes = photo ? ["png"] : ["mp4"]
            } else {
                mediaTypes = photo ? ["png", "jpg", "jpeg"] : ["mp4"]
            }
        }
        picker.mediaTypes = mediaTypes!
        
        picker.delegate = delegate
        
        picker.allowsEditing = true
        
        if source == .camera {
            picker.showsCameraControls = true
            picker.cameraViewTransform = .identity
            
            if photo {
                picker.cameraCaptureMode = .photo
                
                if UIImagePickerController.isCameraDeviceAvailable(.front) {
                    picker.cameraDevice = .front
                } else {
                    picker.cameraDevice = .rear
                }
            } else {
                picker.cameraCaptureMode = .video
                picker.videoExportPreset = "videoExportPreset"
                picker.videoMaximumDuration = 2 * 60
                picker.videoQuality = .typeHigh
                
                //picker.startVideoCapture()
                //picker.stopVideoCapture()
            }
        }
        //else {
        //    picker.takePicture()
        //}
        
        
        //Device Name  :  iPhone 11 Pro Max   | iPad Pro (11-inch)
        //Device Models:        iPhone        |        iPad
        
//        if UIDevice.current.name.lowercased().contains("ipad") {
//            picker.modalPresentationStyle = source == .camera ? .fullScreen : .popover
//        } else {
//            picker.modalPresentationStyle = .fullScreen
//        }
        picker.modalPresentationStyle = .fullScreen
        picker.view.tag = id
        
        self.vc?.present(picker, animated: true, completion: nil)
    }
    
    func pickFromGallery(photo: Bool = true, id: Int = 0) throws {
        try pick(source: .photoLibrary, photo: photo, id: id)
    }
    
    func pickFromCamera(photo: Bool = true, id: Int = 0) throws {
        try pick(source: .camera, photo: photo, id: id)
    }
    
}

class UIImagePickerControllerDelegateExample {
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        Log.p("imagePickerController -> didFinishPickingMediaWithInfo:")
        for (k, v) in info {
            Log.p("InfoKey: \(k)")
            Log.p("InfoVal: \(v)")
            Log.p("++++++++++++++")
        }
        
        _ = info[.imageURL] as! String
        _ = info[.originalImage] as? UIImage
        
        picker.dismiss(animated: true, completion: nil)
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        picker.dismiss(animated: true, completion: nil)
    }
}

