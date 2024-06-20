package com.mateusznalepa.http.clients.client

import org.springframework.context.annotation.Profile
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient


interface ApacheWebClientFactory {
    fun createWebClient(number: Int): WebClient
}

@Component
@Profile("pool200")
class ApacheWebClientFactory200(
    private val webClientBuilder: WebClient.Builder,
) : ApacheWebClientFactory {

    val client = ApacheConfig.create(1, 500,  24)

    override fun createWebClient(number: Int): WebClient =
        webClientBuilder
            .clientConnector(createConnector(number))
            .build()

    private fun createConnector(number: Int): ClientHttpConnector =
//        HttpComponentsClientHttpConnector(ApacheConfig.create(number, 500, Runtime.getRuntime().availableProcessors()))

        // w sumie nie widac jakiejs roznicy miedzy tymi dwoma
//        HttpComponentsClientHttpConnector(ApacheConfig.create(number, 500, Runtime.getRuntime().availableProcessors()))
//        HttpComponentsClientHttpConnector(ApacheConfig.create(number, 500, 25))
        HttpComponentsClientHttpConnector(client)
}

@Component
@Profile("pool8")
class ApacheWebClientFactory8(
    private val webClientBuilder: WebClient.Builder,
) : ApacheWebClientFactory {
//    val client = ApacheConfig.create(1, 500, Runtime.getRuntime().availableProcessors())

//    val client = ApacheConfig.create(1, 600, Runtime.getRuntime().availableProcessors() * 20)
//    val client = ApacheConfig.create(1, 200, Runtime.getRuntime().availableProcessors() * 20)
//    ilosc watkow * 4 daje prawie takie same wyniki co 500 watkow XD
    // ofc jeśli chodzi o klienta samego w sobie, bo na http RPS ciut różnica jest :thinking_face:

    // jak liczba watkow jest wieksza niz liczba klientow, uwzgledniajac to, ze jest wielokrotnoscia availableProcessors,
    // to dziala tak samo szybko, jak ogromna ilosc watkow
//    val client = ApacheConfig.create(1, 500, Runtime.getRuntime().availableProcessors() * 4)

    // jak liczba watkow jest rowna liczbie klientow, to RPsy dla samych klientow sa dokladnie takie same
    // natomiast odnosnie http server RPS, to widac że jest ciut gorzej, że różnica jest mocno zauważalna

//    // chyba jest po prostu cos nie tak z synchronizacja tych danych XD
    // NA TYM TRZEBA SIE SKUPIC I ZNALEZC ROZNICE
    val client = ApacheConfig.create(1, 500,  20)


    // jest zdecydowanie mniej RPSow zarowno w kliencie, jak i przy serwerze
//    val client = ApacheConfig.create(1, 500,  2)




    // czasy klientow sa minimalnie wieksze, jak jest wiecej watkow, wiec tym bardziej nie wiem, czemu jest wiecej RPS XD

    override fun createWebClient(number: Int): WebClient =
        webClientBuilder
            .clientConnector(createConnector(number))
            .build()

    private fun createConnector(number: Int): ClientHttpConnector =
        HttpComponentsClientHttpConnector(client)
}
