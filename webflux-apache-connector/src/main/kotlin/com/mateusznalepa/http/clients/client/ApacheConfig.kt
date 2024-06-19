package com.mateusznalepa.http.clients.client

import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Tag
import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder
import org.apache.hc.core5.http.config.Http1Config
import org.apache.hc.core5.reactor.IOReactorConfig
import org.apache.hc.core5.util.TimeValue
import org.apache.hc.core5.util.Timeout
import org.springframework.scheduling.concurrent.CustomizableThreadFactory

object ApacheConfig {

    fun create(number: Int): CloseableHttpAsyncClient {
        val ioReactorConfig = IOReactorConfig
            .custom()
            .setSoTimeout(Timeout.ofMilliseconds(500))
            .setSelectInterval(Timeout.ofMilliseconds(50))
            .setIoThreadCount(Runtime.getRuntime().availableProcessors())
            .setTcpNoDelay(true)
            .setSoKeepAlive(true) //                                        .setRcvBufSize(8192)
            .build()

        val connectionManager =
            PoolingAsyncClientConnectionManagerBuilder
                .create()
                .setMaxConnTotal(500)
                .setMaxConnPerRoute(500)
                .setDefaultConnectionConfig(
                    ConnectionConfig
                        .custom()
                        .setValidateAfterInactivity(TimeValue.ofSeconds(3))
                        .setConnectTimeout(Timeout.ofMilliseconds(505))
                        .setSocketTimeout(Timeout.ofMilliseconds(507))
                        .build()
                )
                .build()

        // TODO: dodaj to na grafanie XD
        Metrics.gauge("connection_available", listOf(Tag.of("type", "available_idle")), connectionManager) {
            manager -> manager.totalStats.available.toDouble()
        }

        Metrics.gauge("connection_available", listOf(Tag.of("type", "max")), connectionManager) {
                manager -> manager.totalStats.max.toDouble()
        }

        Metrics.gauge("connection_available", listOf(Tag.of("type", "leased")), connectionManager) {
                manager -> manager.totalStats.leased.toDouble()
        }
        Metrics.gauge("connection_available", listOf(Tag.of("type", "pending")), connectionManager) {
                manager -> manager.totalStats.pending.toDouble()
        }

        return HttpAsyncClients
            .custom()
            .setConnectionManager(connectionManager)
            .setThreadFactory(
                CustomizableThreadFactory("http-apache-$number-")
            )
            .setIOReactorConfig(
                ioReactorConfig
            )
            .setHttp1Config(
                Http1Config
                    .custom() //                                        .setBufferSize(8192)
                    .build()
            )
            .setDefaultRequestConfig(
                RequestConfig
                    .custom()
                    .setConnectTimeout(Timeout.ofMilliseconds(509))
                    .setResponseTimeout(Timeout.ofMilliseconds(511))
                    .setConnectionRequestTimeout(Timeout.ofMilliseconds(513))
                    .setRedirectsEnabled(false)
                    .build()
            )
            .disableAutomaticRetries()
            .build()
    }
}