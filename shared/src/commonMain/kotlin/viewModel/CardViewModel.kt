package com.rohengiralt.debatex.viewModel

import com.rohengiralt.debatex.Configurable
import com.rohengiralt.debatex.dataStructure.color.DarkModeSafeColor
import com.rohengiralt.debatex.dataStructure.color.LightnessMode
import com.rohengiralt.debatex.dataStructure.color.SingleColor
import com.rohengiralt.debatex.dataStructure.text.Font
import com.rohengiralt.debatex.dataStructure.text.Text
import com.rohengiralt.debatex.model.Model

abstract class CardViewModel<T : Model>(
    title: Text,
    val cornerRadius: Double,
    backgroundColor: DarkModeSafeColor? = null
) : ViewModel<T>(), BackgroundColored {
    override var backgroundColor: DarkModeSafeColor by observingChangeOf(
        backgroundColor ?: DEFAULT_BACKGROUND_COLOR
    )
        protected set

    var title: Text by observingChangeOf(title)
        protected set

//    abstract var size: Size

    protected companion object {
        @Configurable
        val DEFAULT_BACKGROUND_COLOR: DarkModeSafeColor = DarkModeSafeColor(
            lightModeColor = SingleColor.MEDIUM_GRAY.withOpacity(0.8),
            darkModeColor = SingleColor.MEDIUM_GRAY.withOpacity(0.8),
            mode = LightnessMode.Light
        )

        @Configurable
        val DEFAULT_TITLE_FONT: Font = Font.System
    }
}