package com.mateusznalepa.http.clients.client

import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient


interface WebClientFactory {
    fun createWebClient(number: Int, size: String): WebClient
}

@Component
class NettyWebClientFactory(
    private val webClientBuilder: WebClient.Builder,
    private val nettyConfig: NettyConfig,
) : WebClientFactory {

    override fun createWebClient(number: Int, size: String): WebClient =
        webClientBuilder
            .clientConnector(createConnector(number, size))
            .build()

    fun createConnector(number: Int, size: String): ClientHttpConnector =
        nettyConfig.createClient(number, size)

}

