//
//  View+StackCentered.swift
//  iosApp
//
//  Created by Rohen Giralt on 12/21/20.
//  Copyright © 2020 Rohen Giralt All rights reserved.
//

import SwiftUI

extension View {
    func vStackCentered() -> some View {
        VStack {
            Spacer()
            self
            Spacer()
        }
    }
    
    func hStackCentered() -> some View {
        HStack {
            Spacer()
            self
            Spacer()
        }
    }
    
    func stackCentered() -> some View {
        self
            .vStackCentered()
            .hStackCentered()
    }
}
