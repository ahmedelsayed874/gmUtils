
//
//  JSONParser.swift
//  Ahmed El-Sayed
//
//  Created by Imac on 9/3/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import Foundation

/**
 * https://medium.com/@nimjea/json-parsing-in-swift-2498099b78f
 * https://medium.com/swift-india/use-of-codable-with-jsonencoder-and-jsondecoder-in-swift-4-71c3637a6c65
 */
class JSONManipulations {
    
    static func parseByJSONSerialization(data : Data) -> Any? {
        do{
            let jsonResponse = try JSONSerialization.jsonObject(with: data, options: [])
            Log.p(jsonResponse) //Response result
            
            return jsonResponse
        } catch let parsingError {
            Log.p("Error", parsingError)
            return nil
        }
    }
    
    //----------------------------------------------------------------------------------------------------
    
    static func encode<T : Codable>(_ value: T) -> Data? {
        let jsonEncoder = JSONEncoder()
        do {
            let jsonData = try jsonEncoder.encode(value)
            
            return jsonData
        }
        catch {
            return nil
        }
    }
    
    static func encodeToString<T : Codable>(_ value: T) -> String {
        if let jsonData = JSONManipulations.encode(value) {
            let jsonString = String(data: jsonData, encoding: .utf8)
            
            return jsonString!
        }
        
        return ""
        
    }
    
    static func decode<D: Codable>(type: D.Type, data : Data) -> D? {
        do {
            //here dataResponse received from a network request
            let decoder = JSONDecoder()
            let model = try decoder.decode(type, from: data) //Decode JSON Response Data
            return model
            
        } catch let parsingError {
            Log.p("Error", parsingError)
            return nil
        }
    }
    
}
