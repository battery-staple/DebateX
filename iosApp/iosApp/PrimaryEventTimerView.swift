//
//  MainEventTimer.swift
//  iosApp
//
//  Created by Rohen Giralt on 3/10/21.
//  Copyright © 2021 com.rohengiralt. All rights reserved.
//

import SwiftUI
import shared

struct PrimaryEventTimerView: View {
    @ObservedObject var viewModel: TimerViewModel
    @Binding var presentationState: EventView.PresentationState
    
    private let strokeWidth: CGFloat = 20
    
    var body: some View {
        GeometryReader { proxy in
            Button {
                viewModel.isRunning.toggle()
            } label: {
                ZStack {
                    Circle()
                        .strokeBorder(Color.white.opacity(0.3), style: StrokeStyle(lineWidth: strokeWidth))

                    Circle()
                        .inset(by: strokeWidth / 2)
                        .trim(from: 0, to: CGFloat(viewModel.progress))
                        .stroke(Color.white.opacity(0.6), style: StrokeStyle(lineWidth: strokeWidth, lineCap: .round))
                        .rotationEffect(.degrees(-90))
                        .shadow(color: Color(white: 0.3).opacity(0.8), radius: 5)
                        .mask(
                            Circle()
                                .inset(by: strokeWidth / 2)
                                .stroke(style: StrokeStyle(lineWidth: strokeWidth, lineCap: .round))
                        )

                    Circle()
                        .inset(by: strokeWidth / 2)
                        .trim(from: 0, to: CGFloat(viewModel.progress))
                        .stroke(Color.white.opacity(0.5), style: StrokeStyle(lineWidth: strokeWidth, lineCap: .round))
                        .rotationEffect(.degrees(-90))

                    Circle()
                        .inset(by: strokeWidth)
                        .foregroundColor(Color(white: 0.7))
                        .opacity(0.5)
                        .scaledToFit()

                    VStack(spacing: (1 / 64.0).verticalProportionOf(proxy)) {
                        Text(viewModel.name.shortNameOrLong)
                            .frame(maxHeight: (3 / 32.0).verticalProportionOf(proxy))
                                .font(.system(size: (3 / 32.0).verticalProportionOf(proxy), weight: .bold))

                        Text(viewModel.timeString)
                            .foregroundColor(.black)
                            .font(.system(size: (5 / 16.0).horizontalProportionOf(proxy), weight: .light))

                    }.offset(y: -(3 / 64.0 + 1 / 128.0).verticalProportionOf(proxy))
                }
            }
                .buttonStyle(ScaleButtonStyle(scaleFactor: 0.8))
                .contentShape(Circle().inset(by: strokeWidth)) // make tappable area just the circle
        }
        .onChange(of: viewModel.isRunning) { isRunning in
            if isRunning {
                withAnimation {
                    presentationState = .showingResetButton
                }
            } else {
                withAnimation {
                    presentationState = .showingSecondaryTimers
                }
            }
        }
    }
}

struct MainEventTimer_Previews: PreviewProvider {
    static let eventViewModel: EventViewModel = {
        let svm = EventsSectionViewModel()
        svm.cards[0].open()
        return svm.currentEvent!
    }()

    @State static var presentationState: EventView.PresentationState = .showingSecondaryTimers

    static var previews: some View {
        PrimaryEventTimerView(viewModel: eventViewModel.primaryTimers[0], presentationState: $presentationState)
            .background(LinearGradient(gradient: Gradient(colors: [Color(eventViewModel.currentTopColor), Color(eventViewModel.currentBottomColor)]), startPoint: .top, endPoint: .bottom))
            .edgesIgnoringSafeArea(.all)
    }
}
