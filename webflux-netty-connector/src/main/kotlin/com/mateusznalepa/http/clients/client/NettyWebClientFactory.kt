package com.mateusznalepa.http.clients.client

import org.springframework.context.annotation.Profile
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient


interface NettyWebClientFactory {
    fun createWebClient(number: Int): WebClient
}

@Profile("pool200")
@Component
class NettyWebClientFactory200(
    private val webClientBuilder: WebClient.Builder,
) : NettyWebClientFactory {

    override fun createWebClient(number: Int): WebClient =
        webClientBuilder
            .clientConnector(createConnector(number))
            .build()

    fun createConnector(number: Int): ClientHttpConnector =
        NettyConfig.createClient(number, true)

}

@Profile("pool8")
@Component
class NettyWebClientFactory8(
    private val webClientBuilder: WebClient.Builder,
) : NettyWebClientFactory {

    val connector = NettyConfig.createClient(1, false)

    override fun createWebClient(number: Int): WebClient =
        webClientBuilder
            .clientConnector(createConnector(number))
            .build()

    fun createConnector(number: Int): ClientHttpConnector =
        connector

}


