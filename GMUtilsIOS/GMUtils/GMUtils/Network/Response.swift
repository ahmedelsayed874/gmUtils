//
//  Response.swift
//  Ahmed El-Sayed
//
//  Created by Imac on 8/27/19.
//  Copyright © 2019 OGTech. All rights reserved.
//

import UIKit

struct Response<T : Codable> : Codable {
    enum CodingKeys: String, CodingKey {
        case Status = "status"
        case Message = "message"
        case Data = "data"
    }
    
    var Status: String? = ResponseStatus.Failed
    var Message: String? = "" {
        didSet {
            if (Message == nil || Message == "") {
                Message = "Failed"
            }
        }
    }
    var Data : T? = nil
    
    
    var isSuccess: Bool {
        get {
            if Status == nil {
                return Data != nil
            } else {
                return Status?.lowercased() == ResponseStatus.Success.lowercased()
            }
        }
    }
    
    
}

class ResponseStatus  {
    static let Success = "Success"
    static let Failed = "Failed"
}

