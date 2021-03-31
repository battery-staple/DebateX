package com.rohengiralt.debatex.model.event

//import com.rohengiralt.debatex.dataStructure.Named
//import com.rohengiralt.debatex.dataStructure.SimpleName
//import com.rohengiralt.debatex.dataStructure.competitionTypes.Speaker
//import com.rohengiralt.debatex.datafetch.ConstantModelFetcher
//import com.rohengiralt.debatex.datafetch.DataFetcher
//import com.rohengiralt.debatex.model.Model
//import com.rohengiralt.debatex.model.timerModel.TimerModel
//import kotlinx.serialization.Serializable
//
//@Serializable
//data class SecondaryTimerModel<out T : Speaker<*>>(
//    val name: String,
//    val timerModel: TimerModel<*>,
//    val speakers: List<T>
//) : Model() {
//    constructor(namedBy: SimpleName, timerModelFetcher: DataFetcher<TimerModel>, belongingTo: T) :
//            this(namedBy, timerModelFetcher, listOf(belongingTo))
//
//    constructor(namedBy: SimpleName, timerModel: TimerModel, belongingTo: List<T>) :
//            this(namedBy, ConstantModelFetcher(timerModel), belongingTo)
//
//    constructor(namedBy: SimpleName, timerModel: TimerModel, belongingTo: T) :
//            this(namedBy, ConstantModelFetcher(timerModel), listOf(belongingTo))
//
//    init {
//        require(speakers.isNotEmpty())
//    }
//}