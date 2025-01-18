package com.mateusznalepa.http.clients.util.customThreadPool

import com.mateusznalepa.http.clients.util.timed.TimedExecutorService
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Component
class CustomSchedulerCreator(
    private val meterRegistry: MeterRegistry,
) {

    fun create(threads: Int): Scheduler {

        val threadPoolName = "customPool"

        val threadPoolExecutor =
            ThreadPoolExecutor(
                threads,
                threads,
                1,
                TimeUnit.HOURS,
                LinkedBlockingQueue(100_000),
                CustomThreadFactory(threadPoolName)
            )

        threadPoolExecutor.prestartAllCoreThreads()

        val proxy = TimedExecutorService(threadPoolExecutor)

        return Schedulers.fromExecutorService(proxy)
    }

}
