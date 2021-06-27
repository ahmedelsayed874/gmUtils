//
//  InstanceSearchModirator.swift
//
//  Created by GloryMaker on 5/15/21.
//

import Foundation

class InstantSearchModerator {
    
    private var searchingTextCache: String = ""
    private var isScheduled = false
    
    let delaySeconds: Float
    var delegate: ((String) -> Void)? = nil
    private (set) var lastSearchingText: String = ""
    
    
    init(delaySeconds: Float, delegate: @escaping (String) -> Void) {
        self.delaySeconds = delaySeconds
        self.delegate  = delegate
    }
    
    func updateText(text: String) {
        self.searchingTextCache = text
        
        if !isScheduled {
            scheduleDispatching()
            self.lastSearchingText = self.searchingTextCache
            self.delegate?(self.searchingTextCache)
        }
    }
    
    private func scheduleDispatching() {
        isScheduled = true
        
        let time = DispatchTime.init(uptimeNanoseconds: .init(delaySeconds * 1000 * 1000))
        
        DispatchQueue.main.asyncAfter(deadline: time) {[weak self] in
            guard let self = self else {return}
            
            if self.lastSearchingText != self.searchingTextCache {
                self.lastSearchingText = self.searchingTextCache
                self.delegate?(self.searchingTextCache)
            }
            
            self.isScheduled = false
        }
    }
    
    func stop() {
        self.delegate = nil
    }
    
    deinit {
        stop()
    }
}
