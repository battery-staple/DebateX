//
//  CommonInterop.swift
//  iosApp
//
//  Created by Rohen Giralt on 12/17/20.
//  Copyright © 2020 com.rohengiralt. All rights reserved.
//

import SwiftUI
import shared

extension ViewModel : ObservableObject {}

extension SwiftUI.Image {
    init(from sharedImage: shared.Image) {
        switch sharedImage {
        case let assetImage as AssetImage:
            self.init(assetImage.imageName)
        case let systemImage as SystemImage:
            self.init(systemName: systemImage.sfSymbolName)
        default:
            fatalError(XkcdErrorKt.xkcdError.message!)
        }
    }
}

extension SwiftUI.Color {
    init(_ sharedColor: shared.Color) {
        let rgb: ColorRepresentation.RGB = SwiftInteropKt.asRGB(color: sharedColor)
        self.init(red: rgb.red, green: rgb.green, blue: rgb.blue, opacity: 1.0)
    }
    
    public static let offWhite = Color(red: 0.13, green: 0.73, blue: 0.35)
    
    func components() -> (r: CGFloat, g: CGFloat, b: CGFloat, a: CGFloat) {

        let scanner = Scanner(string: self.description.trimmingCharacters(in: CharacterSet.alphanumerics.inverted))
        var hexNumber: UInt64 = 0
        var r: CGFloat = 0.0, g: CGFloat = 0.0, b: CGFloat = 0.0, a: CGFloat = 0.0

        let result = scanner.scanHexInt64(&hexNumber)
        if result {
            r = CGFloat((hexNumber & 0xff000000) >> 24) / 255
            g = CGFloat((hexNumber & 0x00ff0000) >> 16) / 255
            b = CGFloat((hexNumber & 0x0000ff00) >> 8 ) / 255
            a = CGFloat( hexNumber & 0x000000ff)        / 255
        }
        return (r, g, b, a)
    }
}

