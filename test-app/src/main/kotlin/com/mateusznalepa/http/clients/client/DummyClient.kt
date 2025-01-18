package com.mateusznalepa.http.clients.client

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI

class DummyClient(
    private val webClient: WebClient,
    private val mockServerPort: Int,
    private val size: String,
) {

    fun get(id: String): Mono<MockServerResponse> =
        webClient
            .get()
            .uri(URI.create("http://localhost:$mockServerPort/mock-server/$id/$size"))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(MockServerResponse::class.java)
}

data class MockServerResponse(
    val value: String,
)
