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
        HttpComponentsClientHttpConnector(ApacheConfig.create(number, 500, Runtime.getRuntime().availableProcessors()))
//        HttpComponentsClientHttpConnector(ApacheConfig.create(number, 500, 25))
}

@Component
@Profile("pool8")
class ApacheWebClientFactory8(
    private val webClientBuilder: WebClient.Builder,
) : ApacheWebClientFactory {

//    val client = ApacheConfig.create(1, 600, Runtime.getRuntime().availableProcessors() * 20)
//    val client = ApacheConfig.create(1, 200, Runtime.getRuntime().availableProcessors() * 20)
//    ilosc watkow * 4 daje prawie takie same wyniki co 500 watkow XD
    // ofc jeśli chodzi o klienta samego w sobie, bo na http RPS ciut różnica jest :thinking_face:
//    val client = ApacheConfig.create(1, 500, Runtime.getRuntime().availableProcessors() * 4)
    val client = ApacheConfig.create(1, 500, Runtime.getRuntime().availableProcessors())

    override fun createWebClient(number: Int): WebClient =
        webClientBuilder
            .clientConnector(createConnector(number))
            .build()

    private fun createConnector(number: Int): ClientHttpConnector =
        HttpComponentsClientHttpConnector(client)
}
