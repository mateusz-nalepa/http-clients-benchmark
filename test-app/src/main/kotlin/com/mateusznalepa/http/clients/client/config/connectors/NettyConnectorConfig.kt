package com.mateusznalepa.http.clients.client.config.connectors

import com.mateusznalepa.http.clients.TestAppConfig
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
            client
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500 * 100)
                .responseTimeout(Duration.ofMillis(500 * 100))
                .followRedirect(false)
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
