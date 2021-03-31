//
//  CGFloatCasts.swift
//  iosApp
//
//  Created by Rohen Giralt on 3/7/21.
//  Copyright © 2021 com.rohengiralt. All rights reserved.
//

import Foundation
import CoreGraphics

protocol Numeric : Comparable {
    init(_ v:Float)
    init(_ v:Double)
    init(_ v:Int)
    init(_ v:UInt)
    init(_ v:Int8)
    init(_ v:UInt8)
    init(_ v:Int16)
    init(_ v:UInt16)
    init(_ v:Int32)
    init(_ v:UInt32)
    init(_ v:Int64)
    init(_ v:UInt64)
    init(_ v:CGFloat)

    static func * (lhs: Self, rhs: Self) -> Self
    static func / (lhs: Self, rhs: Self) -> Self
    static func + (lhs: Self, rhs: Self) -> Self
    static func - (lhs: Self, rhs: Self) -> Self
    
    // 'shadow method' that allows instances of Numeric
    // to coerce themselves to another Numeric type
    func _asOther<T:Numeric>() -> T
}

extension Numeric {

    // Default implementation of init(fromNumeric:) simply gets the inputted value
    // to coerce itself to the same type as the initialiser is called on
    // (the generic parameter T in _asOther() is inferred to be the same type as self)
    init<T:Numeric>(fromNumeric numeric: T) { self = numeric._asOther() }
}

// Implementations of _asOther() – they simply call the given initialisers listed
// in the protocol requirement (it's required for them to be repeated like this,
// as the compiler won't know which initialiser you're referring to otherwise)
extension Float   : Numeric {func _asOther<T:Numeric>() -> T { return T(self) }}
extension Double  : Numeric {func _asOther<T:Numeric>() -> T { return T(self) }}
extension CGFloat : Numeric {func _asOther<T:Numeric>() -> T { return T(self) }}
extension Int     : Numeric {func _asOther<T:Numeric>() -> T { return T(self) }}
extension Int8    : Numeric {func _asOther<T:Numeric>() -> T { return T(self) }}
extension Int16   : Numeric {func _asOther<T:Numeric>() -> T { return T(self) }}
extension Int32   : Numeric {func _asOther<T:Numeric>() -> T { return T(self) }}
extension Int64   : Numeric {func _asOther<T:Numeric>() -> T { return T(self) }}
extension UInt    : Numeric {func _asOther<T:Numeric>() -> T { return T(self) }}
extension UInt8   : Numeric {func _asOther<T:Numeric>() -> T { return T(self) }}
extension UInt16  : Numeric {func _asOther<T:Numeric>() -> T { return T(self) }}
extension UInt32  : Numeric {func _asOther<T:Numeric>() -> T { return T(self) }}
extension UInt64  : Numeric {func _asOther<T:Numeric>() -> T { return T(self) }}
