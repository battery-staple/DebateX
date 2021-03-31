//
// Created by Rohen Giralt on 3/11/21.
// Copyright (c) 2021 com.rohengiralt. All rights reserved.
//

import SwiftUI

extension View {
    func inverseMask<Mask>(_ mask: Mask) -> some View where Mask: View {
        GeometryReader { proxy in
            self.mask(
                mask
                    .frame(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
                    .foregroundColor(.black)
                    .background(Color.white)
                    .compositingGroup()
                    .luminanceToAlpha()
            )
        }
    }
}