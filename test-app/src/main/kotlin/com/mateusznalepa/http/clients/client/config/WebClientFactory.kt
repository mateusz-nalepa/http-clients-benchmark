package com.mateusznalepa.http.clients.client.config

import com.mateusznalepa.http.clients.client.config.connectors.NettyConnectorConfig
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient


@Component
class WebClientFactory(
    private val webClientBuilder: WebClient.Builder,
    private val nettyConnectorConfig: NettyConnectorConfig,
) {

    private val CODEC_MAM_IN_MEMORY_SIZE = 16 * 1024 * 1024

    val strategies =
        ExchangeStrategies.builder()
            .codecs { codecs -> codecs.defaultCodecs().maxInMemorySize(CODEC_MAM_IN_MEMORY_SIZE) }
            .build()

    fun create(number: Int, size: String): WebClient =
        webClientBuilder
            .clientConnector(createConnector(number, size))
            .exchangeStrategies(strategies)
            .build()

    fun createConnector(number: Int, size: String): ClientHttpConnector =
        nettyConnectorConfig.createClient(number, size)
//        HttpComponentsClientHttpConnector(ApacheConfig.create(number, 500))

}

