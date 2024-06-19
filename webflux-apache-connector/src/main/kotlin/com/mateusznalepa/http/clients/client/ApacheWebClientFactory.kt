package com.mateusznalepa.http.clients.client

import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder
import org.apache.hc.core5.http.config.Http1Config
import org.apache.hc.core5.reactor.IOReactorConfig
import org.apache.hc.core5.util.TimeValue
import org.apache.hc.core5.util.Timeout
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector
import org.springframework.scheduling.concurrent.CustomizableThreadFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient


@Component
class ApacheWebClientFactory(
    private val webClientBuilder: WebClient.Builder,
) {

//    val ioReactorConfig = IOReactorConfig
//        .custom()
//        .setSoTimeout(Timeout.ofMilliseconds(500))
//        .setSelectInterval(Timeout.ofMilliseconds(50))
//        .setIoThreadCount(Runtime.getRuntime().availableProcessors())
//        .setTcpNoDelay(true)
//        .setSoKeepAlive(true) //                                        .setRcvBufSize(8192)
//        .build()
//
//    val connectionManager =
//        PoolingAsyncClientConnectionManagerBuilder
//            .create()
//            .setMaxConnTotal(500)
//            .setMaxConnPerRoute(500)
//            .setDefaultConnectionConfig(
//                ConnectionConfig
//                    .custom()
//                    .setValidateAfterInactivity(TimeValue.ofSeconds(3))
//                    .setConnectTimeout(Timeout.ofMilliseconds(500))
//                    .setSocketTimeout(Timeout.ofMilliseconds(500))
//                    .build()
//            )
//            .build()
//
//
//    val build = HttpAsyncClients
//        .custom()
//        .setConnectionManager(connectionManager)
//        .setThreadFactory(
//            CustomizableThreadFactory("http-client-$1-webflux-apache-")
//        )
//        .setIOReactorConfig(
//            ioReactorConfig
//        )
//        .setHttp1Config(
//            Http1Config
//                .custom() //                                        .setBufferSize(8192)
//                .build()
//        )
//        .setDefaultRequestConfig(
//            RequestConfig
//                .custom()
//                .setConnectTimeout(Timeout.ofMilliseconds(500))
//                .setResponseTimeout(Timeout.ofMilliseconds(500))
//                .setConnectionRequestTimeout(Timeout.ofMilliseconds(500))
//                .setRedirectsEnabled(false)
//                .build()
//        )
//        .disableAutomaticRetries()
//        .build()


    fun createWebClient(number: Int): WebClient =
        webClientBuilder
            .clientConnector(createConnector(number))
            .build()

    private fun createConnector(number: Int): ClientHttpConnector {
        val connectionManager =
            PoolingAsyncClientConnectionManagerBuilder
                .create()
                .setMaxConnTotal(500)
                .setMaxConnPerRoute(500)
                .setDefaultConnectionConfig(
                    ConnectionConfig
                        .custom()
                        .setValidateAfterInactivity(TimeValue.ofSeconds(3))
                        .setConnectTimeout(Timeout.ofMilliseconds(500))
                        .setSocketTimeout(Timeout.ofMilliseconds(500))
                        .build()
                )
                .build()

        val client =
            HttpAsyncClients
                .custom()
                .setConnectionManager(connectionManager)
                .setThreadFactory(
                    CustomizableThreadFactory("http-client-$number-webflux-apache-")
                )
                .setIOReactorConfig(
                    IOReactorConfig
                        .custom()
                        .setSoTimeout(Timeout.ofMilliseconds(500))
                        .setSelectInterval(Timeout.ofMilliseconds(50))
                        .setIoThreadCount(Runtime.getRuntime().availableProcessors())
                        .setTcpNoDelay(true)
                        .setSoKeepAlive(true) //                                        .setRcvBufSize(8192)
                        .build()
                )
                .setHttp1Config(
                    Http1Config
                        .custom() //                                        .setBufferSize(8192)
                        .build()
                )
                .setDefaultRequestConfig(
                    RequestConfig
                        .custom()
                        .setConnectTimeout(Timeout.ofMilliseconds(500))
                        .setResponseTimeout(Timeout.ofMilliseconds(500))
                        .setConnectionRequestTimeout(Timeout.ofMilliseconds(500))
                        .setRedirectsEnabled(false)
                        .build()
                )
                .disableAutomaticRetries()
                .build()

        return HttpComponentsClientHttpConnector(client)
    }
}
