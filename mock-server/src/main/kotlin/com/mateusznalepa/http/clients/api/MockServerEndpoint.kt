package com.mateusznalepa.http.clients.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration

val uberResponses = mapOf(
    1 to uberResponse(1),
    2 to uberResponse(2),
    3 to uberResponse(3),
    4 to uberResponse(4),
    5 to uberResponse(5),
)

private fun uberResponse(int: Number): String {

    val uber = StringBuilder()

    (0..1).map {
        uber.append(responses[int])
    }

    return uber.toString()
}

@RestController
class MockServerEndpoint {

    @GetMapping("/mock-server/{id}",)
    fun stub(@PathVariable id: Int): Mono<out ResponseEntity<out Any>> =
        Mono
            .just(ResponseEntity.ok(ultraSmallResponses[id]))
//            .just(ResponseEntity.ok(responses[id]))
//            .just(ResponseEntity.ok(responses[1]))
//            .just(ResponseEntity.ok(uberResponses[id]))
            .delayElement(Duration.ofMillis(id * 10L))

}

//5519
//6489