package com.mateusznalepa.http.clients.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class DummyClientsFactory(
    private val webClientFactory: WebClientFactory,
    private val beanFactory: ConfigurableListableBeanFactory,
    @Value("\${mockServerPort}")
    private val mockServerPort: String,
) {

    private val counterek: AtomicInteger = AtomicInteger(1)

    @Bean
    fun dummyClientsSmall(): List<DummyClientXD>  {
        val clients = mutableListOf<DummyClientXD>()
//        clients.addAll((1..20).map { dummyClientBean(it, "s") })
//        clients.addAll((1..14).map { dummyClientBean(it, "m") })
//        clients.addAll((1..6).map { dummyClientBean(it, "l") })
        clients.addAll((1..20).map { dummyClientBean(it, "m") })
        return clients
    }

    private fun dummyClientBean(it: Int, size: String): DummyClientXD {
        val dummyClient = dummyClient(it, size)
        beanFactory.initializeBean(dummyClient, "dummyClient$it$size")
        beanFactory.autowireBean(dummyClient)
        beanFactory.registerSingleton("dummyClient$it$size", dummyClient)
        return dummyClient
    }

    private fun dummyClient(number: Int, size: String): DummyClientXD =
        DummyClientXD(
            webClient = webClientFactory.createWebClient(number, size),
            mockServerPort = mockServerPort,
            size = size,
            numer = counterek.getAndIncrement(),
        )

}
