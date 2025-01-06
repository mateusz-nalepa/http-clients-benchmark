package com.mateusznalepa.http.clients.client

import io.micrometer.core.instrument.Metrics
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration

data class MockServerResponse(
    val value: String,
)
class DummyClient(
    private val webClient: WebClient,
    private val mockServerPort: String,
    private val size: String,
) {

    fun get(id: String): Mono<MockServerResponse> {
        val startDummyClient = System.nanoTime()

        return webClient
            .get()
            .uri(URI.create("http://localhost:$mockServerPort/mock-server/$id/$size"))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(MockServerResponse::class.java)
            .doOnNext {
                val endDummyClient = System.nanoTime()
                val dummyClientDuration = Duration.ofNanos(endDummyClient - startDummyClient)
                Metrics.timer("dummyClientDuration").record(dummyClientDuration)
            }
    }
}
