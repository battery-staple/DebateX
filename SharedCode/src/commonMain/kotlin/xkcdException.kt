package com.jetbrains.debatex

class xkcdException : IllegalStateException(
    """
    |If you're seeing this, the code is in what I thought was an unreachable state.
    |
    |I could give you advice for what to do. But honestly, why should you trust me?
    |I clearly screwed this up. I'm writing a message that should never appear, 
    |yet I know it will probably appear someday.
    |
    |On a deep level, I know I'm not up to this task. I'm so sorry.
    |https://www.xkcd.com/2200/
    """.trimMargin()
)