package com.rohengiralt.debatex.presets

import com.rohengiralt.debatex.datafetch.SectionsSelectionFetcher
import com.rohengiralt.debatex.datafetch.SettingsSectionFetcher
import com.rohengiralt.debatex.model.*
import com.rohengiralt.debatex.viewModel.SectionsSelectionViewModel

//@Suppress("UNUSED")
//val presetSectionsModel: SectionsSelectionModel = //TODO: Make file (JSON? YAML?) and deserialize at compile time w/annotation
//    SectionsSelectionModel(
//        SettingsSectionFetcher()
//    )

@Suppress("UNUSED")
val presetSectionSidebarViewModel: SectionsSelectionViewModel by lazy {
    SectionsSelectionViewModel(SectionsSelectionFetcher())
}