//
//  VisualEffectView.swift
//  iosApp
//
//  Created by Rohen Giralt on 12/23/20.
//  Copyright © 2020 com.rohengiralt. All rights reserved.
//

import SwiftUI

struct VisualEffectView: UIViewRepresentable {
    var effect: UIVisualEffect?
    func makeUIView(context: UIViewRepresentableContext<Self>) -> UIVisualEffectView { UIVisualEffectView() }
    func updateUIView(_ uiView: UIVisualEffectView, context: UIViewRepresentableContext<Self>) { uiView.effect = effect }
}

struct VisualEffectView_Previews: PreviewProvider {
    static var previews: some View {
        ZStack {
            Text("Blur me!")
            VisualEffectView(effect: UIBlurEffect(style: .regular))
        }
    }
}
