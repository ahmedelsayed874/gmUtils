//
//  Thread.swift
//
//  Created by GloryMaker on 5/15/21.
//

import Foundation

typealias ThreadDelegate = () -> Void

class Thread {
    
    private var deadline: DispatchTime? = nil
    private var delegate: ThreadDelegate? = nil
    
    init(delegate: @escaping () -> Void) {
        self.delegate = delegate
    }
    
    init(deadline: DispatchTime?, delegate: @escaping ThreadDelegate) {
        self.delegate = delegate
        self.deadline = deadline
    }
    
    func start() {
        if deadline == nil {
            DispatchQueue.global().async {
                self.delegate?()
                self.delegate = nil
            }
        } else {
            DispatchQueue.global().asyncAfter(deadline: deadline!, execute: {
                self.delegate?()
                self.delegate = nil
            })
        }
    }
    
    //--------------------------------------------------------------------------
    
    static func runOnUIThread(deadline: DispatchTime? = nil, _ delegate: @escaping ThreadDelegate) {
        if deadline == nil {
            DispatchQueue.main.async {
                delegate()
            }
        } else {
            DispatchQueue.main.asyncAfter(deadline: deadline!) {
                delegate()
            }
        }
    }
}
