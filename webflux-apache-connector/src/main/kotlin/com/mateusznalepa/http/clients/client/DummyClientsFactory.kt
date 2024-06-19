package com.mateusznalepa.http.clients.client

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class DummyClientsFactory(
    private val apacheWebClientFactory: ApacheWebClientFactory,
) {

    @Bean
    fun dummyClient1(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(1))

    @Bean
    fun dummyClient2(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(2))

    @Bean
    fun dummyClient3(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(3))

    @Bean
    fun dummyClient4(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(4))

    @Bean
    fun dummyClient5(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(5))

    @Bean
    fun dummyClient6(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(6))


    @Bean
    fun dummyClient7(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(7))


    @Bean
    fun dummyClient8(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(8))

    @Bean
    fun dummyClient9(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(9))

    @Bean
    fun dummyClient10(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(10))

    @Bean
    fun dummyClient11(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(11))

    @Bean
    fun dummyClient12(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(12))

    @Bean
    fun dummyClient13(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(13))

    @Bean
    fun dummyClient14(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(14))

    @Bean
    fun dummyClient15(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(15))

    @Bean
    fun dummyClient16(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(16))


    @Bean
    fun dummyClient17(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(17))


    @Bean
    fun dummyClient18(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(18))

    @Bean
    fun dummyClient19(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(19))

    @Bean
    fun dummyClient20(): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(20))
}