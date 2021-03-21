//
//  EventView.swift
//  iosApp
//
//  Created by Rohen Giralt on 3/7/21.
//  Copyright © 2021 Rohen Giralt All rights reserved.
//

import SwiftUI
import shared

struct EventView: View {
    enum PresentationState {
        case showingSecondaryTimers
        case showingResetButton
        indirect case dragging(distance: CGFloat, from: PresentationState)

        func offset(given: GeometryProxy) -> CGFloat {
            switch self {
            case .showingSecondaryTimers: return 0
            case .showingResetButton: return given.size.height/2 - primaryTimerSize(given: given)/2
            case .dragging(distance: let distance, from: let oldPosition):
                return max(PresentationState.showingSecondaryTimers.offset(given: given),
                        min(PresentationState.showingResetButton.offset(given: given),
                            oldPosition.offset(given: given) * swipeSpeedMultiplier + distance
                        )
                    )
            }
        }

        func progressTowards(_ state: PresentationState, given geometry: GeometryProxy) -> Double {
            Double(1 - abs(offset(given: geometry) - state.offset(given: geometry)) / (PresentationState.showingResetButton.offset(given: geometry) - PresentationState.showingSecondaryTimers.offset(given: geometry)))
        }
    }

    @StateObject var viewModel: EventViewModel
    @State private var presentationState: PresentationState = .showingSecondaryTimers

    @inline(__always) private static func primaryTimerSize(given geometry: GeometryProxy) -> CGFloat {
        0.95.horizontalProportionOf(geometry)
    }

    static let swipeSpeedMultiplier: CGFloat = 0.5

    var body: some View {
        GeometryReader { geometry in
            VStack(content: {
                Spacer(minLength: 0)
                Button(action: { viewModel.resetCurrent() }) {
                    Image("arrow.clockwise.medium")
                        .renderingMode(.template)
                        .resizable()
                        .aspectRatio(1, contentMode: .fit)
                        .padding()
                        .foregroundColor(Color(white: 1.0).opacity(0.6))
                        .opacity(presentationState.progressTowards(.showingResetButton, given: geometry) * 0.8)
                        .frame(
                            height: min(150, max(0, presentationState.offset(given: geometry))),
                            alignment: .center
                        )
                        .background(
                            Image("arrow.clockwise.medium")
                                .resizable()
                                .aspectRatio(1, contentMode: .fit)
                                .padding()
                                .shadow(color: Color.black.opacity(0.3), radius: 3)
                                .frame(
                                    height: min(150, max(0, presentationState.offset(given: geometry))),
                                    alignment: .center
                                )
                                .inverseMask(
                                    Image("arrow.clockwise.medium")
                                        .resizable()
                                        .aspectRatio(1, contentMode: .fit)
                                        .padding()
                                        .foregroundColor(Color(white: 0.0))
                                        .frame(
                                            height: min(150, max(0, presentationState.offset(given: geometry))),
                                            alignment: .center
                                        )
                                )
                        )
                }
                    .buttonStyle(ScaleButtonStyle(scaleFactor: 0.8))

                PagerView(
                    data: viewModel.primaryTimers,
                    currentPage: Binding {
                        Int(viewModel.currentPageIndex)
                    } set: {
                        viewModel.currentPageIndex = Int32($0)
                    },
                    pageWidth: UIScreen.main.bounds.width
                ) { i, vm in
                    PrimaryEventTimerView(viewModel: vm, presentationState: $presentationState)
                }
                    .frame(width: EventView.primaryTimerSize(given: geometry), height: EventView.primaryTimerSize(given: geometry))
                    .layoutPriority(2)
                    .hStackCentered()

                if let secondaryTimers = viewModel.secondaryTimers {
                    PagerView(
                        data: secondaryTimers,
                        currentPage: Binding {
                            Int(truncating: viewModel.currentSecondaryTimerIndex!)
                        } set: { value in
                            viewModel.currentSecondaryTimerIndex = KotlinInt(int: Int32(value))
                        },
                        pageWidth: 1.horizontalProportionOf(geometry)
                    ) { i, vm in
                        SecondaryEventTimerView(viewModel: vm)
                            .opacity(presentationState.progressTowards(.showingSecondaryTimers, given: geometry) / 2 + 0.5)
                            .frame(width: 0.8.horizontalProportionOf(geometry))
                    }
                        .layoutPriority(1)
                        .frame(minWidth: geometry.size.width, maxHeight: 0.35.verticalProportionOf(geometry))
                }
                Spacer(minLength: 0)
            })
                .navigationBarTitle(viewModel.title, displayMode: .inline)
                .compositingGroup()
                .simultaneousGesture(
                    DragGesture(minimumDistance: 30)
                        .onChanged { drag in
                            switch presentationState {
                            case .showingSecondaryTimers, .showingResetButton:
                                if abs(drag.predictedEndTranslation.height) > abs(2*drag.predictedEndTranslation.width) {
                                    self.presentationState = .dragging(distance: drag.translation.height, from: presentationState)
                                } else { break }
                            case .dragging(_, from: let lastState):
                                self.presentationState = .dragging(distance: drag.translation.height, from: lastState)
                            }
                        }
                        .onEnded { _ in
                            if case .dragging = presentationState {
                                if presentationState.offset(given: geometry) <
                                       (PresentationState.showingSecondaryTimers.offset(given: geometry) +
                                           PresentationState.showingResetButton.offset(given: geometry))/2 {
                                    withAnimation(.some(.spring(response: 0.1))) { presentationState = .showingSecondaryTimers }
                                } else {
                                    withAnimation(.some(.spring(response: 0.1))) { presentationState = .showingResetButton }
                                }
                            }
                        },
                    including: { if case .dragging = presentationState { return .gesture } else { return .all } }()
                )
        }
            .background(
                LinearGradient(
                    gradient: Gradient(colors: [SwiftUI.Color(viewModel.currentTopColor), SwiftUI.Color(viewModel.currentBottomColor)]),
                    startPoint: .top,
                    endPoint: .bottom
                )
                    .edgesIgnoringSafeArea(.all)
            )
            .navigationBarItems(trailing: Button {
                viewModel.resetAll()
            } label: {
                Text("Reset All")
            })
            .onAppear {
                UIApplication.shared.isIdleTimerDisabled = true
            }
            .onDisappear {
                UIApplication.shared.isIdleTimerDisabled = false

                viewModel.stopAll()
            }
    }
}

//struct SecondaryTimers<T : AnyObject>: View {
//    let pageGeometry: GeometryProxy
//    let secondaryTimers: [TimerViewModel<T>]
//    @Binding var currentPage: Int
//
//    var body: some View {
//        SwiftUIPagerView(data: self.secondaryTimers, currentPage: self.$currentPage, pageWidth: 0.8.horizontalProportionOf(pageGeometry)) { index, timer in
//            PrepTimeView(timer: timer)
//        }
//            .layoutPriority(1)
//            .frame(minWidth: 1.horizontalProportionOf(pageGeometry), maxHeight: 0.35.verticalProportionOf(pageGeometry))
//    }
//}

struct EventView_Previews: PreviewProvider {
    static let sectionViewModel: EventsSectionViewModel = {
        let svm = EventsSectionViewModel()
        svm.cards[2].open()
        return svm
    }()
    
    static var previews: some View {
        EventView(viewModel: sectionViewModel.currentEvent!)
            
    }
}
