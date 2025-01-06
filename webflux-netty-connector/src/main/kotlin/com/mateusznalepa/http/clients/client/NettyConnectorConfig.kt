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
class NettyConnectorConfig(
    @Value("\${isUseDedicatedThreadsPerClient:false}")
    private val isUseDedicatedThreadsPerClient: Boolean,

    @Value("\${memoryAllocatorType}")
    private val memoryAllocatorType: String,
) {


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
                println("Dedicated Loop Resources")
                val reactorRequestFactory = ReactorResourceFactory().apply {
                    connectionProvider = connectionProviderBuilder.build()
                    loopResources = LoopResources.create("$size-Y-${number}")
                }

                return ReactorClientHttpConnector(reactorRequestFactory, clientCustomization)
            }

            false -> {
                println("Shared Loop Resource")
//                val reactorRequestFactory = ReactorResourceFactory().apply {
//                    connectionProvider = connectionProviderBuilder.build()
//                    loopResources = sharedLoopResources
//                }
//                return ReactorClientHttpConnector(reactorRequestFactory, clientCustomization)
                return ReactorClientHttpConnector(clientCustomization.invoke(HttpClient.create(connectionProviderBuilder.build())))
            }
        }
    }


//    fun createClient(number: Int, size: String): ReactorClientHttpConnector {
//        val maxConnections = 5000
//
//        val connectionProviderBuilder =
//            ConnectionProvider
//                .builder("http-netty-$number-")
//                .maxConnections(maxConnections)
//                .pendingAcquireTimeout(Duration.ofMillis(500 * 100))
//                .pendingAcquireMaxCount(maxConnections * 3)
//                .metrics(true)
//
//        val clientCustomization: (t: HttpClient) -> HttpClient = { client ->
//
//            val builder =
//                client
//                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500 * 100)
//                    .responseTimeout(Duration.ofMillis(500 * 100))
//                    .followRedirect(false)
////                    .metrics()
//
//
//            val memoryAllocator =
//                when (memoryAllocatorType) {
//                    "pooled" -> PooledByteBufAllocator.DEFAULT
//                    "unpooled" -> UnpooledByteBufAllocator.DEFAULT
//                    else -> throw RuntimeException("not supported allocator type")
//                }
//
//
//            builder.option(ChannelOption.ALLOCATOR, memoryAllocator)
//        }
//
//        when (isUseDedicatedThreadsPerClient) {
//            true -> {
//                println("Dedicated Loop Resources")
//                val reactorRequestFactory = ReactorResourceFactory().apply {
//                    connectionProvider = connectionProviderBuilder.build()
//                    loopResources = LoopResources.create("$size-Y-${number}")
//                }
//
//                return ReactorClientHttpConnector(reactorRequestFactory, clientCustomization)
//            }
//
//            false -> {
//                println("Shared Loop Resource")
////                val reactorRequestFactory = ReactorResourceFactory().apply {
////                    connectionProvider = connectionProviderBuilder.build()
////                    loopResources = sharedLoopResources
////                }
////                return ReactorClientHttpConnector(reactorRequestFactory, clientCustomization)
//                return ReactorClientHttpConnector(clientCustomization.invoke(HttpClient.create(connectionProviderBuilder.build())))
//            }
//        }
//    }

//    val sharedLoopResoeurces = LoopResources.create("LP-")
//    val sharedLoopResources = LoopResources.create("klientHttp")
//    val sharedLoopResources = LoopResources.create("LP-", 400, true)

}
