package com.mateusznalepa.http.clients.client

import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient


@Component
class WebClientFactory(
    private val webClientBuilder: WebClient.Builder,
    private val nettyConnectorConfig: NettyConnectorConfig,
) {

    private val size = 16 * 1024 * 1024


    val strategies =
        ExchangeStrategies.builder()
            .codecs { codecs -> codecs.defaultCodecs().maxInMemorySize(size) }
            .build()

    fun create(number: Int, size: String): WebClient =
        webClientBuilder
            .clientConnector(createConnector(number, size))
            .exchangeStrategies(strategies)
            .build()

    fun createConnector(number: Int, size: String): ClientHttpConnector =
        nettyConnectorConfig.createClient(number, size)

}

