package com.mateusznalepa.http.clients.client

import io.micrometer.core.instrument.Metrics
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration


class DummyClientXD(
    private val webClient: WebClient,
    private val mockServerPort: String,
    private val size: String,
    private val numer: Int,
) {

    fun numer() = numer

    fun get(id: String): Mono<XD> {
        val startDummyClient = System.nanoTime()

        return webClient.get()
            .uri(URI.create("http://localhost:$mockServerPort/mock-server/$id/$size"))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(XD::class.java)
//            .toFuture()
//            .get()

            .doOnNext {
                val endDummyClient = System.nanoTime()
                val dummyClientDuration = Duration.ofNanos(endDummyClient - startDummyClient)
                Metrics.timer("dummyClientDuration").record(dummyClientDuration)
            }

    }


}

data class XD(
    val value: String,
)