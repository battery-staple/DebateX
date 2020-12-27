package com.rohengiralt.debatex.model.sectionModel

import com.rohengiralt.debatex.settings.Setting
import com.rohengiralt.debatex.dataStructure.Image
import com.rohengiralt.debatex.dataStructure.SystemImage
import com.rohengiralt.debatex.datafetch.DataFetcher
import com.rohengiralt.debatex.model.event.EventModel
import com.rohengiralt.debatex.model.Model
import com.rohengiralt.debatex.model.speech.SpeechModel

//@Serializable
sealed class SectionModel : Model() {
    abstract val name: String
    abstract val icon: Image
}

//@Serializable
object NoSectionModel : Model() {
    const val text: String = "Please select a section."
}

//@Serializable
sealed class CardSectionModel<T : Model> : SectionModel() {
    abstract val pages: List<DataFetcher<T>>
}

//@Serializable
class EventsSectionModel(
    override val pages: List<DataFetcher<EventModel<*>>>
) : CardSectionModel<EventModel<*>>() {
    override val name: String get() = "Events"
    override val icon: Image get() = SystemImage.Timer
}

//@Serializable
class SpeechesSectionModel(
    override val pages: List<DataFetcher<SpeechModel<*>>>
) : CardSectionModel<SpeechModel<*>>() {
    override val name: String get() = "Speeches"
    override val icon: Image get() = SystemImage.Document

    val words: String = "Hello asdf lorem ipsum dolor sit amet."
}

//@Serializable
class SettingsSectionModel(val settings: List<Setting<*>>) : SectionModel() {
    override val name: String
        get() = "Settings"
    override val icon: Image
        get() = SystemImage.Settings

}