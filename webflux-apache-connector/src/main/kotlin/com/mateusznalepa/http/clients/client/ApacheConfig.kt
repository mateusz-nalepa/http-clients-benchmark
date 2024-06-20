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

    fun create(number: Int, maxConn: Int, threadCount: Int): CloseableHttpAsyncClient {
        val ioReactorConfig = IOReactorConfig
            .custom()
            .setSoTimeout(Timeout.ofMilliseconds(500))
            .setSelectInterval(Timeout.ofMilliseconds(50))
            .setIoThreadCount(threadCount)
            .setTcpNoDelay(true)
            .setSoKeepAlive(true) //                                        .setRcvBufSize(8192)
            .build()

        // maxConnTotal - ma sens, jak sie lacze do wiecej niz 1 hosta
        // maxConnPerRoute - czyli ile jest do konkretnego hosta
        // jak mam 2 hosty i maxConnPerRoute =6 to maxymalnie moge miec 12 polaczen, wiec ustawianie wtedy maxTotal na wieksze niz 12 nie ma sensu, tak mowi copilot XD
        val connectionManager =
            PoolingAsyncClientConnectionManagerBuilder
                .create()
//                .setMaxConnTotal(150)
//                .setMaxConnPerRoute(150)
//                .setMaxConnTotal(300)
//                .setMaxConnPerRoute(300)
//                .setMaxConnTotal(600)
//                .setMaxConnPerRoute(600)

                // setup pod wiele watkow xd
                .setMaxConnTotal(maxConn)
                .setMaxConnPerRoute(maxConn)
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
        Metrics.gauge("connection_available", listOf(Tag.of("clientNumber", number.toString()), Tag.of("type", "available_idle")), connectionManager) {
            manager -> manager.totalStats.available.toDouble()
        }

        Metrics.gauge("connection_available", listOf(Tag.of("clientNumber", number.toString()), Tag.of("type", "max")), connectionManager) {
                manager -> manager.totalStats.max.toDouble()
        }

        Metrics.gauge("connection_available", listOf(Tag.of("clientNumber", number.toString()), Tag.of("type", "leased")), connectionManager) {
                manager -> manager.totalStats.leased.toDouble()
        }
        Metrics.gauge("connection_available", listOf(Tag.of("clientNumber", number.toString()), Tag.of("type", "pending")), connectionManager) {
                manager -> manager.totalStats.pending.toDouble()
        }

        Metrics.gauge("connection_available", listOf(Tag.of("clientNumber", number.toString()), Tag.of("type", "usage")), connectionManager) {
                manager -> (manager.totalStats.leased.toDouble() / manager.totalStats.max.toDouble()) * 100.0
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