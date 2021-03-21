//
// Created by Rohen Giralt on 3/14/21.
// Copyright (c) 2021 com.rohengiralt. All rights reserved.
//

import SwiftUI

struct ScaleButtonStyle: ButtonStyle {
    let scaleFactor: CGFloat

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .scaleEffect(configuration.isPressed ? scaleFactor : 1.0)
            .animation(.spring(response: 0.5), value: configuration.isPressed)
    }
}
