package com.mateusznalepa.http.clients.client.config.connectors

import com.mateusznalepa.http.clients.TestAppConfig
import io.netty.buffer.AdaptiveByteBufAllocator
import io.netty.buffer.PooledByteBufAllocator
import io.netty.buffer.UnpooledByteBufAllocator
import io.netty.channel.ChannelOption
import org.springframework.http.client.ReactorResourceFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import reactor.netty.resources.LoopResources
import java.time.Duration

@Component
class NettyConnectorConfig {

    fun createClient(number: Int, size: String): ReactorClientHttpConnector {
        val maxConnections = 5000

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


            val clientMemoryAllocator =
                when (TestAppConfig.CLIENT_MEMORY_ALLOCATOR_TYPE) {
                    "pooled" -> AdaptiveByteBufAllocator()
                    "unpooled" -> UnpooledByteBufAllocator.DEFAULT
                    "adaptive" -> PooledByteBufAllocator.DEFAULT
                    else -> throw RuntimeException("not supported allocator type")
                }

            builder
                .option(ChannelOption.ALLOCATOR, clientMemoryAllocator)
        }

        when (TestAppConfig.USE_DEDICATED_THREADS_FOR_CLIENT) {
            true -> {
                val reactorRequestFactory = ReactorResourceFactory().apply {
                    connectionProvider = connectionProviderBuilder.build()
                    loopResources = LoopResources.create("$size-Y-${number}")
                }

                return ReactorClientHttpConnector(reactorRequestFactory, clientCustomization)
            }

            false -> {
                return ReactorClientHttpConnector(clientCustomization.invoke(HttpClient.create(connectionProviderBuilder.build())))
            }
        }
    }
}
