//
// Created by Rohen Giralt on 3/12/21.
// Copyright (c) 2021 Rohen Giralt All rights reserved.
//

import SwiftUI
import shared

struct SettingsSectionView : View {
    @ObservedObject var viewModel: SettingsSectionViewModel = SettingsSectionViewModel()

    var body: some View {
        Form {
            ForEach(viewModel.settings, id: \.self) { setting in
                Section {
                    switch setting as AnyObject {
                    case let setting as SwitchSettingViewModel:
                        Toggle(
                            setting.name,
                            isOn: Binding {
                                Bool(truncating: setting.currentEntry)
                            } set: {
                                setting.currentEntry = KotlinBoolean(value: $0)
                            }
                        )
                    case let setting as TextFieldSettingViewModel:
                    TextField(
                        setting.name,
                        text: Binding {
                            (setting as AnyObject as! TextFieldSettingViewModel).currentEntry
                        } set: {
                            (setting as AnyObject as! TextFieldSettingViewModel).currentEntry = $0
                        }
                    ).keyboardType(
                        setting.keyboardType == TextFieldSettingViewModel.KeyboardType.text ? .alphabet :
                            setting.keyboardType == TextFieldSettingViewModel.KeyboardType.numeric ? .numberPad :
                            setting.keyboardType == TextFieldSettingViewModel.KeyboardType.numericDecimal ? .decimalPad :
                            .default
                    )
                    case let setting as PickerSettingViewModel<AnyObject>:
                        VStack {
                            Text(setting.name) //SegmentedPickerStyle doesn't show a name for the setting
                            Picker(
                                setting.name,
                                selection:
                                    Binding {
                                        setting.currentEntry
                                    } set: {
                                        setting.currentEntry = $0
                                    }
                            ) {
                                ForEach(setting.possibleSelections, id: \.self) { option in
                                    Text(option.name)
                                }
                            }
                                .pickerStyle(SegmentedPickerStyle())
                        }
                    default: EmptyView()
                    }
//                Text(String(describing: setting))
//                Text(String(describing: setting.type))
                }
            }
        }
        .onDisappear {
            print("Saving!")
            print()
            print()
            viewModel.updateAllSettings()
        }
    }
}
