package com.mateusznalepa.http.clients.api

import com.mateusznalepa.http.clients.client.DummyClient
import io.micrometer.core.instrument.Metrics
import io.netty.util.concurrent.FastThreadLocal
import io.netty.util.internal.PlatformDependent
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import reactor.netty.resources.LoopResources
import java.time.Duration
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

@RestController
class NettyAppEndpoint(
    private val dummyClients: List<DummyClient>,
    @Value("\${threadsForResponse}")
    private val threadsForResponse: Boolean,
) {

//    private val xd: Scheduler? =
//        threadsForResponse
//            .takeIf { it }
//            ?.let {
//                Schedulers.fromExecutor(
//                    Executors.newFixedThreadPool(
//                        100,
//                        CustomThreadFactory(),
//                    )
//                )
//            }

    private val xd: Scheduler? =
        threadsForResponse
            .takeIf { it }
            ?.let {
                LoopResources.create("response", 100, true).onServer(true)
            }
            ?.let { Schedulers.fromExecutorService(it) }

    class CustomThreadFactory : ThreadFactory {
        private val xd = AtomicInteger(0)
        private val namePrefix = "response-";

        override fun newThread(r: Runnable): Thread {
            val thread = Thread(r, namePrefix + xd.getAndIncrement())
            thread.setDaemon(true)
            return thread
        }
    }

    @GetMapping("/dummy/{id}")
    fun dummyValue(@PathVariable id: String): Mono<List<String>> {
        val startDummyValue = System.nanoTime()
        return Flux
            .fromIterable(dummyClients)
            .flatMap { it.get(id) }
            .collectList() // ta linijka jest najbardziej kluczwa w tym wszystkim XDD
            .doOnNext {
                val endDummyValue = System.nanoTime()
                val dummyValueDuration = Duration.ofNanos(endDummyValue - startDummyValue)
                Metrics.timer("dummyValueDuration").record(dummyValueDuration)
            }
            .threadsXDD()
//            .map { "5" }

    }

    private fun <T> Mono<T>.threadsXDD(): Mono<T> {
        if (threadsForResponse) {
            return this.publishOn(xd!!)
        }
        return this
    }


}

object Klasa {

    private const val MAX_TL_ARRAY_LEN: Int = 1024

    val BYTE_ARRAYS: FastThreadLocal<ByteArray> =
        object : FastThreadLocal<ByteArray>() {
            @Throws(Exception::class)
            override fun initialValue(): ByteArray {
                return PlatformDependent.allocateUninitializedArray(MAX_TL_ARRAY_LEN)
            }
        }
}