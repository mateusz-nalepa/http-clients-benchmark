//package com.mateusznalepa.http.clients
//
//import org.junit.jupiter.api.Test
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//import reactor.core.scheduler.Schedulers
//import java.util.concurrent.Executors
//import java.util.concurrent.ThreadFactory
//import java.util.concurrent.atomic.AtomicInteger
//
//class ASD {
//
//
//    val scheduler =
//        Schedulers.fromExecutorService(
//            Executors.newFixedThreadPool(2, CustomThreadFactory() )
//        )
//
//    @Test
//    fun asd() {
//        val lista = listOf(1, 2, 3, 4, 5).reversed().map { it * 100 }
//
//        Flux.fromIterable(lista)
//            .subscribeOn(scheduler)
//            .flatMap {
//                Mono
//                    .just(it)
////                    .delayElement(Duration.ofMillis(it.toLong()))
//            }
//            .collectList()
//
//            .block()
//            .let { println(it) }
//    }
//
//}
//
//class CustomThreadFactory : ThreadFactory {
//    private val xd = AtomicInteger(0)
//    private val namePrefix = "res-";
//
//    override fun newThread(r: Runnable): Thread {
//        val thread = Thread(r, namePrefix + xd.getAndIncrement())
//        thread.setDaemon(true)
//        return thread
//    }
//}