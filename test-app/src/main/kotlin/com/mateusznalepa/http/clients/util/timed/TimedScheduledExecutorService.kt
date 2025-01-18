package com.mateusznalepa.http.clients.util.timed

import java.util.concurrent.*

//class TimedScheduledExecutorService(
//    private val delegate: ScheduledExecutorService
//) : ScheduledExecutorService {
//    override fun execute(command: Runnable) {
//        delegate.execute(TimedRunnable(command))
//    }
//
//    override fun shutdown() {
//        delegate.shutdown()
//
//    }
//
//    override fun shutdownNow(): MutableList<Runnable> {
//        return delegate.shutdownNow()
//    }
//
//    override fun isShutdown(): Boolean {
//        return delegate.isShutdown
//    }
//
//    override fun isTerminated(): Boolean {
//        return delegate.isTerminated
//    }
//
//    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
//        return delegate.awaitTermination(timeout, unit)
//    }
//
//    override fun <T : Any?> submit(task: Callable<T>): Future<T> {
//        return delegate.submit(TimedCallable(task))
//    }
//
//    override fun <T : Any?> submit(task: Runnable, result: T): Future<T> {
//        return delegate.submit(TimedRunnable(task), result)
//
//    }
//
//    override fun submit(task: Runnable): Future<*> {
//        return delegate.submit(TimedRunnable(task))
//    }
//
//    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>): MutableList<Future<T>> {
//        return delegate.invokeAll(tasks.map { TimedCallable(it) })
//    }
//
//    override fun <T : Any?> invokeAll(
//        tasks: MutableCollection<out Callable<T>>,
//        timeout: Long,
//        unit: TimeUnit
//    ): MutableList<Future<T>> {
//        return delegate.invokeAll(tasks.map { TimedCallable(it) }, timeout, unit)
//    }
//
//    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>): T {
//        return delegate.invokeAny(tasks.map { TimedCallable(it) })
//    }
//
//    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): T {
//        return delegate.invokeAny(tasks.map { TimedCallable(it) }, timeout, unit)
//    }
//
//    override fun schedule(command: Runnable, delay: Long, unit: TimeUnit): ScheduledFuture<*> {
//        return delegate.schedule(TimedRunnable(command), delay, unit)
//    }
//
//    override fun <V : Any?> schedule(callable: Callable<V>, delay: Long, unit: TimeUnit): ScheduledFuture<V> {
//        return delegate.schedule(TimedCallable(callable), delay, unit)
//    }
//
//    override fun scheduleAtFixedRate(
//        command: Runnable,
//        initialDelay: Long,
//        period: Long,
//        unit: TimeUnit
//    ): ScheduledFuture<*> {
//        return delegate.scheduleAtFixedRate(TimedRunnable(command), initialDelay, period, unit)
//    }
//
//    override fun scheduleWithFixedDelay(
//        command: Runnable,
//        initialDelay: Long,
//        delay: Long,
//        unit: TimeUnit
//    ): ScheduledFuture<*> {
//        return delegate.scheduleWithFixedDelay(TimedRunnable(command), initialDelay, delay, unit)
//    }
//}
