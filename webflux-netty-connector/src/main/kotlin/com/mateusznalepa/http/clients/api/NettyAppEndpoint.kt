package com.mateusznalepa.http.clients.api

import com.mateusznalepa.http.clients.client.DummyClient
import io.micrometer.core.instrument.Metrics
import io.netty.util.concurrent.FastThreadLocal
import io.netty.util.internal.PlatformDependent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@RestController
class NettyAppEndpoint(
    private val dummyClients: List<DummyClient>,
) {

    @GetMapping("/dummy/{id}")
    fun dummyValue(@PathVariable id: String): Mono<List<String>> {
        val startDummyValue = System.nanoTime()
        return Flux
            .fromIterable(dummyClients)
            .flatMap { it.get(id) }
            .collectList() // ta linijka jest najbardziej kluczwa w tym wszystkim XDD
            .doOnNext {
                val endDummyValue = System.nanoTime()
                val dummyValueDuration = Duration.ofNanos(endDummyValue - startDummyValue)
                Metrics.timer("dummyValueDuration").record(dummyValueDuration)
            }
    }


}

object Klasa {

    private const val MAX_TL_ARRAY_LEN: Int = 1024

    val BYTE_ARRAYS: FastThreadLocal<ByteArray> =
        object : FastThreadLocal<ByteArray>() {
            @Throws(Exception::class)
            override fun initialValue(): ByteArray {
                return PlatformDependent.allocateUninitializedArray(MAX_TL_ARRAY_LEN)
            }
        }
}