//@file:DependsOn("ch.qos.logback:logback-core:1.2.6")
//@file:DependsOn("ch.qos.logback:logback-classic:1.2.6")
//@file:DependsOn("org.slf4j:slf4j-api:1.7.32")

@file:DependsOn("org.springframework.boot:spring-boot-starter-webflux:3.2.5")

@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
@file:DependsOn("com.squareup.okhttp3:okhttp:4.9.2")

import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.http.client.ReactorResourceFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import reactor.netty.resources.LoopResources
import reactor.util.retry.Retry
import java.time.Duration
import java.util.concurrent.TimeUnit

object Client {

    val webClient = createWebClient()

    private fun createWebClient(): WebClient {
        val size = 16 * 1024 * 1024

        val sharedLoopResources = LoopResources.create("1-", 64, true)

        val strategies =
            ExchangeStrategies.builder()
                .codecs { codecs -> codecs.defaultCodecs().maxInMemorySize(size) }
                .build()

        return SslContextBuilder
            .forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build()
            .let { sslContext -> HttpClient.create().secure { it.sslContext(sslContext) } }
            .let {

                val clientCustomization: (t: HttpClient) -> HttpClient = { client -> client }


                val reactorRequestFactory = ReactorResourceFactory().apply {
                    connectionProvider = ConnectionProvider
                        .builder("send1-")
                        .maxConnections(5000)
                        .pendingAcquireMaxCount(5000 * 3)
                        .build()
                    loopResources = sharedLoopResources
                }

                ReactorClientHttpConnector(reactorRequestFactory, clientCustomization)
            }
            .let {
                WebClient
                    .builder()
                    .exchangeStrategies(strategies)
                    .clientConnector(it)
                    .build()
            }
    }
}


fun main() {
    val targetUrl = "http://localhost:8083/dummy/1" // Zmień na docelowy URL
    val requestsPerSecond = 75
    val duration = 60L * 60L // Czas trwania w sekundach

    runBlocking {
        sendRequestsPerSecond(targetUrl, requestsPerSecond, duration)
    }
}

suspend fun sendRequestsPerSecond(url: String, requestsPerSecond: Int, duration: Long) {
    val interval = 1000L / requestsPerSecond

    withContext(Dispatchers.IO) {
        val endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(duration)
        while (System.currentTimeMillis() < endTime) {
            launch {
                Client
                    .webClient
                    .get()
                    .uri(url)
                    .retrieve()
                    .toEntity(String::class.java)
                    .subscribe()
//                    println("Status Code: ${response.code}")
            }
            delay(interval)
        }
    }
}

main()