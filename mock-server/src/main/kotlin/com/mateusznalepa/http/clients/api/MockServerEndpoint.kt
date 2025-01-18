package com.mateusznalepa.http.clients.api

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
class MockServerEndpoint {

    @GetMapping(
        value = ["/mock-server/{id}/{size}"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun mockResponse(
        @PathVariable id: Int,
        @PathVariable size: String,
    ): Mono<ResponseEntity<MockResponseDto>> =
        Mono
            .just(ResponseEntity.ok(MockResponseDto(response(size, id)!!)))


    private fun response(size: String, id: Int): String? =
        when (size) {
            "s" -> ultraSmallResponses[id]
            "m" -> responses[id]
            "l" -> uberResponses[id]
            else -> throw RuntimeException("XD")
        }
}

data class MockResponseDto(
    val value: String,
)
