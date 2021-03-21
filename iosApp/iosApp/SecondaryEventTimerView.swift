//
// Created by Rohen Giralt on 3/12/21.
// Copyright (c) 2021 Rohen Giralt All rights reserved.
//

import SwiftUI
import shared

struct SecondaryEventTimerView: View {
    @ObservedObject var viewModel: TimerViewModel

    var body: some View {
        GeometryReader { geometry in
            VStack {
                Text(viewModel.name.longName)
                    .font(Font.system(size: 0.12.horizontalProportionOf(geometry)))
                    .foregroundColor(.black)

                HStack {
                    Button(action: { viewModel.isRunning.toggle() }) {
                        Text(viewModel.timeString)
                            .lineLimit(1)
                            .font(.system(size: 0.3.horizontalProportionOf(geometry))).minimumScaleFactor(0.1)
                            .foregroundColor(.black)
                            .fixedSize()
                    }

                    Button(action: {
                        viewModel.reset()
                    }) {
                        Image(systemName: "arrow.clockwise")
                            .font(.system(size: 0.15.horizontalProportionOf(geometry), weight: .medium))
                            .foregroundColor(Color(red: 0.0, green: 0.0, blue: 0.8, opacity: 1.0))
                            .scaledToFill()
                    }
                }

            }
                .padding(0.025.horizontalProportionOf(geometry))
                .background(
                    RoundedRectangle(cornerRadius: 20)
                        .foregroundColor(Color(white: 0.8, opacity: 0.6))
                        .shadow(radius: 5)
                        .frame(width: geometry.size.width + 0.025.horizontalProportionOf(geometry))
                )
                .position(x: geometry.frame(in: .local).midX, y: geometry.frame(in: .local).midY)
        }
//            .padding(.horizontal)
    }
}