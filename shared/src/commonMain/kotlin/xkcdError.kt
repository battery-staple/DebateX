package com.rohengiralt.debatex

/**
 * An error thrown on encountering an unreachable state.
 *
 * This error should only be thrown at times where it is
 * possible to guarantee that a given state is impossible
 * to reach, but that the compiler is not capable of inferring.
 *
 * Do not throw an [xkcdError] when it is at all possible
 * for the state causing it to throw to occur; this includes
 * times in which an illegal state could be reached due to
 * code not conforming to an arbitrary, but not compile-time
 * guaranteed, restriction.
 */
val xkcdError: Error
    inline get() = Error(
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