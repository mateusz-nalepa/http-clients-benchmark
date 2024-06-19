package com.mateusznalepa.http.clients.client

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class DummyClientsFactory(
    private val nettyWebClientFactory: NettyWebClientFactory,
) {

    @Bean
    fun dummyClient1(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(1))

    @Bean
    fun dummyClient2(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(2))

    @Bean
    fun dummyClient3(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(3))

    @Bean
    fun dummyClient4(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(4))

    @Bean
    fun dummyClient5(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(5))

    @Bean
    fun dummyClient6(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(6))


    @Bean
    fun dummyClient7(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(7))


    @Bean
    fun dummyClient8(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(8))

    @Bean
    fun dummyClient9(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(9))

    @Bean
    fun dummyClient10(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(10))

    @Bean
    fun dummyClient11(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(11))

    @Bean
    fun dummyClient12(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(12))

    @Bean
    fun dummyClient13(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(13))

    @Bean
    fun dummyClient14(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(14))

    @Bean
    fun dummyClient15(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(15))

    @Bean
    fun dummyClient16(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(16))


    @Bean
    fun dummyClient17(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(17))


    @Bean
    fun dummyClient18(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(18))

    @Bean
    fun dummyClient19(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(19))

    @Bean
    fun dummyClient20(): DummyClient =
        DummyClient(nettyWebClientFactory.createWebClient(20))
}