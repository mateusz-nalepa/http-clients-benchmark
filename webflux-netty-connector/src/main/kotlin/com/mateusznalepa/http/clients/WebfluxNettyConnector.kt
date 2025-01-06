package com.mateusznalepa.http.clients

import io.micrometer.core.instrument.Meter.Id
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class WebfluxNettyConnector

fun main(args: Array<String>) {
    runApplication<WebfluxNettyConnector>(*args)
}

@Component
class PrometheusPercentilesConfig(
    private val meterRegistry: PrometheusMeterRegistry,
) {
    @PostConstruct
    fun configurePercentiles() {
        meterRegistry
            .config()
            .meterFilter(object : MeterFilter {

                override fun configure(id: Id, config: DistributionStatisticConfig): DistributionStatisticConfig? {
                    return DistributionStatisticConfig
                        .builder()
                        .percentiles(0.99, 0.999)
                        .build()
                        .merge(config)
                }
            })
    }

}