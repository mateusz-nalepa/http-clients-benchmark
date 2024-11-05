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
    @Value("\${czyJestWiecejWatkow}")
    private val isUseDedicatedThreadsPerClient: Boolean,

    @Value("\${typAlokatora}")
    private val memoryAllocatorType: String,
) {

    fun createClient(number: Int): ReactorClientHttpConnector {
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


            val alokator =
                when (memoryAllocatorType) {
                    "pooled" -> PooledByteBufAllocator.DEFAULT
                    "unpooled" -> UnpooledByteBufAllocator.DEFAULT
                    else -> throw RuntimeException("not supported allocator type")
                }


            builder.option(ChannelOption.ALLOCATOR, alokator)
        }

        when (isUseDedicatedThreadsPerClient) {
            true -> {
                val reactorRequestFactory = ReactorResourceFactory().apply {
                    connectionProvider = connectionProviderBuilder.build()
                    loopResources = LoopResources.create("http-loop-$number-pool-")
                }

                return ReactorClientHttpConnector(reactorRequestFactory, clientCustomization)
            }

            false -> {
                val reactorRequestFactory = ReactorResourceFactory().apply {
                    connectionProvider = connectionProviderBuilder.build()
                    loopResources = sharedLoopResources
                }

                return ReactorClientHttpConnector(reactorRequestFactory, clientCustomization)
            }
        }
    }

    val sharedLoopResources = LoopResources.create("http-loop-XD-pool-")

}
