package com.mateusznalepa.http.clients.util.timed

import io.micrometer.core.instrument.Metrics
import java.time.Duration
import java.util.concurrent.Callable

class TimedRunnable(
    private val delegate: Runnable,
) : Runnable {

    private val start = System.nanoTime()

    override fun run() {
        recordMetrics(start, "runnable")
        delegate.run()
    }
}

class TimedCallable<T>(
    private val delegate: Callable<T>,
) : Callable<T> {

    private val start = System.nanoTime()

    override fun call(): T {
        recordMetrics(start, "callable")
        return delegate.call()
    }

}


private fun recordMetrics(startNanos: Long, source: String) {
    val endDummyClient = System.nanoTime()
    val dummyClientDuration = Duration.ofNanos(endDummyClient - startNanos)
    Metrics
        .timer(
            "thread_pool_task_waiting_time",
            "thread", Thread.currentThread().name,
            "source", source,
        )
        .record(dummyClientDuration)
}

