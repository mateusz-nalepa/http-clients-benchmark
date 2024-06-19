package com.mateusznalepa.http.clients.client

import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.net.URI

class DummyClient(
    private val webClient: WebClient,
) {

    fun get(id: String): Mono<String> =
        webClient.get()
            .uri(URI.create("http://localhost:8091/mock-server/$id"))
            .retrieve()
            .bodyToMono(String::class.java)
//            .toFuture()
//            .get()

}