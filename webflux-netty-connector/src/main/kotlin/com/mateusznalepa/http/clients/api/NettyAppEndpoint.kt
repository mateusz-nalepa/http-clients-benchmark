package com.mateusznalepa.http.clients.api

import com.mateusznalepa.http.clients.client.DummyClient
import com.mateusznalepa.http.clients.client.MockServerResponse
import io.micrometer.core.instrument.Metrics
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.math.BigInteger
import java.time.Duration
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger


class TimedScheduledExecutorService(
    private val delegate: ScheduledExecutorService
) : ScheduledExecutorService {
    override fun execute(command: Runnable) {
        delegate.execute(TimedRunnable(command))
    }

    override fun shutdown() {
        delegate.shutdown()

    }

    override fun shutdownNow(): MutableList<Runnable> {
        return delegate.shutdownNow()
    }

    override fun isShutdown(): Boolean {
        return delegate.isShutdown
    }

    override fun isTerminated(): Boolean {
        return delegate.isTerminated
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        return delegate.awaitTermination(timeout, unit)
    }

    override fun <T : Any?> submit(task: Callable<T>): Future<T> {
        return delegate.submit(TimedCallable(task))
    }

    override fun <T : Any?> submit(task: Runnable, result: T): Future<T> {
        return delegate.submit(TimedRunnable(task), result)

    }

    override fun submit(task: Runnable): Future<*> {
        return delegate.submit(TimedRunnable(task))
    }

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>): MutableList<Future<T>> {
        return delegate.invokeAll(tasks.map { TimedCallable(it) })
    }

    override fun <T : Any?> invokeAll(
        tasks: MutableCollection<out Callable<T>>,
        timeout: Long,
        unit: TimeUnit
    ): MutableList<Future<T>> {
        return delegate.invokeAll(tasks.map { TimedCallable(it) }, timeout, unit)
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>): T {
        return delegate.invokeAny(tasks.map { TimedCallable(it) })
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): T {
        return delegate.invokeAny(tasks.map { TimedCallable(it) }, timeout, unit)
    }

    override fun schedule(command: Runnable, delay: Long, unit: TimeUnit): ScheduledFuture<*> {
        return delegate.schedule(TimedRunnable(command), delay, unit)
    }

    override fun <V : Any?> schedule(callable: Callable<V>, delay: Long, unit: TimeUnit): ScheduledFuture<V> {
        return delegate.schedule(TimedCallable(callable), delay, unit)
    }

    override fun scheduleAtFixedRate(
        command: Runnable,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ): ScheduledFuture<*> {
        return delegate.scheduleAtFixedRate(TimedRunnable(command), initialDelay, period, unit)
    }

    override fun scheduleWithFixedDelay(
        command: Runnable,
        initialDelay: Long,
        delay: Long,
        unit: TimeUnit
    ): ScheduledFuture<*> {
        return delegate.scheduleWithFixedDelay(TimedRunnable(command), initialDelay, delay, unit)
    }
}

@RestController
class NettyAppEndpoint(
    private val dummyClients: List<DummyClient>,
    @Value("\${threadsForResponse:false}")
    private val threadsForResponse: Boolean,

    @Value("\${inneWatkiNaResponseEncode:false}")
    private val inneWatkiNaResponseEncode: Boolean,

    @Value("\${poolSize}")
    private val poolSize: Int,
) {

    private val schedulerOrNull: Scheduler? =
        if (inneWatkiNaResponseEncode) {
//            Schedulers.newParallel("XD", 256)//8083
//            Schedulers.newBoundedElastic(256, 5_000, "XDD")
//            Schedulers.boundedElastic()
Schedulers.newBoundedElastic(
    Runtime.getRuntime().availableProcessors(), 100_000, "customBounded"
)
//Schedulers.newParallel(
//    "customParallel", Runtime.getRuntime().availableProcessors(),
//)

//            val bounded =
//                Schedulers.newBoundedElastic(
//                    Runtime.getRuntime().availableProcessors(), 100_000, "XDDDDD"
//                )
                Schedulers.boundedElastic()
//            Schedulers.addExecutorServiceDecorator("asd") { a, b ->
//                TimedScheduledExecutorService(b)
//            }
//            bounded
            //            Schedulers.parallel()
//            ThreadLocal.withInitial { createXD(256) }
//            createXD(8)
//            Schedulers.newParallel("2", 20)

        } else null

    @GetMapping("/dummy/{id}")
    fun dummyValue(@PathVariable id: String): Mono<List<MockServerResponse>> =
        Flux
            .fromIterable(dummyClients)
//            .parallel()
//            .runOn(Schedulers.parallel())
            .doOnNext {
                Logeusz.loguj(" po fromIterable")
            }
            .flatMap { dummyClient ->
                Logeusz.loguj(" PRZED webClient.get() ")
                dummyClient
                    .get(id)
//                    .publishOnSchedulerWhenFlagIsTrue()
                    .doOnNext {
                        Logeusz.loguj(" PO webClient.get() ")
                    }
            }
            .doOnNext {
                Logeusz.loguj(" przed collectList")
            }
            .collectList()
//            .publishOnSchedulerWhenFlagIsTrue()
//            .collectSortedList { o1, o2 -> o1.compareTo(o2) }
            .doOnNext {
                Logeusz.loguj(" po collectList")
            }
//            .publishOnSchedulerWhenFlagIsTrue()
//            .map {
//                heavyCpuOperation()
//                it
//            }

    private fun heavyCpuOperation() {
        var bigInteger = BigInteger.ZERO
        for (i in 0..500_000) {
            bigInteger = bigInteger.add(BigInteger.valueOf(i.toLong()))
        }
    }


    private fun <T> Flux<T>.publishOnSchedulerWhenFlagIsTrue(): Flux<T> {
        if (threadsForResponse) {
            return this.publishOn(schedulerOrNull!!)
        }
        return this
    }

    private fun <T> Mono<T>.publishOnSchedulerWhenFlagIsTrue(): Mono<T> {
        if (inneWatkiNaResponseEncode) {
            return this.publishOn(schedulerOrNull!!)
        }
        return this
    }

    init {
        if (inneWatkiNaResponseEncode) {
            println("Will use publishOn")
        } else {
            println("Won't use publishOn")
        }
    }

}


object Logeusz {
    fun loguj(message: String) {
//        println(Thread.currentThread().name + " ### " + message)
    }
}


private fun createXD(threads: Int): Scheduler {

    val threadPoolExecutor =
        ThreadPoolExecutor(
            threads,
            threads,
            1,
            TimeUnit.HOURS,
            LinkedBlockingQueue(100_000),
            CustomThreadFactory("wlasny")
        )

    threadPoolExecutor.prestartAllCoreThreads()

    val proxy = TimedExecutorService(threadPoolExecutor)

    return Schedulers.fromExecutorService(proxy)
}

class CustomThreadFactory(val prefix: String) : ThreadFactory {
    private val xd = AtomicInteger(0)

    override fun newThread(r: Runnable): Thread {
        val thread = Thread(r, prefix + "-" + xd.getAndIncrement())
        thread.setDaemon(true)
        thread.priority = Thread.MAX_PRIORITY
        return thread
    }
}

class TimedExecutorService(
    private val delegate: ExecutorService,
) : ExecutorService {
    override fun execute(command: Runnable) {
        delegate.execute(TimedRunnable(command))
    }

    override fun shutdown() {
        delegate.shutdown()
    }

    override fun shutdownNow(): MutableList<Runnable> {
        return delegate.shutdownNow()
    }

    override fun isShutdown(): Boolean {
        return delegate.isShutdown
    }

    override fun isTerminated(): Boolean {
        return delegate.isTerminated
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        return delegate.awaitTermination(timeout, unit)
    }

    override fun <T : Any?> submit(task: Callable<T>): Future<T> {
        return delegate.submit(TimedCallable(task))
    }

    override fun <T : Any?> submit(task: Runnable, result: T): Future<T> {
        return delegate.submit(TimedRunnable(task), result)
    }

    override fun submit(task: Runnable): Future<*> {
        return delegate.submit(TimedRunnable(task))
    }

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>): MutableList<Future<T>> {
        return delegate.invokeAll(tasks.map { TimedCallable(it) })
    }

    override fun <T : Any?> invokeAll(
        tasks: MutableCollection<out Callable<T>>,
        timeout: Long,
        unit: TimeUnit
    ): MutableList<Future<T>> {
        return delegate.invokeAll(tasks.map { TimedCallable(it) }, timeout, unit)

    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>): T {
        return delegate.invokeAny(tasks.map { TimedCallable(it) })
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): T {
        return delegate.invokeAny(tasks.map { TimedCallable(it) }, timeout, unit)
    }
}

class TimedRunnable(
    private val delegate: Runnable,
) : Runnable {

    private val start = System.nanoTime()

    override fun run() {
        val endDummyClient = System.nanoTime()
        val dummyClientDuration = Duration.ofNanos(endDummyClient - start)
        Metrics.timer("waiting_time", "thread", Thread.currentThread().name).record(dummyClientDuration)
        delegate.run()
    }
}

class TimedCallable<T>(
    private val delegate: Callable<T>,
) : Callable<T> {

    private val start = System.nanoTime()

    override fun call(): T {
        val endDummyClient = System.nanoTime()
        val dummyClientDuration = Duration.ofNanos(endDummyClient - start)
        Metrics.timer("thread_pool_task_waiting_time", "thread", Thread.currentThread().name).record(dummyClientDuration)
        return delegate.call()
    }

}
