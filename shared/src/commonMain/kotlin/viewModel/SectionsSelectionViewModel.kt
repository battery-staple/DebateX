package com.rohengiralt.debatex.viewModel

import com.rohengiralt.debatex.dataStructure.Button
import com.rohengiralt.debatex.datafetch.DataFetcher
import com.rohengiralt.debatex.datafetch.SectionFetcher
import com.rohengiralt.debatex.datafetch.SectionsSelectionFetcher
import com.rohengiralt.debatex.model.SectionsSelectionModel
import com.rohengiralt.debatex.model.sectionModel.SectionModel

class SectionsSelectionViewModel(override val modelFetcher: SectionsSelectionFetcher) :
    ViewModel<SectionsSelectionModel>() {

    @Suppress("WEAKER_ACCESS")
    val buttons: List<Button<SectionViewModel<*>>> = //TODO: "by lazy" here causes segfault idek
        model.sections.mapNotNull { sectionFetcher ->
            (sectionFetcher as? SectionFetcher<*>)?.let {
                SectionViewModel(sectionFetcher).let { sectionViewModel ->
                    Button(sectionViewModel) {
                        currentPage = sectionViewModel
                        logger.info("updated currentPage to $sectionViewModel (now $currentPage)")
                    }
                }
            }
        }

    @Suppress("WEAKER_ACCESS")
    var currentPage: SectionViewModel<*>? by observingChangeOf(if (buttons.isEmpty()) null else buttons.first().display) { new, old ->
        old?.onDisappear() ?: logger.info("No old to disappear")
        new?.onAppear() ?: logger.info("No new to appear")
    }
}
