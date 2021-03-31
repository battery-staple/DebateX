//
//  EventCardView.swift
//  iosApp
//
//  Created by Rohen Giralt on 3/7/21.
//  Copyright © 2021 Rohen Giralt All rights reserved.
//

import SwiftUI
import shared

struct EventCardView: View {
    @ObservedObject var viewModel: EventCardViewModel
    let scrollGeometry: GeometryProxy
    @Namespace var card
    
    private static let animationLength = 0.3
    private static let animation = Animation.easeInOut(duration: animationLength)
    
    var body: some View {
        VStack {
            HStack {
                Text(viewModel.title)
                    .font(Font.title.weight(.semibold))
                    .foregroundColor(.primary)
                    .padding(.leading)
                Spacer()
                Text(viewModel.subtitle)
                    .font(Font.title3.weight(.light))
                    .foregroundColor(.secondary)
                Spacer()
                Button { viewModel.open() } label: {
                    Image(systemName: "arrow.right.circle.fill")
                        .font(Font.title.weight(.semibold))
                        .background(Circle().foregroundColor(.white))
                }
            }.frame(height: 0.1.horizontalProportionOf(scrollGeometry))
            
            if viewModel.showingInfo {
                VStack {
                    if let captionedImages = viewModel.captionedImages {
                        HStack {
                            ForEach(captionedImages, id: \.self) { captionedImage in
                                VStack {
                                    Image(from: captionedImage.image)
                                        .resizable()
                                        .aspectRatio(contentMode: .fit)
                                    if let caption = captionedImage.caption {
                                        Text(caption)
                                            .font(.title)
                                            .fontWeight(.medium)
                                            .foregroundColor(Color.primary.opacity(0.6))
                                    }
                                }
                            }
                        }
                    }
                    
                    Text(viewModel.body)
                        .font(.title)
                        .foregroundColor(.secondary)
                        .padding(.vertical)
                }.transition(
                    .asymmetric(
                        insertion: AnyTransition.opacity.animation(EventCardView.animation.delay(EventCardView.animationLength)),
                        removal: .identity
                    )
                )
            }
        }
        .padding(0.015.horizontalProportionOf(scrollGeometry))
        .background(
            Button { viewModel.showingInfo = !viewModel.showingInfo } label: {
                RoundedRectangle(
                    cornerRadius: 0.065.horizontalProportionOf(scrollGeometry),
                    style: .continuous
                )
                    .foregroundColor(Color.gray.opacity(0.3))
                    .animation(EventCardView.animation, value: viewModel.showingInfo)
            }
        )
        .animation(EventCardView.animation)
        .padding(.horizontal)
    }
}

struct EventCardView_Previews: PreviewProvider {
    static var previews: some View {
        GeometryReader { g in
            VStack {
                EventCardView(viewModel: EventsSectionViewModel().cards[2], scrollGeometry: g)
            }
        }
    }
}
