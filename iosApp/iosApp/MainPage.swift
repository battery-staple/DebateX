//
//  MainPage.swift
//  iosApp
//
//  Created by Rohen Giralt on 8/4/20.
//  Copyright © 2020 com.rohengiralt. All rights reserved.
//

import SwiftUI

struct MainPage: View {
    var body: some View {
        SelectorView {
            EventsSectionView()
                .selectable(name: "Events", iconName: "timer", default: true, showTitle: false)

            SettingsSectionView()
                .selectable(name: "Settings", iconName: "gear", showTitle: true)
        }
    }
}

struct MainPage_Previews: PreviewProvider {
    static var previews: some View {
        MainPage()
    }
}
