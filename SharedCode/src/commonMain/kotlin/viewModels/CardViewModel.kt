package com.rohengiralt.debatex.viewModels

import com.rohengiralt.debatex.dataStructures.color.DarkModeSafeColor
import com.rohengiralt.debatex.dataStructures.color.LightnessMode
import com.rohengiralt.debatex.dataStructures.color.SingleColor
import com.rohengiralt.debatex.dataStructures.text.Font
import com.rohengiralt.debatex.dataStructures.text.Text
import kotlin.jvm.JvmStatic

abstract class CardViewModel(
    title: Text,
    backgroundColor: DarkModeSafeColor = DEFAULT_BACKGROUND_COLOR
) : ViewModel(), BackgroundColored {
    override var backgroundColor: DarkModeSafeColor by updatesViewModelOnChange(backgroundColor)
        protected set

    var title: Text by updatesViewModelOnChange(title)
        protected set

    companion object {
        @JvmStatic // Kotlin doesn't support protected non-static members in companion objects on the JVM as of 1.3.71
        protected val DEFAULT_BACKGROUND_COLOR: DarkModeSafeColor = DarkModeSafeColor(
            lightModeColor = SingleColor.MEDIUM_GRAY.withOpacity(0.8),
            darkModeColor = SingleColor.MEDIUM_GRAY.withOpacity(0.8),
            mode = LightnessMode.Light
        )

        @JvmStatic
        protected val DEFAULT_TITLE_FONT: Font = Font.System
    }
}