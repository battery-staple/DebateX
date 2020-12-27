package com.rohengiralt.debatex.datafetch

import com.rohengiralt.debatex.model.TimerCountStrategy
import com.rohengiralt.debatex.settings.Settings
import com.rohengiralt.debatex.model.TimerModel
import com.rohengiralt.debatex.settings.applicationSettings
import com.soywiz.klock.TimeSpan

class SettingsCountStrategyTimerFetcher(var totalTime: TimeSpan) : DataFetcher<TimerModel> {
    override fun fetch(): TimerModel =
        TimerModel(
            totalTime,
//            TimerCountStrategy.CountDown
            applicationSettings.countStrategy.value.also { println("Timer model with strategy of $it") }
        )
}