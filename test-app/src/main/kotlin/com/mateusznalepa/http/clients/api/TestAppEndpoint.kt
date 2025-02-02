package com.mateusznalepa.http.clients.api

import com.mateusznalepa.http.clients.TestAppConfig
import com.mateusznalepa.http.clients.client.DummyClient
import com.mateusznalepa.http.clients.client.MockServerResponse
import com.mateusznalepa.http.clients.util.customThreadPool.CustomSchedulerCreator
import com.mateusznalepa.http.clients.util.logger.CustomLoggerWrapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler


@RestController
class TestAppEndpoint(
    private val dummyClients: List<DummyClient>,
    customSchedulerCreator: CustomSchedulerCreator,
) {

    private val cpuBoundSchedulerOrNull: Scheduler? =
        if (TestAppConfig.CPU_BOUND_SCHEDULER_ACTIVE) {
//            val bounded = Schedulers.boundedElastic()
//            Schedulers.addExecutorServiceDecorator("asd") { a, b ->
//                TimedScheduledExecutorService(b)
//            }
//            bounded
//            Schedulers.parallel()
            customSchedulerCreator.create(Runtime.getRuntime().availableProcessors())
//            Schedulers.boundedElastic()
        } else null


    @GetMapping("/dummy/{id}")
    fun dummyValueWithId(@PathVariable id: String): Mono<List<MockServerResponse>> =
        Flux
            .fromIterable(dummyClients)
            .doOnNext { CustomLoggerWrapper.log("Before client") }
            .flatMap { dummyClient ->
                dummyClient
                    .get(id)
                    .doOnNext { CustomLoggerWrapper.log("After client") }
            }
            .collectList()
            .publishOnCPUBoundThreads()
            .doFinally { CustomLoggerWrapper.log("doFinally") }

    @GetMapping("/dummy")
    fun dummyValue(): Mono<MockServerResponse> =
        Mono
            .just(MockServerResponse("Asd"))
            .doOnNext { CustomLoggerWrapper.log("poczatek")}
            .doFinally { CustomLoggerWrapper.log("koniec")}

    private fun <T> Flux<T>.publishOnCPUBoundThreads(): Flux<T> {
        if (TestAppConfig.CPU_BOUND_SCHEDULER_ACTIVE) {
            return this.publishOn(cpuBoundSchedulerOrNull!!)
        }
        return this
    }

    private fun <T> Mono<T>.publishOnCPUBoundThreads(): Mono<T> {
        if (TestAppConfig.CPU_BOUND_SCHEDULER_ACTIVE) {
            return this.publishOn(cpuBoundSchedulerOrNull!!)
        }
        return this
    }

}
