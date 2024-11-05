package com.mateusznalepa.http.clients.client

import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient


interface WebClientFactory {
    fun createWebClient(number: Int): WebClient
}

@Component
class NettyWebClientFactory(
    private val webClientBuilder: WebClient.Builder,
    private val nettyConfig: NettyConfig,
) : WebClientFactory {

    override fun createWebClient(number: Int): WebClient =
        webClientBuilder
            .clientConnector(createConnector(number))
            .build()

    fun createConnector(number: Int): ClientHttpConnector =
        nettyConfig.createClient(number)

}

