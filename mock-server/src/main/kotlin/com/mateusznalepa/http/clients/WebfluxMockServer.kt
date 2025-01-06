package com.mateusznalepa.http.clients

//import org.openjdk.jmh.annotations.Benchmark
//import org.openjdk.jmh.annotations.BenchmarkMode
//import org.openjdk.jmh.annotations.Mode
//import org.openjdk.jmh.annotations.OutputTimeUnit
//import org.openjdk.jmh.annotations.Scope
//import org.openjdk.jmh.annotations.State
//import org.openjdk.jmh.runner.Runner
//import org.openjdk.jmh.runner.options.Options
//import org.openjdk.jmh.runner.options.OptionsBuilder
//import org.openjdk.jmh.runner.options.TimeValue
import io.micrometer.core.instrument.Meter.Id
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

//import java.util.concurrent.TimeUnit

//@SpringBootApplication
//class WebfluxMockServer {}
//
//fun main(args: Array<String>) {


@SpringBootApplication
class WebfluxMockServer

fun main(args: Array<String>) {
    runApplication<WebfluxMockServer>(*args)

//    val opt: Options =
//        OptionsBuilder()
//            .include(".*" + XDDD::class.java.simpleName + ".*")
//
//            .warmupIterations(5)
//            .warmupTime(TimeValue.seconds(10))
//
//            .measurementIterations(5)
//            .measurementTime(TimeValue.seconds(10))
//
//            .forks(1)
//            .threads(10)
//
//            .syncIterations(false)
//
//            .build()
//
//    Runner(opt).run()


}


//@State(Scope.Thread)
//@BenchmarkMode(Mode.Throughput)
//@OutputTimeUnit(TimeUnit.SECONDS)
//object XDDD {
//
//    @Benchmark
//    fun asd() {
//
//    }
//}

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