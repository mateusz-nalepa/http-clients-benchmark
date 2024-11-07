package com.mateusznalepa.http.clients.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class DummyClientsFactory(
    private val webClientFactory: WebClientFactory,
    private val beanFactory: ConfigurableListableBeanFactory,
    @Value("\${mockServerPort}")
    private val mockServerPort: String,
) {

    @Bean
    fun dummyClientsSmall(): List<DummyClient>  {
        val clients = mutableListOf<DummyClient>()
//        clients.addAll((1..40).map { dummyClientBean(it, "s") })
//        clients.addAll((1..50).map { dummyClientBean(it, "m") })
//        clients.addAll((1..5).map { dummyClientBean(it, "l") })
        clients.addAll((1..100).map { dummyClientBean(it, "m") })
        return clients
    }

    private fun dummyClientBean(it: Int, size: String): DummyClient {
        val dummyClient = dummyClient(it, size)
        beanFactory.initializeBean(dummyClient, "dummyClient$it$size")
        beanFactory.autowireBean(dummyClient)
        beanFactory.registerSingleton("dummyClient$it$size", dummyClient)
        return dummyClient
    }

    private fun dummyClient(number: Int, size: String): DummyClient =
        DummyClient(
            webClient = webClientFactory.createWebClient(number, size),
            mockServerPort = mockServerPort,
            size = size,
        )

}
