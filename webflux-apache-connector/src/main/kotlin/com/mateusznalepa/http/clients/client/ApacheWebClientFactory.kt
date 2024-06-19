package com.mateusznalepa.http.clients.client

import org.springframework.context.annotation.Profile
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient


interface ApacheWebClientFactory {
    fun createWebClient(number: Int): WebClient
}

@Component
@Profile("pool200")
class ApacheWebClientFactory200(
    private val webClientBuilder: WebClient.Builder,
) : ApacheWebClientFactory {

    override fun createWebClient(number: Int): WebClient =
        webClientBuilder
            .clientConnector(createConnector(number))
            .build()

    private fun createConnector(number: Int): ClientHttpConnector =
        HttpComponentsClientHttpConnector(ApacheConfig.create(number))
}

@Component
@Profile("pool8")
class ApacheWebClientFactory8(
    private val webClientBuilder: WebClient.Builder,
) : ApacheWebClientFactory {

    val client = ApacheConfig.create(1)

    override fun createWebClient(number: Int): WebClient =
        webClientBuilder
            .clientConnector(createConnector(number))
            .build()

    private fun createConnector(number: Int): ClientHttpConnector =
        HttpComponentsClientHttpConnector(client)
}
