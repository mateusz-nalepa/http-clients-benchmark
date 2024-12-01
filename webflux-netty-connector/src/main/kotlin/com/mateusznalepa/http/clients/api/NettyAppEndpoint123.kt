package com.mateusznalepa.http.clients.api

import com.mateusznalepa.http.clients.client.DummyClientXD
import io.micrometer.core.instrument.Metrics
import io.netty.util.concurrent.FastThreadLocal
import io.netty.util.internal.PlatformDependent
import org.apache.hc.client5.http.impl.nio.NalepaLogger.NALEPA_LOGXDD
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
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
import kotlin.math.max


//@ConditionalOnProperty("threadsForResponse", havingValue = "true")
//@Configuration
//class ConfigXD {
//
//    init {
//        println("WESZLEM")
//    }
//
//    @Bean
//    fun reactorResourceFactory(): ReactorResourceFactory {
//        val factory = ReactorResourceFactory()
//        factory.isUseGlobalResources = false;
//        factory.loopResources = LoopResources.create("xd", 16, true);
//        return factory;
//    }
//}

@ConditionalOnProperty("threadsForResponse", havingValue = "true")
@Configuration
class ConfigXD {

    init {
        println("WESZLEM")
    }

//    @Bean
//    @ConditionalOnProperty("threadsForResponse", havingValue = "true")
//    fun httpServer() :HttpServer  {
//        return HttpServer
//            .create()
//            .runOn(
//                LoopResources.create("xd", 8, 32, true)
//                    .onServer(true)
//            )
//    }

//    @Component
//    @ConditionalOnProperty("threadsForResponse", havingValue = "true")
//    class EventLoopNettyCustomizer : NettyServerCustomizer {
//        override fun apply(httpServer: HttpServer): HttpServer {
//            val eventLoopGroup: EventLoopGroup = EpollEventLoopGroup(32)
//            println("config XD")
//            return httpServer.runOn( LoopResources.create("xd", 8, 32, true)
//                    .onServer(true))
//        }
//    }
}


@RestController
class NettyAppEndpoint123(
    private val dummyClientXDS: List<DummyClientXD>,
    @Value("\${threadsForResponse:false}")
    private val threadsForResponse: Boolean,

    @Value("\${isUseDedicatedThreadsPerClient}")
    private val isUseDedicatedThreadsPerClient: Boolean,
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
                LoopResources.create(
                    "response",
                    max(Runtime.getRuntime().availableProcessors().toDouble() * 2, 4.0).toInt(), true
                ).onServer(true)
            }
            ?.let { Schedulers.fromExecutorService(it) }


    val asd = (0..2_000_0).map { it.toString() }.joinToString { it }

    @GetMapping("/dummy/{id}")
    fun dummyValue(@PathVariable id: String): Mono<List<String>> {
        val startDummyValue = System.nanoTime()
        return Flux
            .fromIterable(dummyClientXDS)
            .flatMap { klient ->
                NALEPA_LOGXDD.error("{}: FLATMAP_START: klient: ${klient.numer()}", Thread.currentThread())

                klient
                    .get(id)
                    .doOnNext { NALEPA_LOGXDD.error("{}: FLATMAP_END: klient: ${klient.numer()}", Thread.currentThread()) }
            }
//            .publishOn(Schedulers.parallel())
            .publishOnxddd()
            .collectList() // ta linijka jest najbardziej kluczwa w tym wszystkim XDD
            .doOnNext {
                val endDummyValue = System.nanoTime()
                val dummyValueDuration = Duration.ofNanos(endDummyValue - startDummyValue)
                Metrics.timer("dummyValueDuration").record(dummyValueDuration)
            }
            .threadsXDD()
    }

//    @GetMapping("/dummy/{id}")
//    fun dummyValuea(@PathVariable id: String): Mono<String> {
//        val startDummyValue = System.nanoTime()
//        return dummyClients[0]
//            .get(id)
//            .doOnNext {
//                val endDummyValue = System.nanoTime()
//                val dummyValueDuration = Duration.ofNanos(endDummyValue - startDummyValue)
//                Metrics.timer("dummyValueDuration").record(dummyValueDuration)
//            }
//            .threadsXDD()
//    }

//
//
//    @GetMapping("/dummy/{id}")
//    fun dummyValuea(@PathVariable id: String): Mono<List<String>> {
//        val startDummyValue = System.nanoTime()
//        return Flux
//            .fromIterable((0..100).toList())
//            .flatMap { Mono.just(asd) }
////            .threadsXDD()
//            .collectList() // ta linijka jest najbardziej kluczwa w tym wszystkim XDD
//            .doOnNext {
//                val endDummyValue = System.nanoTime()
//                val dummyValueDuration = Duration.ofNanos(endDummyValue - startDummyValue)
//                Metrics.timer("dummyValueDuration").record(dummyValueDuration)
//            }
//
//            .threadsXDD()
////            .map { "5" }
//    }

    private fun <T> Flux<T>.threadsXDD(): Flux<T> {
        if (threadsForResponse) {
            return this.publishOn(xd!!)
        }
        return this
    }

    private fun <T> Mono<T>.threadsXDD(): Mono<T> {
        if (threadsForResponse) {
            return this.publishOn(xd!!)
        }
        return this
    }

    private fun <T> Flux<T>.publishOnxddd(): Flux<T> {
        if (isUseDedicatedThreadsPerClient) {
            return this
        }
        return this.publishOn(Schedulers.parallel())
//        return this.subscribeOn(Schedulers.parallel())
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

class CustomThreadFactory : ThreadFactory {
    private val xd = AtomicInteger(0)
    private val namePrefix = "res-";

    override fun newThread(r: Runnable): Thread {
        val thread = Thread(r, namePrefix + xd.getAndIncrement())
        thread.setDaemon(true)
        return thread
    }
}


//@Component
//class LoggingWebFilter : WebFilter {
//    override fun filter(
//        exchange: ServerWebExchange,
//        chain: WebFilterChain
//    ): Mono<Void> {
//        val start = Instant.now()
//        return chain.filter(exchange)
//            .doOnSuccess { aVoid ->
//                val end = Instant.now()
//                val duration = Duration.between(start, end)
//                println("${Thread.currentThread().name} Request ${exchange.request.path} took ${duration.toMillis()} ms",)
//            }
//    }
//}
