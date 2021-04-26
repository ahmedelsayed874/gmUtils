//
//  DateOp.swift
//  Choueifat
//
//  Created by Imac on 11/4/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import UIKit

class DateOp {
    static let FORMAT_PATTERN_HH_mm_ss = "HH:mm:ss"
    
    static let FORMAT_PATTERN_yyyy_MM_dd = "yyyy-MM-dd"
    static let FORMAT_PATTERN_yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm"
    static let FORMAT_PATTERN_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
    static let FORMAT_PATTERN_yyyy_MM_dd_HH_mm_ss_SS = "yyyy-MM-dd HH:mm:ss.SS"
    
    static let FORMAT_PATTERN_dd_MM_yyyy = "dd-MM-yyyy"
    static let FORMAT_PATTERN_dd_MM_yyyy_HH_mm_ss = "dd-MM-yyyy HH:mm:ss"
    
    static let FORMAT_PATTERN_yyyy_MM_dd__T__HH_mm_ss = "yyyy-MM-dd'T'HH:mm:ss"
    
    static let FORMAT_PATTERN_MMMM_dd_yyyy = "MMMM dd, yyyy"
    static let FORMAT_PATTERN_MMM_dd_yyyy_HH_mm_ss = "MMM dd, yyyy HH:mm:ss"
    static let FORMAT_PATTERN_EEEE_dd_MMMM_yyyy = "EEEE dd, MMMM yyyy"
    static let FORMAT_PATTERN_EEEE_dd_MMMM_yyyy_HH_mm = "EEEE dd, MMMM yyyy, HH:mm"
    static let FORMAT_PATTERN_EEEE_dd_MMMM_yyyy_hh_mm_a = "EEEE dd, MMMM yyyy, hh:mm a"
    
    //will complete later
    
//    private var date: Date
//
//    init(day: Int? = nil, month: Int? = nil, year: Int? = nil, hour:Int? = nil, minute: Int? = nil, second: Int? = nil, millisecond: Int? = nil) {
//        let today = Calendar.current
//        let dateString = "\(year ?? today.)-\()-\() \():\():\()"
//
//        today.
//
//    }
//
//
//    func addSeconds(amount: Int) {
//        date.addingTimeInterval(Double(amount))
//    }
    
    func format(date: String, fromFormatPattern: String, toFormatPattern: String) throws -> String {
        
        let formatter = DateFormatter()
        formatter.dateFormat = fromFormatPattern
        
        guard let dateObj = formatter.date(from: date) else {
            throw "Date Format Error"
        }
        
        formatter.dateFormat = toFormatPattern
        
        let fd = formatter.string(from: dateObj)
        return fd
    }
    
    func format(date: String, fromFormatPattern: String, toFormatPattern: String, cosiderTimeZone: Bool = false) throws -> String {
        
        let formatter = DateFormatter()
        formatter.dateFormat = fromFormatPattern
        
        var dateObj = formatter.date(from: date)
        if dateObj == nil {
            throw "Date Format Error"
        }
        
        if cosiderTimeZone {
            dateObj = considerCurrentTimeZone(date: dateObj!)
        }
        
        formatter.dateFormat = toFormatPattern
        
        let fd = formatter.string(from: dateObj!)
        return fd
    }
    
    func format(date: Date, toFormatPattern: String) throws -> String {
        
        let formatter = DateFormatter()
        formatter.dateFormat = toFormatPattern
        
        let fd = formatter.string(from: date)
        return fd
    }
    
    func parse(date: String, dateFormatPattern: String) throws -> Date {
        
        let formatter = DateFormatter()
        formatter.dateFormat = dateFormatPattern
        
        guard let dateObj = formatter.date(from: date) else {
            throw "Date Format Error"
        }
        
        return dateObj
    }
    
    func considerCurrentTimeZone(date: Date) -> Date {
        let currentDifferenceSec = TimeZone.current.secondsFromGMT()
        var date = date
//        Log.p("DATE_BEFORE_TIMEZONE: \(date)")
        date.addTimeInterval(TimeInterval(currentDifferenceSec))
//        Log.p("DATE_AFTER_TIMEZONE: \(date)")
        return date
    }
    
    //----------------------------------------------------------
    
    func tryFormat(date: String, toFormat: String, cosiderTimeZone: Bool = false) -> String {
        if date == "" { return "" }
        var result = ""
        let date =
            date.replacingOccurrences(of: "'T'", with: " ")
                .replacingOccurrences(of: "T", with: " ")
        
        do {
            result = try format(
                date: date,
                fromFormatPattern: DateOp.FORMAT_PATTERN_yyyy_MM_dd_HH_mm_ss,
                toFormatPattern: toFormat,
                cosiderTimeZone: cosiderTimeZone)
        } catch {
            do {
                result = try format(
                date: date,
                fromFormatPattern: DateOp.FORMAT_PATTERN_dd_MM_yyyy_HH_mm_ss,
                toFormatPattern: toFormat,
                cosiderTimeZone: cosiderTimeZone)
            } catch {
                do {
                    result = try format(
                        date: date,
                        fromFormatPattern: DateOp.FORMAT_PATTERN_yyyy_MM_dd,
                        toFormatPattern: toFormat,
                        cosiderTimeZone: cosiderTimeZone)
                } catch {
                    do {
                        result = try format(
                            date: date,
                            fromFormatPattern: DateOp.FORMAT_PATTERN_dd_MM_yyyy,
                            toFormatPattern: toFormat,
                            cosiderTimeZone: cosiderTimeZone)
                    } catch {
                        result = date
                    }
                }
            }
        }
        
        return result
    }
    
    func tryParse(date: String) -> Date? {
        let date =
            date.replacingOccurrences(of: "'T'", with: " ")
                .replacingOccurrences(of: "T", with: " ")
        
        do {
            return try parse(date: date, dateFormatPattern: DateOp.FORMAT_PATTERN_yyyy_MM_dd_HH_mm_ss)
        } catch {
            do {
                return try parse(date: date, dateFormatPattern: DateOp.FORMAT_PATTERN_dd_MM_yyyy_HH_mm_ss)
            } catch {
                do {
                    return try parse(date: date, dateFormatPattern: DateOp.FORMAT_PATTERN_dd_MM_yyyy)
                } catch {
                    do {
                        return try parse(date: date, dateFormatPattern: DateOp.FORMAT_PATTERN_yyyy_MM_dd)
                    } catch {
                        return nil
                    }
                }
            }
        }
    }
    
    //----------------------------------------------------------
    
    func isDateAfterToday(dateYMDHM: String)  -> Bool {
        let destinationDate = try! DateOp().parse(date: dateYMDHM, dateFormatPattern: DateOp.FORMAT_PATTERN_yyyy_MM_dd_HH_mm)
        let currentDate = Date()

        return destinationDate > currentDate
    }
    
}

extension String : Error {}
