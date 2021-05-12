//
//  DateOp.swift
//  Ahmed El-Sayed
//
//  Created by Imac on 11/4/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import UIKit

class DateOp {
    static let FORMAT_PATTERN_HH_mm_ss = "HH:mm:ss"
    
    static let FORMAT_PATTERN_yyyy_MM_dd = "yyyy-MM-dd"
    static let FORMAT_PATTERN_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
    static let FORMAT_PATTERN_yyyy_MM_dd_HH_mm_ss_SS = "yyyy-MM-dd HH:mm:ss.SS"
    
    static let FORMAT_PATTERN_dd_MM_yyyy = "dd-MM-yyyy"
    static let FORMAT_PATTERN_dd_MM_yyyy_HH_mm_ss = "dd-MM-yyyy HH:mm:ss"
    
    static let FORMAT_PATTERN_yyyy_MM_dd__T__HH_mm_ss = "yyyy-MM-dd'T'HH:mm:ss"
    
    static let FORMAT_PATTERN_MMMM_dd_yyyy = "MMMM dd, yyyy"
    static let FORMAT_PATTERN_MMM_dd_yyyy_HH_mm_ss = "MMM dd, yyyy HH:mm:ss"
    static let FORMAT_PATTERN_MMMM_yyyy = "MMMM yyyy"
    static let FORMAT_PATTERN_EEEE_dd_MMMM_yyyy = "EEEE dd, MMMM yyyy"
    static let FORMAT_PATTERN_EEEE_dd_MMMM_yyyy_HH_mm = "EEEE dd, MMMM yyyy, HH:mm"
    static let FORMAT_PATTERN_EEEE_dd_MMMM_yyyy_hh_mm_a = "EEEE dd, MMMM yyyy, hh:mm a"
    
    static let ONE_DAY_MILLIS = 86_400_000; // (1d * 24h * 60m * 60s * 1000ms);
    static let ONE_HOUR_MILLIS = 3_600_000; // (0d * 01h * 60m * 60s * 1000ms);
    static let ONE_MINUTE_MILLIS = 60_000; //  (0d * 00h * 01m * 60s * 1000ms);
    static let TEN_SECOND_MILLIS = 10_000; //  (0d * 00h * 00m * 10s * 1000ms);
    static let ONE_SECOND_MILLIS = 1_000; //   (0d * 00h * 00m * 01s * 1000ms);
    
    
    
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
    
    func convertTimeToString(timeDiffSec: Double, en: Bool) -> String {
        var timeInMillis: Int = Int(timeDiffSec * 1000.0)
        
        var string = ""
        
        let standardIntervals = [
            0: DateOp.ONE_DAY_MILLIS,
            1: DateOp.ONE_HOUR_MILLIS,
            2: DateOp.ONE_MINUTE_MILLIS,
            3: DateOp.ONE_SECOND_MILLIS
        ]
        let intervalNamesEn = [
            0: "day",
            1: "hour",
            2: "minute",
            3: "second"
        ]
        let intervalNamesAr = [
            0:  ["يوم", "يومان", "أيام"],
            1:   ["ساعة", "ساعتان", "ساعات"],
            2:   ["دقيقة", "دقيقتان", "دقائق"],
            3: ["ثانية", "ثانيتين", "ثواني"]
        ]
        
        Log.p("convertTimeToString -> time: \(timeInMillis)")
        
        var isNeg = false
        
        if timeInMillis < 0 {
            timeInMillis *= -1
            isNeg = true
        }
        
        for i in 0..<standardIntervals.count {
            let x = Int(timeInMillis / standardIntervals[i]!)
            
            Log.p("convertTimeToString ->[\(i)] \(timeInMillis)/\(standardIntervals[i]!) ==> \(x) \(intervalNamesEn[i]!)")
            
            if x > 0 {
                if string.count > 0 {
                    if en {
                        string.append(", ")
                    } else {
                        string.append(" و")
                    }
                }
                
                if en {
                    string += "\(x) "
                    string += "\(intervalNamesEn[i]!)";
                    if x > 1 {
                        string.append("s");
                    }
                } else {
                    if x == 1 {
                        string += " \(intervalNamesAr[i]![0])"
                    } else if x == 2 {
                        string += " \(intervalNamesAr[i]![1])"
                    } else {
                        string += Utils.convertEnglishNumbersToArabicNumber("\(x)")
                        string += " \(intervalNamesAr[i]![2])"
                    }
                }
                
                timeInMillis -= x * standardIntervals[i]!;
            }
        }
        
        if isNeg {
            string = "(-) \(string)"
        } else {
            string = "(+) \(string)"
        }
        
        return string
    }
}

extension String : Error {}
