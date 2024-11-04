package com.mateusznalepa.http.clients.client

import io.netty.channel.ChannelOption
import org.springframework.http.client.ReactorResourceFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import reactor.netty.resources.LoopResources
import java.time.Duration

object NettyConfig {

    fun createClient(number: Int, shouldUsed200Thread: Boolean): ReactorClientHttpConnector {
        val maxConnections = 500

        val connectionProviderBuilder =
            ConnectionProvider
                .builder("http-netty-$number-")
                .maxConnections(maxConnections)
                .pendingAcquireTimeout(Duration.ofMillis(500))
                .pendingAcquireMaxCount(maxConnections * 3)
                .metrics(true)
//                .metrics(true) {
//                    ReactorNettyMetrics(1, maxConnections)
//                }


        val reactorRequestFactory = ReactorResourceFactory().apply {
            connectionProvider = connectionProviderBuilder.build()
            loopResources = LoopResources.create("http-loop-$number-pool-")
        }


        val clientCustomization: (t: HttpClient) -> HttpClient = { client ->

            client
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500)
//                .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
//                .option(ChannelOption.ALLOCATOR, PreferHeapByteBufAllocator.DEFAULT)
//                .option(ChannelOption.RCVBUF_ALLOCATOR, FixedRecvByteBufAllocator(8192))
                .responseTimeout(Duration.ofMillis(500))
                .followRedirect(false)
        }

        if (shouldUsed200Thread) {
            return ReactorClientHttpConnector(reactorRequestFactory, clientCustomization)
        } else {
            val httpClient = clientCustomization.invoke(HttpClient.create(connectionProviderBuilder.build()))
            return ReactorClientHttpConnector(httpClient)
        }
    }

}