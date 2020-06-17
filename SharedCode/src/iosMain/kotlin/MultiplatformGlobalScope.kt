package com.rohengiralt.debatex

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.CoroutineContext

actual class MultiplatformGlobalScope : CoroutineScope {
    private val dispatcher = MainDispatcher()
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = dispatcher + job
}

//@InternalCoroutinesApi
//private object MainLoopDispatcher : CoroutineDispatcher(), Delay {
//
//    override fun dispatch(context: CoroutineContext, block: Runnable) {
//        dispatch_async(dispatch_get_main_queue()) {
//            block.run()
//        }
//    }
//
//
//    @ExperimentalCoroutinesApi
//    override fun scheduleResumeAfterDelay(
//        timeMillis: Long,
//        continuation: CancellableContinuation<Unit>
//    ) {
//        dispatch_after(
//            dispatch_time(DISPATCH_TIME_NOW, timeMillis * 1_000/*_000*/),
//            dispatch_get_main_queue()
//        ) {
//            with(continuation) {
//                resumeUndispatched(Unit)
//            }
//        }
//    }
//
////    @InternalCoroutinesApi
//    override fun invokeOnTimeout(timeMillis: Long, block: Runnable): DisposableHandle {
//        val handle = object : DisposableHandle {
//            var disposed = false
//                private set
//
//            override fun dispose() {
//                disposed = true
//            }
//        }
//        dispatch_after(
//            dispatch_time(DISPATCH_TIME_NOW, timeMillis * 1_000/*_000*/),
//            dispatch_get_main_queue()
//        ) {
//            if (!handle.disposed) {
//                block.run()
//            }
//        }
//
//        return handle
//    }
//
//}

private class MainDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatch_get_main_queue()) {
            block.run()
        }
    }
}