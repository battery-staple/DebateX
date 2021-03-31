//
// Created by Rohen Giralt on 3/11/21.
// Copyright (c) 2021 com.rohengiralt. All rights reserved.
//

import SwiftUI

struct PagerView<Data, Content> : View where Data : RandomAccessCollection, Content : View {

    @Binding var index: Int
    @State private var offset: CGFloat = 0
    @State private var isGestureActive: Bool = false

    private let pageWidth: CGFloat
    private let pages: [Content]

    init(data: Data, currentPage index: Binding<Int>, pageWidth: CGFloat, pageGenerator: (Int, Data.Element) -> Content) {
        self._index = index
        let data = data.map { $0 }
        self.pageWidth = pageWidth
        pages = (0..<data.count).map { i in
            pageGenerator(i, data[i])
        }
    }

    private func calcOffset(geometry: GeometryProxy) -> CGFloat {
        -pageWidth * CGFloat(index) + ((-pageWidth / 2 + geometry.size.width / 2) as CGFloat)
    }

    var body: some View {
        GeometryReader {
            geometry in
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(alignment: .center, spacing: 0) {
                    ForEach(pages.indices) { index in
                        pages[index]
                            .frame(width: pageWidth, alignment: .center)
                    }
                }
                    .frame(alignment: .center)
            }
                .content.offset(x: isGestureActive ? offset : calcOffset(geometry: geometry))
                .animation(.easeInOut, value: isGestureActive)
                .animation(.easeInOut, value: index)
                .frame(width: pageWidth, height: nil, alignment: .leading)
                .simultaneousGesture(DragGesture()
                    .onChanged { value in
                        if abs(value.translation.width) > abs(2 * value.translation.height) || isGestureActive {
                            self.isGestureActive = true
                            self.offset = value.translation.width + -pageWidth * CGFloat(index)
                        }
                    }
                    .onEnded { value in
                        if isGestureActive {
//                            withAnimation {
                                self.isGestureActive = false
//                            }
                            if -value.predictedEndTranslation.width > pageWidth / 2, index < pages.endIndex - 1 {
//                                withAnimation {
                                    self.index += 1
//                                }
                            }
                            if value.predictedEndTranslation.width > pageWidth / 2, index > 0 {
//                                withAnimation {
                                    self.index -= 1
//                                }
                            }
//                            withAnimation {
//                                self.offset = -pageWidth * CGFloat(index)
//                            }
//                            withAnimation {
//                                self.isGestureActive = false
//                            }
                        }
                    }, including: isGestureActive ? .gesture : .all
                )
        }
    }
}
