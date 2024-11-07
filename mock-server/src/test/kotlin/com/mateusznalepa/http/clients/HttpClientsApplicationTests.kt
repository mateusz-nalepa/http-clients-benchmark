package com.mateusznalepa.http.clients

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigInteger

class HttpClientsApplicationTests {

    val mapa = mutableMapOf<String, MutableList<Wynik>>()
    val naCsvRecvFromListaRecvfrom = mutableListOf<NaCsvRecvFrom>()
    val naCsvRecvFromListaEpoll = mutableListOf<NaCsvEpoll>()
    val naCsvRecvFromListaSwitch = mutableListOf<NaCsvSwitch>()

    @Test
    fun contextLoads() {
        val objectMapper = ObjectMapper().findAndRegisterModules().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        val file = File(this::class.java.classLoader.getResource("result2.txt")!!.file)

        file.forEachLine { line ->
            kotlin.runCatching {
                val wynik = objectMapper.readValue<Wynik>(line)
                mapa.getOrPut(wynik.thread) { mutableListOf() }.add(wynik)
            }
        }

        mapa.entries
            .forEach {
                val recvFromDuration =
                    it.value
                        .filter { it.option == "recvfromxx" }
                        .map { it.duratioNs }
                        .also {
                            if (it.isEmpty()) {
                                println("recvfromxx_bytes_duration")
                            }
                        }
                        ?.takeIf { it.isNotEmpty() }
                        ?.reduce { acc, bigInteger -> acc.plus(bigInteger) }

                val recvFromBytes =
                    it.value
                        .filter { it.option == "recvfromxx" }
                        .map { it.result }
                        .also {
                            if (it.isEmpty()) {
                                println("recvfromxx_bytes")
                            }
                        }
                        ?.takeIf { it.isNotEmpty() }
                        ?.reduce { acc, bigInteger -> acc.plus(bigInteger) }
                        ?.divide(BigInteger.valueOf(1024))

                val naCsvRecvFrom = NaCsvRecvFrom(
                    it.key,
                    "recvfromxx",
                    recvFromDuration,
                    recvFromBytes,
                    recvFromBytes?.divide(recvFromDuration)
                )
                naCsvRecvFromListaRecvfrom.add(naCsvRecvFrom)

                val epollFromDuration =
                    it.value
                        .filter { it.option == "epoll_wait" }
                        .map { it.duratioNs }
                        .also {
                            if (it.isEmpty()) {
                                println("epoll_wait_duration")
                            }
                        }
                        .takeIf { it.isNotEmpty() }
                        ?.reduce { acc, bigInteger -> acc.plus(bigInteger) }

                val epollFromBytes =
                    it.value
                        .filter { it.option == "epoll_wait" }
                        .map { it.result }
                        .also {
                            if (it.isEmpty()) {
                                println("epoll_wait_bytes")
                            }
                        }
                        .takeIf { it.isNotEmpty() }
                        ?.reduce { acc, bigInteger -> acc.plus(bigInteger) }

                val naCsvRecvFrom2 = NaCsvEpoll(it.key, "epoll_wait", epollFromDuration, epollFromBytes)
                naCsvRecvFromListaEpoll.add(naCsvRecvFrom2)

                val switchFromDuration =
                    it.value
                        .filter { it.option == "sched_switch" }
                        .map { it.duratioNs }
                        .also {
                            if (it.isEmpty()) {
                                println("switch")
                            }
                        }
                        .takeIf { it.isNotEmpty() }
                        ?.reduce { acc, bigInteger -> acc.plus(bigInteger) }

                val naCsvSwitch = NaCsvSwitch(it.key, "sched_switch", switchFromDuration)
                naCsvRecvFromListaSwitch.add(naCsvSwitch)
            }

        println("############ recvfromxx ############")
        naCsvRecvFromListaRecvfrom.sortedBy { it.thread }.forEach { println(it) }

        println("############ epoll_wait ############")
        naCsvRecvFromListaEpoll.sortedBy { it.thread }.forEach { println(it) }

        println("############ switch ############")
        naCsvRecvFromListaSwitch.sortedBy { it.thread }.forEach { println(it) }
    }

}

data class Wynik(
    val thread: String,
    val option: String,
    val duratioNs: BigInteger,
    val result: BigInteger,
)

data class NaCsvRecvFrom(
    val thread: String,
    val operacja: String,
    val czasNs: BigInteger?,
    val kiloBajty: BigInteger?,
    val bajtyNaNs: BigInteger?,
)

data class NaCsvEpoll(
    val thread: String,
    val operacja: String,
//    val czasNs: BigInteger,
    val durationNs: BigInteger?,
    val sumaResultow: BigInteger?,
//    val bajtyNaNs: BigInteger,
)

data class NaCsvSwitch(
    val thread: String,
    val operacja: String,
//    val czasNs: BigInteger,
    val sumaResultow: BigInteger?,
//    val bajtyNaNs: BigInteger,
)