package com.mateusznalepa.http.clients.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration

@RestController
class MockServerEndpoint {

    @GetMapping("/mock-server/{id}",)
    fun stub(@PathVariable id: Int): Mono<out ResponseEntity<out Any>> =
        Mono
            .just(ResponseEntity.ok(responses[1]))
            .delayElement(Duration.ofMillis(90))

}
