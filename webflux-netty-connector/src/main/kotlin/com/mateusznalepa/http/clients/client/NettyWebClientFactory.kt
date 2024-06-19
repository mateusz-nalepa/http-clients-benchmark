package com.mateusznalepa.http.clients.client

import io.netty.channel.ChannelOption
import org.springframework.http.client.ReactorResourceFactory
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.resources.ConnectionProvider
import reactor.netty.resources.LoopResources
import java.time.Duration


@Component
class NettyWebClientFactory(
    private val webClientBuilder: WebClient.Builder,
) {

//    val connectionProviderBuilder =
//        ConnectionProvider
//            .builder("http-client-$1-webflux-netty-")
//            .maxConnections(500)
//            .pendingAcquireTimeout(Duration.ofMillis(500))
//            .pendingAcquireMaxCount(500)
//
//    val reactorRequestFactory = ReactorResourceFactory().apply {
//        connectionProvider = connectionProviderBuilder.build()
//        loopResources = LoopResources.create("http-loop-$1")
//    }
//
//    val reactorClientHttpConnector = ReactorClientHttpConnector(reactorRequestFactory) { client ->
//
//        client
//            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500)
////                .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
//            .option(ChannelOption.ALLOCATOR, PreferHeapByteBufAllocator.DEFAULT)
//            .option(ChannelOption.RCVBUF_ALLOCATOR, FixedRecvByteBufAllocator(8192))
//            .responseTimeout(Duration.ofMillis(500))
//            .followRedirect(false)
//    }

    fun createWebClient(number: Int): WebClient =
        webClientBuilder
            .clientConnector(createConnector(number))
            .build()

    fun createConnector(number: Int): ClientHttpConnector {
        val connectionProviderBuilder =
            ConnectionProvider
                .builder("http-client-$number-webflux-netty-")
                .maxConnections(500)
                .pendingAcquireTimeout(Duration.ofMillis(500))
                .pendingAcquireMaxCount(500)

        val reactorRequestFactory = ReactorResourceFactory().apply {
            connectionProvider = connectionProviderBuilder.build()
            loopResources = LoopResources.create("http-loop-$number-")
        }

        val reactorClientHttpConnector = ReactorClientHttpConnector(reactorRequestFactory) { client ->

            client
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500)
//                .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
//                .option(ChannelOption.ALLOCATOR, PreferHeapByteBufAllocator.DEFAULT)
//                .option(ChannelOption.RCVBUF_ALLOCATOR, FixedRecvByteBufAllocator(8192))
                .responseTimeout(Duration.ofMillis(500))
                .followRedirect(false)
        }

        return reactorClientHttpConnector
    }

    private val loopResourcesXD = LoopResources.create("http-loop-pool-")

}
