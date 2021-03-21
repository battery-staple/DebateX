//package com.rohengiralt.debatex.viewModel.section
//
//import com.rohengiralt.debatex.dataStructure.Image
//import com.rohengiralt.debatex.dataStructure.ObservableMutableList
//import com.rohengiralt.debatex.dataStructure.text.Text
//import com.rohengiralt.debatex.datafetch.DataFetcher
//import com.rohengiralt.debatex.datafetch.EventsSectionFetcher
//import com.rohengiralt.debatex.datafetch.SectionFetcher
//import com.rohengiralt.debatex.datafetch.SettingsSectionFetcher
//import com.rohengiralt.debatex.datafetch.SpeechesSectionFetcher
//import com.rohengiralt.debatex.model.event.EventModel
//import com.rohengiralt.debatex.model.Model
//import com.rohengiralt.debatex.model.sectionModel.*
//import com.rohengiralt.debatex.settings.Setting
//import com.rohengiralt.debatex.settings.applicationSettings
//import com.rohengiralt.debatex.viewModel.OldViewModel
//import com.rohengiralt.debatex.viewModel.ViewModel
//import com.rohengiralt.debatex.viewModel.ViewModelOnly
//import com.rohengiralt.debatex.viewModel.event.EventViewModel
//
//@Suppress("FunctionName")
//fun OldSectionViewModel(sectionModelFetcher: DataFetcher<SectionModel>): SectionViewModel<*> =
//    (sectionModelFetcher as? EventsSectionFetcher)?.let(::EventsSectionViewModel)
//        ?: (sectionModelFetcher as? SpeechesSectionFetcher)?.let(::SpeechesSectionViewModel)
//        ?: (sectionModelFetcher as? SettingsSectionFetcher)?.let(::SettingsSectionViewModel)
//        ?: throw IllegalArgumentException("Cannot create a SectionViewModel from fetcher $sectionModelFetcher")
//
//@ViewModelOnly
//sealed class OldSectionViewModel(onMainPageAtStart: Boolean = true) :
//    ViewModel() {
//    open val onMainPage: Boolean by observingChangeOf(onMainPageAtStart)
//
//    open val name: String get() = model.name
//    open val icon: Image get() = model.icon
//
//    open fun onAppear() {}
//    open fun onDisappear() {}
//
////    companion object {
////        private val logger = loggerForClass<SectionViewModel<*>>()
////    }
//}
//
////class NoSectionViewModel : ViewModel<NoSectionModel>() {
////    override val modelFetcher: DataFetcher<NoSectionModel> =
////
////    val text: Text
////        get() =
////            Text(
////                rawText = model.text,
////                height = null
////            )
////}
//
//sealed class OldCardSectionViewModel<T : Model, U : OldViewModel<out T>>(
//    @Suppress("UNUSED") val viewModels: List<U>,
//) : SectionViewModel<CardSectionModel<T>>() {
//    private val pagesOpenedState: ObservableMutableList<Boolean> by observable(
//        ObservableMutableList(viewModels.size) { false }
//    )
//
//    fun pageOpenDidChange(at: Int, to: Boolean) {
//        pagesOpenedState[at] = to
//    }
//
//    fun pageIsOpen(at: Int): Boolean = pagesOpenedState[at]
//
//    @Suppress("SimplifyBooleanWithConstants")
//    final override val onMainPage: Boolean
//        get() = pagesOpenedState.all { pageOpened -> pageOpened == false }
//}
//
//class OldEventsSectionViewModel(
//    override val modelFetcher: DataFetcher<CardSectionModel<EventModel<*>>>,
//) : CardSectionViewModel<EventModel<*>, EventViewModel<*>>(
//    modelFetcher.fetch().pages.map(::EventViewModel)
//) {
//
////    val events: List<EventModel<*>>
////        get() {
////            val unsorted = loadPresetEvents() + loadCustomEvents()
////            unsorted.splitByFavorited.forEach {
////
////            }
////        }
////
////    private inline fun loadPresetEvents(): List<EventModel<*>> { //TODO: Make events indexible
////        return presetEvents //TODO: Get from file
////    }
////
////    private inline fun loadCustomEvents(): List<EventModel<*>> {
////        return emptyList()
////    }
////
////    private val List<EventModel<*>>.splitByFavorited: List<Pair<String, List<EventModel<*>>?>>
////        inline get() =
////            groupBy { it.favorited }
////                .let {
////                    listOf(
////                        FAVORITES_HEADER to it[true],
////                        NOT_FAVORITED_HEADER to it[false]
////                    )
////                }
////
////    companion object {
////        private const val FAVORITES_HEADER = "Favorites"
////        private const val NOT_FAVORITED_HEADER = "Other"
////    }
//}
//
//class OldSpeechesSectionViewModel(
//    override val modelFetcher: DataFetcher<SpeechesSectionModel>,
//) : SectionViewModel<SpeechesSectionModel>() {
//    @Suppress("UNUSED")
//    val centerText: Text
//        get() = Text(rawText = model.words, height = null)
//}
//
//class OldSettingsSectionViewModel(override val modelFetcher: SectionFetcher<SettingsSectionModel>) :
//    SectionViewModel<SettingsSectionModel>() {
//
//    val settings: List<Pair<String, Setting<*>>>
//        get() = model.settings.map {
//            when (it.name) {
//                "countStrategy" -> "Timers Count Direction"
//                "playSoundOnComplete" -> "Play Sound On Complete"
//                "initialHue" -> "Initial Hue"
//                else -> it.name
//            } to it
//        }
//
//    override fun onDisappear() {
//        applicationSettings.update()
//    }
//}