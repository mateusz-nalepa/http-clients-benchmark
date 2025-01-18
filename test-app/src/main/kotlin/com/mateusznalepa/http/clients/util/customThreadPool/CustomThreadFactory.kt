package com.mateusznalepa.http.clients.util.customThreadPool

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class CustomThreadFactory(
    private val prefix: String,
) : ThreadFactory {
    private val atomicCounter = AtomicInteger(0)

    override fun newThread(r: Runnable): Thread {
        val thread = Thread(r, prefix + "-" + atomicCounter.getAndIncrement())
        thread.setDaemon(true)
        thread.priority = Thread.MAX_PRIORITY
        return thread
    }
}
