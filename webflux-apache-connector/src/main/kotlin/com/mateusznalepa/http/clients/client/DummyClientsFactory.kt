package com.mateusznalepa.http.clients.client

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class DummyClientsFactory(
    private val apacheWebClientFactory: ApacheWebClientFactory,
    private val beanFactory: ConfigurableListableBeanFactory,
) {

    @Bean
    fun dummyClients(): List<DummyClient> =
        (1..20)
            .map {
                val dummyClient = dummyClient(it)
                beanFactory.initializeBean(dummyClient, "dummyClient$it")
                beanFactory.autowireBean(dummyClient)
                beanFactory.registerSingleton("dummyClient$it", dummyClient)
                dummyClient
            }

    private fun dummyClient(number: Int): DummyClient =
        DummyClient(apacheWebClientFactory.createWebClient(number))
}