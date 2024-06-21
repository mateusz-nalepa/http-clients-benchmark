package com.mateusznalepa.http.clients.client

import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Tag
import reactor.netty.resources.ConnectionPoolMetrics
import reactor.netty.resources.ConnectionProvider
import java.net.SocketAddress

class ReactorNettyMetrics(
    private val number: Int,
    private val maxConnections: Int,
) : ConnectionProvider.MeterRegistrar {
    override fun registerMetrics(
        poolName: String,
        id: String,
        remoteAddress: SocketAddress,
        metrics: ConnectionPoolMetrics
    ) {

        // TODO: czy te metryki w async apache dobre sa? :D chyba tak, skoro sa takie same dla connection pool przed i po wdrozeniu
        Metrics.gauge(
            "connection_available",
            listOf(Tag.of("clientNumber", number.toString()), Tag.of("type", "available_idle")), metrics
        ) { asd ->
            asd.idleSize().toDouble()
        }


        Metrics.gauge(
            "connection_available",
            listOf(Tag.of("clientNumber", number.toString()), Tag.of("type", "max")),
            metrics
        ) { asd ->
            maxConnections.toDouble()
        }

        Metrics.gauge(
            "connection_available",
            listOf(Tag.of("clientNumber", number.toString()), Tag.of("type", "leased")),
            metrics
        ) { asd ->
            metrics.acquiredSize().toDouble()
        }

        Metrics.gauge(
            "connection_available",
            listOf(Tag.of("clientNumber", number.toString()), Tag.of("type", "pending")),
            metrics
        ) { asd ->
            metrics.pendingAcquireSize().toDouble()
        }

        Metrics
            .gauge(
                "connection_available",
                listOf(Tag.of("clientNumber", number.toString()), Tag.of("type", "usage")),
                metrics
            ) { asd ->
                (metrics.acquiredSize().toDouble() / maxConnections.toDouble()) * 100.0
            }

    }

}