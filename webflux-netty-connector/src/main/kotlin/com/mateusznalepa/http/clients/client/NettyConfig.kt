package com.mateusznalepa.http.clients.client

import io.netty.buffer.PooledByteBufAllocator
import io.netty.buffer.UnpooledByteBufAllocator
import io.netty.channel.ChannelOption
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.ReactorResourceFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import reactor.netty.resources.LoopResources
import java.time.Duration

@Component
class NettyConfig(
    @Value("\${isUseDedicatedThreadsPerClient}")
    private val isUseDedicatedThreadsPerClient: Boolean,

    @Value("\${memoryAllocatorType}")
    private val memoryAllocatorType: String,
) {

    fun createClient(number: Int, size: String): ReactorClientHttpConnector {
        val maxConnections = 500

        val connectionProviderBuilder =
            ConnectionProvider
                .builder("http-netty-$number-")
                .maxConnections(maxConnections)
                .pendingAcquireTimeout(Duration.ofMillis(500 * 100))
                .pendingAcquireMaxCount(maxConnections * 3)
                .metrics(true)

        val clientCustomization: (t: HttpClient) -> HttpClient = { client ->

            val builder =
                client
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500 * 100)
                    .responseTimeout(Duration.ofMillis(500 * 100))
                    .followRedirect(false)
//                    .metrics()


            val memoryAllocator =
                when (memoryAllocatorType) {
                    "pooled" -> PooledByteBufAllocator.DEFAULT
                    "unpooled" -> UnpooledByteBufAllocator.DEFAULT
                    else -> throw RuntimeException("not supported allocator type")
                }


            builder.option(ChannelOption.ALLOCATOR, memoryAllocator)
        }

        when (isUseDedicatedThreadsPerClient) {
            true -> {
                println("custom loop resources")
                val reactorRequestFactory = ReactorResourceFactory().apply {
                    connectionProvider = connectionProviderBuilder.build()
//                    loopResources = LoopResources.create("${size}P-$number-")
                    loopResources = LoopResources.create("$size-Y-${number}")
                }

                return ReactorClientHttpConnector(reactorRequestFactory, clientCustomization)
            }

            false -> {
                println("shared loop resources")
                val reactorRequestFactory = ReactorResourceFactory().apply {
                    connectionProvider = connectionProviderBuilder.build()
                    loopResources = sharedLoopResoeurces
                }

                return ReactorClientHttpConnector(clientCustomization.invoke(HttpClient.create()))
            }
        }
    }

//    val sharedLoopResoeurces = LoopResources.create("LP-")
    val sharedLoopResoeurces = LoopResources.create("XX")
//    val sharedLoopResources = LoopResources.create("LP-", 400, true)

}
