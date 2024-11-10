package com.mateusznalepa.http.clients.api

import com.mateusznalepa.http.clients.client.DummyClient
import io.micrometer.core.instrument.Metrics
import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.util.concurrent.FastThreadLocal
import io.netty.util.internal.PlatformDependent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import reactor.netty.http.server.HttpServer
import reactor.netty.resources.LoopResources
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger


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




    val asd = (0..2_000_0).map { it.toString() }.joinToString { it }

//    @GetMapping("/dummy/{id}")
//    fun dummyValuea(@PathVariable id: String): Mono<List<String>> {
//        val startDummyValue = System.nanoTime()
//        return Flux
//            .fromIterable(dummyClients)
//            .flatMap { it.get(id) }
//            .collectList() // ta linijka jest najbardziej kluczwa w tym wszystkim XDD
//            .doOnNext {
//                val endDummyValue = System.nanoTime()
//                val dummyValueDuration = Duration.ofNanos(endDummyValue - startDummyValue)
//                Metrics.timer("dummyValueDuration").record(dummyValueDuration)
//            }
//
//    }
//
//
    @GetMapping("/dummy/{id}")
    fun dummyValuea(@PathVariable id: String): Mono<List<String>> {
        val startDummyValue = System.nanoTime()
        return Flux
            .fromIterable((0..100).toList())
            .flatMap { Mono.just(asd) }
//            .threadsXDD()
            .collectList() // ta linijka jest najbardziej kluczwa w tym wszystkim XDD
            .doOnNext {
                val endDummyValue = System.nanoTime()
                val dummyValueDuration = Duration.ofNanos(endDummyValue - startDummyValue)
                Metrics.timer("dummyValueDuration").record(dummyValueDuration)
            }

            .threadsXDD()
//            .map { "5" }
    }

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
