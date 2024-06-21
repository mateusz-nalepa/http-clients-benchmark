package com.mateusznalepa.http.clients.client

import io.micrometer.core.instrument.Metrics
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration

class DummyClient(
    private val webClient: WebClient,
) {

    fun get(id: String): Mono<String> {
        val startDummyClient = System.nanoTime()

        return webClient.get()
            .uri(URI.create("http://localhost:8092/mock-server/$id"))
            .retrieve()
            .bodyToMono(String::class.java)
//            .toFuture()
//            .get()

            .doOnNext {
                val endDummyClient = System.nanoTime()
                val dummyClientDuration = Duration.ofNanos(endDummyClient - startDummyClient)
                Metrics.timer("dummyClientDuration").record(dummyClientDuration)
            }

    }


}