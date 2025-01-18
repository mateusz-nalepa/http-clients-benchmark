package com.mateusznalepa.http.clients.config

import io.micrometer.core.instrument.Meter.Id
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class DummyPrometheusConfig(
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