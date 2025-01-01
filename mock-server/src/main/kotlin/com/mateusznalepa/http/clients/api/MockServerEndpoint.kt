package com.mateusznalepa.http.clients.api

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import javax.print.attribute.standard.Media
import kotlin.random.Random

val uberResponses = mapOf(
    1 to uberResponse(1),
    2 to uberResponse(2),
    3 to uberResponse(3),
    4 to uberResponse(4),
    5 to uberResponse(5),
)

private fun uberResponse(int: Number): String {

    val uber = StringBuilder()

    (0..2).map {
        uber.append(responses[int])
    }

    return uber.toString()
}

@RestController
class MockServerEndpoint {


    @GetMapping()
    fun stubaa(): Mono<out ResponseEntity<out Any>> =
        Mono

//            .just(ResponseEntity.ok(ultraSmallResponses[Random.nextInt(1, 5).also { println(it) }]))
//            .just(ResponseEntity.ok(responses[id].also { println(id) }))
            .just(ResponseEntity.ok("123456789EOF"))
//            .just(ResponseEntity.ok(uberResponses[id]))
//            .just(ResponseEntity.ok(response()))
            .delayElement(Duration.ofMillis(10L))


    @GetMapping("/mock-server/{id}/{size}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun stub(@PathVariable id: Int, @PathVariable size: String): Mono<ResponseEntity<XD>> =
        Mono

//            .just(ResponseEntity.ok(ultraSmallResponses[Random.nextInt(1, 5).also { println(it) }]))
//            .just(ResponseEntity.ok(responses[id].also { println(id) }))
            .just(ResponseEntity.ok(XD(response(size, id)!!)))
//            .publishOn(Schedulers.boundedElastic())
//            .just(ResponseEntity.ok(uberResponses[id]))
//            .just(ResponseEntity.ok(response()))
//            .delayElement(Duration.ofMillis(id * 10L))


    private fun response(size: String, id: Int):String? =
        when(size) {
            "s"-> ultraSmallResponses[id]
            "m"-> responses[id]
            "l"-> uberResponses[id]
            else-> throw RuntimeException("XD")
        }
}

//5519
//6489

data class XD(
    val value: String,
)