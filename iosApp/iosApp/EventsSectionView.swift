//
//  EventsSectionView.swift
//  iosApp
//
//  Created by Rohen Giralt on 3/7/21.
//  Copyright © 2021 com.rohengiralt. All rights reserved.
//

import SwiftUI
import shared

struct EventsSectionView: View {
    @ObservedObject var viewModel = EventsSectionViewModel()
    
    var body: some View {
        GeometryReader { scrollGeometry in
//            NavigationView {
                ScrollView(.vertical, showsIndicators: true) {
                    VStack(spacing: 0.01.verticalProportionOf(scrollGeometry)) {
                        Spacer(minLength: 0.01.verticalProportionOf(scrollGeometry))
                        HStack {
                            Text("Events")
                                .font(Font.largeTitle)
                                .fontWeight(.bold)
                                .multilineTextAlignment(.leading)
                                .padding(.leading)
                            Spacer()
                        }
                        NavigationLink(
                            destination: Group {
                                if let eventViewModel = viewModel.currentEvent {
                                    EventView(viewModel: eventViewModel)
                                }
                            },
                            isActive: Binding<Bool> { viewModel.showingEvent } set: { viewModel.showingEvent = $0 }
                        ) {
                            EmptyView()
                        }
                        ForEach(viewModel.cards, id: \.self) { card in
                            EventCardView(viewModel: card, scrollGeometry: scrollGeometry)
                        }
                  }
                }
//            }
        }
    }
}

struct EventsSectionView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            EventsSectionView()
        }
    }
}
