package com.rohengiralt.debatex.viewModel

//abstract class CardViewModel<T : Model>(
//    title: Text,
//    val cornerRadius: Double,
//    backgroundColor: DarkModeSafeColor? = null
//) : OldViewModel<T>(), BackgroundColored {
//    override var backgroundColor: DarkModeSafeColor by observingChangeOf(
//        backgroundColor ?: DEFAULT_BACKGROUND_COLOR
//    )
//        protected set
//
//    var title: Text by observingChangeOf(title)
//        protected set
//
////    abstract var size: Size
//
//    protected companion object {
//        @Configurable
//        val DEFAULT_BACKGROUND_COLOR: DarkModeSafeColor = DarkModeSafeColor(
//            lightModeColor = SingleColor.MEDIUM_GRAY.withOpacity(0.8),
//            darkModeColor = SingleColor.MEDIUM_GRAY.withOpacity(0.8),
//            mode = LightnessMode.Light
//        )
//
//        @Configurable
//        val DEFAULT_TITLE_FONT: Font = Font.System
//    }
//}