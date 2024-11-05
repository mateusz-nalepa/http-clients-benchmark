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
    fun dummyClients(): List<DummyClient> =
        (1..100)
            .map {
                val dummyClient = dummyClient(it)
                beanFactory.initializeBean(dummyClient, "dummyClient$it")
                beanFactory.autowireBean(dummyClient)
                beanFactory.registerSingleton("dummyClient$it", dummyClient)
                dummyClient
            }

    private fun dummyClient(number: Int): DummyClient =
        DummyClient(
            webClient = webClientFactory.createWebClient(number),
            mockServerPort = mockServerPort,
        )

}
