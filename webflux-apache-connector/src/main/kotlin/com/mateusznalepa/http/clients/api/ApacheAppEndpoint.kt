package com.mateusznalepa.http.clients.api

import com.mateusznalepa.http.clients.client.DummyClient
import io.micrometer.core.instrument.Metrics
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@RestController
class ApacheAppEndpoint(
    private val dummyClients: List<DummyClient>,
) {

    @GetMapping("/dummy/{id}")
    fun dummyValue(@PathVariable id: String): Mono<List<String>> {
        val startDummyValue = System.nanoTime()
        return Flux
            .fromIterable(dummyClients)
            .flatMap { it.get(id) }
            .collectList()
            .doOnNext {
                val endDummyValue = System.nanoTime()
                val dummyValueDuration = Duration.ofNanos(endDummyValue - startDummyValue)
                Metrics.timer("dummyValueDuration").record(dummyValueDuration)
            }
    }

}