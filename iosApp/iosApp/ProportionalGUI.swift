//
//  ProportionalGUI.swift
//  iosApp
//
//  Created by Rohen Giralt on 3/7/21.
//  Copyright © 2021 com.rohengiralt. All rights reserved.
//

import SwiftUI

extension GeometryProxy {
    func proportionalWidth(proportion: CGFloat) -> CGFloat {
        let width = min(size.width, size.height)
        return proportion * width
    }
    
    func proportionalHeight(proportion: CGFloat) -> CGFloat {
        let height = max(size.width, size.height)
        return proportion * height
    }
}

extension CGFloat {
    var percent: CGFloat { self / 100 }
    
    func horizontalProportionOf(_ g: GeometryProxy) -> CGFloat {
        return g.proportionalWidth(proportion: self)
    }
    
    func verticalProportionOf(_ g: GeometryProxy) -> CGFloat {
        return g.proportionalHeight(proportion: self)
    }
}

extension Numeric {
    var percent: Self { self / 100._asOther() }
    
    func horizontalProportionOf(_ g: GeometryProxy) -> CGFloat {
        return g.proportionalWidth(proportion: CGFloat(fromNumeric: self))
    }
    
    func verticalProportionOf(_ g: GeometryProxy) -> CGFloat {
        return g.proportionalHeight(proportion: CGFloat(fromNumeric: self))
    }
}

extension View {
    public func center(geometryProxy g: GeometryProxy) -> some View {
        return position(x: 0.5.horizontalProportionOf(g), y: 0.5.verticalProportionOf(g))
    }
}
