package com.rohengiralt.debatex.datafetch

import com.rohengiralt.debatex.model.SectionsSelectionModel

class SectionsSelectionFetcher : DataFetcher<SectionsSelectionModel> {
    override fun fetch(): SectionsSelectionModel =
        SectionsSelectionModel(
            EventsSectionFetcher(),
            SettingsSectionFetcher()
        )
}