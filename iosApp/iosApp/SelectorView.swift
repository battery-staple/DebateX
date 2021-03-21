//
//  SelectorView.swift
//  iosApp
//
//  Created by Rohen Giralt on 12/17/20.
//  Copyright © 2020 com.rohengiralt. All rights reserved.
//

import SwiftUI
import Combine
//import shared

struct SelectorView<Content : SwiftUI.View> : SwiftUI.View {
    let content: Content
    private let defaultNavBar: UINavigationBarAppearance
    private let transparentNavBar: UINavigationBarAppearance
    
    init(@ViewBuilder content: () -> Content) {
        self.content = content()
        
        defaultNavBar = UINavigationBar.appearance().standardAppearance

        let appearance = UINavigationBarAppearance()
                
        // this overrides everything you have set up earlier.
        appearance.configureWithTransparentBackground()

        transparentNavBar = appearance

        //In the following two lines you make sure that you apply the style for good
//        UINavigationBar.appearance().scrollEdgeAppearance = defaultNavBar
//        UINavigationBar.appearance().compactAppearance = defaultNavBar
//        UINavigationBar.appearance().standardAppearance = defaultNavBar
    }
    
//
//    private func setNavBarTransparency(to transparent: Bool) {
////        if transparent {
//            UINavigationBar.appearance().scrollEdgeAppearance = transparentNavBar
//            UINavigationBar.appearance().compactAppearance = transparentNavBar
//            UINavigationBar.appearance().standardAppearance = transparentNavBar
////        } else {
////            UINavigationBar.appearance().scrollEdgeAppearance = defaultNavBar
////            UINavigationBar.appearance().standardAppearance = defaultNavBar
////        }
//    }
    
    @State private var selectableViews: [SelectableView] = []
    @State private var currentSelectableView: SelectableView?
    @State private var defaultView: SelectableView?
    @State private var isShowingPopup: Bool = false
    @State private var currentTitle: String = ""
    @State private var popUpIsFullyHidden: Bool = true
    
//    @ViewBuilder
    private var currentView: SelectableView? {
        return currentSelectableView ?? defaultView
        
//        if let currentView = currentSelectableView ?? defaultView {
//            currentView
////                .navigationTitle(Text(currentView.name))
////                .navigationBarTitleDisplayMode(.automatic)
//        } else {
//            EmptyView()
//        }
    }
    
    var currentDisplayMode : NavigationBarItem.TitleDisplayMode {
        isShowingPopup ? .inline : .inline
    }
    
    var body: some SwiftUI.View {
        
//        if popUpIsFullyHidden {
            NavigationView {
                ZStack {
                    GeometryReader { g in
                        content
                            .hidden()
                            .frame(width: g.size.width, height: g.size.height)
                    }
                    currentView
                    if !popUpIsFullyHidden {
                        VisualEffectView(effect: UIBlurEffect(style: .regular))
                            .edgesIgnoringSafeArea(.all)
                            .transition(.opacity)
                    }
                    PopupView(selectableViews: $selectableViews, selectedView: $currentSelectableView, isPoppedUp: $isShowingPopup, isFullyHidden: $popUpIsFullyHidden)
                }
                .navigationBarItems(leading: Button { isShowingPopup = !isShowingPopup } label: {
                    Group {
                        if isShowingPopup {
                            Image(systemName: "xmark")
                        } else {
                            Image(systemName: "line.horizontal.3")
                        }
                    }
                    .font(.title)
                    .transition(.opacity)
                    .animation(.linear(duration: 0.1))
                })
                .navigationTitle(Text(currentTitle))
                .navigationBarTitleDisplayMode(currentDisplayMode)
//                .configureNavigationBar { //TODO: Make navbar disappear
//                    print("running!")
//                    print("popup? \(self.isShowingPopup), \(self.popUpIsFullyHidden)")
//                    $0.navigationBar.barTintColor = self.isShowingPopup ? .blue : .black
//                    $0.navigationBar.standardAppearance = self.isShowingPopup ? transparentNavBar : defaultNavBar
//                    $0.navigationBar.compactAppearance = self.isShowingPopup ? transparentNavBar : defaultNavBar
//                    $0.navigationBar.scrollEdgeAppearance = self.isShowingPopup ? transparentNavBar : defaultNavBar
//                    print("popup? \(self.isShowingPopup), \(self.popUpIsFullyHidden)")
//                }
            }
            .onChange(of: popUpIsFullyHidden) { nowHidden in
                if nowHidden {
                    if let currentView = currentView, currentView.showTitle {
                        currentTitle = currentView.name
                    } else {
                        currentTitle = ""
                    }
//                    setNavBarTransparency(to: false)
//                    print("")
                } else {
                    currentTitle = ""
//                    setNavBarTransparency(to: true)
//                    print("transparent!")
                }
            }
            .onPreferenceChange(SelectorPreferenceKey.self) { selectableViews in
                self.selectableViews = selectableViews
            }
            .onPreferenceChange(DefaultSelectorPreferenceKey.self) { defaultView in
                self.defaultView = defaultView
                if let currentView = currentView, currentView.showTitle {
                    currentTitle = currentView.name
                } else {
                    currentTitle = ""
                }
            }
            
//        } else {
//            NavigationView {
//                ZStack {
//                    GeometryReader { g in
//                        content
//                            .hidden()
//                            .frame(width: g.size.width, height: g.size.height)
//                    }
//                    currentView
//                    if !popUpIsFullyHidden {
//                        VisualEffectView(effect: UIBlurEffect(style: .regular))
//                    }
//                    PopupView(selectableViews: $selectableViews, selectedView: $currentSelectableView, isPoppedUp: $isShowingPopup, isFullyHidden: $popUpIsFullyHidden)
//                }
//                .navigationBarItems(leading: Button { isShowingPopup = !isShowingPopup } label: {
//                    Group {
//                        if isShowingPopup {
//                            Image(systemName: "xmark")
//                        } else {
//                            Image(systemName: "line.horizontal.3")
//                        }
//                    }
//                    .font(.title)
//                    .transition(.opacity)
//                    .animation(.linear(duration: 0.1))
//                })
//                .navigationTitle(Text(currentTitle))
//                .navigationBarTitleDisplayMode(currentDisplayMode)
//            }
//            .onChange(of: popUpIsFullyHidden) { nowHidden in
//                if nowHidden {
//                    currentTitle = currentView?.name ?? ""
//                } else {
//                    currentTitle = ""
//                }
//            }
//            .onPreferenceChange(SelectorPreferenceKey.self) { selectableViews in
//                self.selectableViews = selectableViews
//            }
//            .onPreferenceChange(DefaultSelectorPreferenceKey.self) { defaultView in
//                self.defaultView = defaultView
//            }
//        }
    }
}

fileprivate struct PopupView: SwiftUI.View {

    @Binding private var selectableViews: [SelectableView]
    @Binding private var selectedView: SelectableView?
    @Binding private var isPoppedUp: Bool
    @Binding private var isFullyHidden: Bool
//    @Binding private var currentTitle: String
//    let content: SelectableView?
    
    init(selectableViews: Binding<[SelectableView]>, selectedView: Binding<SelectableView?>, isPoppedUp: Binding<Bool>, isFullyHidden: Binding<Bool>) {
        self._selectableViews = selectableViews
        self._selectedView = selectedView
        self._isPoppedUp = isPoppedUp
        self._showingButtons = State(initialValue: selectableViews.wrappedValue.map { _ in isPoppedUp.wrappedValue })
        self._isFullyHidden = isFullyHidden
//        self._currentTitle = currentTitle
//        self.content = content()
//        print("popped & buttons")
//        print(self.isPoppedUp)
//        print(self.selectableViews)
//        print(self.showingButtons)
    }
    
    @State private var showingButtons: [Bool]
    
    static func newAnimationTimer() -> Publishers.Autoconnect<SwiftUI.Timer.TimerPublisher> {
        SwiftUI.Timer.publish(every: 0.1, on: .main, in: .common)
            .autoconnect()
    }
    
    @State private var animationTimer: Publishers.Autoconnect<SwiftUI.Timer.TimerPublisher>? = nil
//    private var animationTimerOrEmpty: AnyPublisher<Date, Never> {
//        animationTimer?.eraseToAnyPublisher() ?? Empty().eraseToAnyPublisher()
//    }
    
    func resizeShowingButtons(newSize: Int) {
        let fillWith = showingButtons.last ?? isPoppedUp
        let newViews = newSize - showingButtons.count
        
        if newViews > 0 {
            showingButtons = showingButtons + Array(repeating: fillWith, count: newViews)
        } else if newViews < 0 {
            showingButtons = showingButtons.dropLast(-newViews)
        }
        
//        print("update to \(showingButtons)")
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            ForEach(Array(selectableViews.enumerated()), id: \.0) { (index, selectableView) in
                Button { selectedView = selectableView; isPoppedUp = false } label: {
                    HStack {
                        Group {
                            selectableView.icon
                            Text(selectableView.name)
                        }
                        .font(.system(size: 40))
                    }
                }
                .opacity(showingButtons[safe: index] ?? false ? 1.0 : 0.0)
                .animation(.easeInOut(duration: 0.15), value: showingButtons[safe: index] ?? false)
            }
        }
        .stackCentered()
        .onChange(of: isPoppedUp) { aboutToPopUp in
//            print("atpo: \(aboutToPopUp)")
            if showingButtons.indices.contains(0) {
                showingButtons[0] = aboutToPopUp
            }
            animationTimer = PopupView.newAnimationTimer()
        }
        .onChange(of: selectableViews) { newSelectableViews in
            resizeShowingButtons(newSize: newSelectableViews.count)
        }
        .onChange(of: showingButtons) { _ in
            withAnimation {
                isFullyHidden = showingButtons.allSatisfy { !$0 }
            }
        }
        .onReceive(animationTimer?.eraseToAnyPublisher() ?? Empty().eraseToAnyPublisher()) { time in
            if let index = showingButtons.firstIndex(where: { $0 != isPoppedUp }), showingButtons.indices.contains(index) {
                showingButtons[index] = isPoppedUp
            } else {
                animationTimer?.upstream.connect().cancel()
                animationTimer = nil
            }
        }
        .onAppear {
            showingButtons = selectableViews.map { _ in isPoppedUp }
        }
    }
}

public extension View {
    @ViewBuilder
    private func maybeDefaultSelectable(selectableView: SelectableView, setAsDefaultView: Bool) -> some View {
        if setAsDefaultView {
            preference(key: DefaultSelectorPreferenceKey.self, value: selectableView)
        } else {
            self
        }
    }
    
    func selectable(name: String, icon: SwiftUI.Image, default setAsDefaultView: Bool = false, showTitle: Bool = false) -> some View {
        let selectableView = SelectableView(view: AnyViewContainer(self), icon: icon, name: name, showTitle: showTitle)
        
        return preference(key: SelectorPreferenceKey.self, value: [selectableView])
            .maybeDefaultSelectable(selectableView: selectableView, setAsDefaultView: setAsDefaultView)
    }
    
    func selectable(name: String, iconName: String, default setAsDefaultView: Bool = false, showTitle: Bool = false) -> some View {
        let selectableView = SelectableView(view: AnyViewContainer(self), icon: Image(systemName: iconName), name: name, showTitle: showTitle)
        
        return preference(key: SelectorPreferenceKey.self, value: [selectableView])
            .maybeDefaultSelectable(selectableView: selectableView, setAsDefaultView: setAsDefaultView)
    }
}

fileprivate struct AnyViewContainer : View, Equatable {
    let view: AnyView
    private let uuid: UUID = UUID()
        
    init<V : View>(erasing view: V) {
        self.view = AnyView(view)
    }
    
    init<V : View>(_ view: V) {
        self.init(erasing: view)
    }
    
    var body: some View { view }
    
    static func == (lhs: AnyViewContainer, rhs: AnyViewContainer) -> Bool {
        lhs.uuid == rhs.uuid
    }
}

fileprivate struct SelectableView : Hashable, View {
    let view: AnyViewContainer
    let icon: SwiftUI.Image
    let name: String
    let showTitle: Bool
    
    var body: some View {
        view
    }
    
    static func == (lhs: SelectableView, rhs: SelectableView) -> Bool {
        lhs.name == rhs.name && lhs.view == rhs.view
    }
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(name)
    }
}

fileprivate struct SelectorPreferenceKey : PreferenceKey {
    static let defaultValue: [SelectableView] = []
    static func reduce(value: inout [SelectableView], nextValue: () -> [SelectableView]) {
        value.append(contentsOf: nextValue())
    }
}

fileprivate struct DefaultSelectorPreferenceKey : PreferenceKey {
    static let defaultValue: SelectableView? = nil
    static func reduce(value: inout SelectableView?, nextValue: () -> SelectableView?) {
        if let nextValue = nextValue() {
            value = nextValue
        }
    }
}

struct SelectorView_Previews: PreviewProvider {
    static var previews: some View {
        SelectorView {
            ScrollView(.vertical) {
                HStack {
                    Spacer()
                        VStack {
                            ForEach(0..<50) { index in
                                NavigationLink(
                                    destination: Text("Subview1! \(index)")
                                ) {
                                    Text("This is a view1").padding()
                                }
                            }
                        }
                    Spacer()
                }
            }
                .selectable(name: "view1", iconName: "doc", default: true)
            
            ScrollView(.vertical) {
                HStack {
                    Spacer()
                        VStack {
                            ForEach(0..<50) { index in
                                NavigationLink(
                                    destination: Text("Subview2! \(index)")
                                ) {
                                    Text("This is a view2").padding()
                                }
                            }
                        }
                    Spacer()
                }
            }
                .selectable(name: "view2", iconName: "gear")
            
            ScrollView(.vertical) {
                HStack {
                    Spacer()
                        VStack {
                            ForEach(0..<50) { index in
                                NavigationLink(
                                    destination: Text("Subview3! \(index)")
                                ) {
                                    Text("This is a view3").padding()
                                }
                            }
                        }
                    Spacer()
                }
            }
                .selectable(name: "view3", iconName: "timer")
        }
    }
}
