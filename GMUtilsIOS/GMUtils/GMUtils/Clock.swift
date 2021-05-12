//
//  CTimer.swift
//  Ahmed El-Sayed
//
//  Created by Imac on 09/03/2021.
//  Copyright © 2021 OGTech. All rights reserved.
//

import Foundation


class Clock {
    
    typealias ClockTaskDelegate = (/*clock ticks*/Int, /*observer name*/ String,/*updated offset*/Double) -> Void
    
    private class ClockTask {
        let offset: Double
        var updatedTime: Double
        private (set) var delegate: ClockTaskDelegate?
        
        init(offset: Double, delegate: ClockTaskDelegate?) {
            self.offset = offset
            self.updatedTime = offset
            self.delegate = delegate
        }
        
        func destroy() {
            delegate = nil
        }
    }
    
    private var clock: Timer? = nil
    private var clockTasks = [String : ClockTask]()
    
    func registerClockObserver(name: String, offset: Double, delegate: ClockTaskDelegate?) {
        clockTasks.updateValue(ClockTask(offset: offset, delegate: delegate), forKey: name)
        //log("registerClockObserver", name, Double(clockTicks))
        //if clock == nil {start()}
    }
    
    func unregisterClockObserver(name: String) {
        clockTasks.removeValue(forKey: name)
        //log("unregisterClockObserver", name, Double(clockTicks))
        //if clockTasks.count == 0 {reset()}
    }
    
    func removeAllObserver() {
        clockTasks.forEach { (k, v) in v.destroy()}
        clockTasks.removeAll()
        //log("removeAllObserver", "", 0.0)
    }
    
    func getUpdatedTime(name: String) -> Double? {
        //log("getUpdatedTime", name, 0.0)
        return clockTasks[name]?.updatedTime
    }
    
    //-------------------------------------------------------------------------------------------------------------------
    
    private (set) var elapsedTime = 0
    
    func start() {
        self.clock = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) {[weak self] (timer) in
            guard let self = self else {return}
            self.onClockTick()
        }
    }
    
    private func onClockTick() {
        self.elapsedTime += 1
        //log("onClockTick", "ticks", Double(clockTicks))
        
        self.clockTasks.forEach { (k, v) in
            v.updatedTime += 1
            log("onClockTick", k, v.updatedTime)
        }
        
        DispatchQueue.main.async {
            self.clockTasks.forEach { (k, v) in
                v.delegate?(self.elapsedTime, k, v.updatedTime)
            }
        }
    }
    
    func stop() {
        clock?.invalidate()
        clock = nil
    }
    
    func reset() {
        elapsedTime = 0
        removeAllObserver()
    }
    
    func stopAndReset() {
        stop()
        reset()
    }
    
    //-------------------------------------------------------------------------------------------------------------------
    
    private func log(_ method: String, _ name: String, _ tick: Double) {
//        if Log.inDebugMode {
//            Log.p("Clock", method, name, "time: \(Date(timeIntervalSince1970: tick))")//, "Thread: \(Thread.current)")
//        }
    }
     
    deinit {
        stopAndReset()
    }
}
