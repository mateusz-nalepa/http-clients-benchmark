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

    fun create(number: Int, maxConn: Int, ): CloseableHttpAsyncClient {
        val ioReactorConfig = IOReactorConfig
            .custom()
            .setSoTimeout(Timeout.ofMilliseconds(500 * 100))
            .setSelectInterval(Timeout.ofMilliseconds(50))
            .setIoThreadCount(Runtime.getRuntime().availableProcessors())
            .setTcpNoDelay(true)
            .setSoKeepAlive(true)
            .build()

        val connectionManager =
            PoolingAsyncClientConnectionManagerBuilder
                .create()
                .setMaxConnTotal(maxConn)
                .setMaxConnPerRoute(maxConn)
                .setDefaultConnectionConfig(
                    ConnectionConfig
                        .custom()
                        .setValidateAfterInactivity(TimeValue.ofSeconds(3 * 100))
                        .setConnectTimeout(Timeout.ofMilliseconds(500 * 100))
                        .setSocketTimeout(Timeout.ofMilliseconds(500 * 100))
                        .build()
                )
                .build()

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
                    .custom()
                    .build()
            )
            .setDefaultRequestConfig(
                RequestConfig
                    .custom()
                    .setConnectTimeout(Timeout.ofMilliseconds(500 * 100))
                    .setResponseTimeout(Timeout.ofMilliseconds(500 * 100))
                    .setConnectionRequestTimeout(Timeout.ofMilliseconds(500 * 100))
                    .setRedirectsEnabled(false)
                    .build()
            )
            .disableAutomaticRetries()
            .build()
    }
}
