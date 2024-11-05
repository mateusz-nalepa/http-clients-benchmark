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
    private val czyJestWiecejWatkow: Boolean,

    @Value("\${typAlokatora}")
    private val typAlokatora: String,
) {

    fun createClient(number: Int): ReactorClientHttpConnector {
        val maxConnections = 500

        val connectionProviderBuilder =
            ConnectionProvider
                .builder("http-netty-$number-")
                .maxConnections(maxConnections)
                .pendingAcquireTimeout(Duration.ofMillis(500*100))
                .pendingAcquireMaxCount(maxConnections * 3)
                .metrics(true)
//                .metrics(true) {
//                    ReactorNettyMetrics(1, maxConnections)
//                }

//        -DczyJestWiecejWatkow=true -DtypAlokatora=pooled -Xms2048m -Xmx2048m -Dspring.profiles.active=8082
        val clientCustomization: (t: HttpClient) -> HttpClient = { client ->

            val builder =
                client
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500 * 100)
//                .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
//                .option(ChannelOption.ALLOCATOR, PreferHeapByteBufAllocator.DEFAULT)
//                .option(ChannelOption.RCVBUF_ALLOCATOR, FixedRecvByteBufAllocator(8192))
                    .responseTimeout(Duration.ofMillis(500 * 100))
                    .followRedirect(false)
                    .metrics(true) {
                        asd -> "$asd$number"
                    }


            val alokator =
                when (typAlokatora) {
                    "pooled" -> PooledByteBufAllocator.DEFAULT
                    "unpooled" -> UnpooledByteBufAllocator.DEFAULT
                    else -> throw RuntimeException("zly alokator")
                }


            builder.option(ChannelOption.ALLOCATOR, alokator)
        }

        when (czyJestWiecejWatkow) {
            true -> {
                val reactorRequestFactory = ReactorResourceFactory().apply {
                    connectionProvider = connectionProviderBuilder.build()
                    loopResources = LoopResources.create("http-loop-$number-pool-")
                }

                return ReactorClientHttpConnector(reactorRequestFactory, clientCustomization)
            }

            false -> {
//                val httpClient = clientCustomization.invoke(HttpClient.create(connectionProviderBuilder.build()))
//                return ReactorClientHttpConnector(httpClient)
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
//23741
//23878