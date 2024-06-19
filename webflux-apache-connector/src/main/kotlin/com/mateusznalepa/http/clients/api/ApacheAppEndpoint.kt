package com.mateusznalepa.http.clients.api

import com.mateusznalepa.http.clients.client.DummyClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class ApacheAppEndpoint(
    private val dummyClients: List<DummyClient>,
) {

    @GetMapping("/dummy/{id}")
    fun dummyValue(@PathVariable id: String): Mono<List<String>> =
        Flux
            .fromIterable(dummyClients)
            .flatMap { it.get(id) }
            .collectList()

}