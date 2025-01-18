package com.mateusznalepa.http.clients.client.config

import com.mateusznalepa.http.clients.TestAppConfig
import com.mateusznalepa.http.clients.client.DummyClient
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class DummyClientsFactory(
    private val webClientFactory: WebClientFactory,
    private val beanFactory: ConfigurableListableBeanFactory,
) {

    @Bean
    fun dummyClients(): List<DummyClient> {
        val clients = mutableListOf<DummyClient>()
        TestAppConfig
            .HTTP_CLIENTS
            .entries
            .map { entry ->
                clients.addAll(
                    (1..entry.key).map { dummyClientBean(it, entry.value) }
                )
            }
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
            webClient = webClientFactory.create(number, size),
            mockServerPort = TestAppConfig.MOCK_SERVER_PORT,
            size = size,
        )

}
