//
//  Collection+SafeSubscript.swift
//  iosApp
//
//  Created by Rohen Giralt on 12/17/20.
//  Copyright © 2020 com.rohengiralt. All rights reserved.
//

import Foundation

extension Collection {
    subscript (safe index: Index) -> Element? {
        get {
            indices.contains(index) ? self[index] : nil
        }
    }
}
