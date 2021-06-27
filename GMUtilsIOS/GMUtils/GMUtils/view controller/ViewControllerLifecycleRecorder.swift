//
//  ViewControllerLifecycle.swift
//

import UIKit

enum ViewControllerLifecycleStates {
    case didLoad
    case willAppear(animated: Bool)
    case didAppear(animated: Bool)
    case willDisappear(animated: Bool)
    case didDisappear(animated: Bool)
    case willTransition(size: CGSize, coordinator: UIViewControllerTransitionCoordinator)
    case didDismiss
}

class ViewControllerLifecycleRecorder {
    
    typealias Delegate = (String, UIViewController, ViewControllerLifecycleStates) -> Void
    
    private var delegates = [String:Delegate]()
    
    
    //MARK:- DELEGATE
    func registerDelegate(name: String, delegate: @escaping Delegate) {
        delegates.updateValue(delegate, forKey: name)
    }
    
    func unregisterDelegate(name: String) {
        delegates.removeValue(forKey: name)
    }
    
    func removeAllDelegates() {
        delegates.removeAll()
    }
    
    
    //MARK:- RECORDERS
    /*
     Called after the controller's view is loaded into memory.
     */
    func recordeViewDidLoad(viewController: UIViewController) {
        delegates.forEach { (name, delegate) in
            delegate(name, viewController, ViewControllerLifecycleStates.didLoad)
        }
    }
    
    
    /*
     Notifies the view controller that its view is about to be added to a view hierarchy.
     */
    func recordeViewWillAppear(viewController: UIViewController, animated: Bool) {
        delegates.forEach { (name, delegate) in
            delegate(name, viewController, ViewControllerLifecycleStates.willAppear(animated: animated))
        }
    }
    
    /*
     Notifies the view controller that its view was added to a view hierarchy.
     */
    func recordeViewDidAppear(viewController: UIViewController, animated: Bool) {
        delegates.forEach { (name, delegate) in
            delegate(name, viewController, ViewControllerLifecycleStates.didAppear(animated: animated))
        }
    }
    
    /*
     Notifies the view controller that its view is about to be removed from a view hierarchy.
     */
    func recordeViewWillDisappear(viewController: UIViewController, animated: Bool) {
        delegates.forEach { (name, delegate) in
            delegate(name, viewController, ViewControllerLifecycleStates.willDisappear(animated: animated))
        }
    }
    
    /*
     Notifies the view controller that its view was removed from a view hierarchy.
     */
    func recordeViewDidDisappear(viewController: UIViewController, animated: Bool) {
        delegates.forEach { (name, delegate) in
            delegate(name, viewController, ViewControllerLifecycleStates.didDisappear(animated: animated))
        }
    }

    /*
     Notifies the container that the size of its view is about to change.
     */
    func recordeViewWillTransition(viewController: UIViewController, size: CGSize, coordinator: UIViewControllerTransitionCoordinator) {
        delegates.forEach { (name, delegate) in
            delegate(name, viewController, ViewControllerLifecycleStates.willTransition(size: size, coordinator: coordinator))
        }
    }
    
    func recordeDismiss(viewController: UIViewController) {
        delegates.forEach { (name, delegate) in
            delegate(name, viewController, ViewControllerLifecycleStates.didDismiss)
        }
    }
    
    
    //MARK:- dispose
    
    func dispose() {
        removeAllDelegates()
    }
    
    deinit {
        dispose()
    }
}
