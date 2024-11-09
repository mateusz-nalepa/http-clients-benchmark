#!/usr/bin/env kotlin

/**
 * How to Run
 * 1. brew install kotlin
 * 2. Have a look at the ScriptParameters class
 * 3. Put files to migrate in `source` folder, next to script.
 *      It's good to split one large into few smaller
 *      Eg: split -l 10000 /path-to-file
 * 4. Run it by using arrow from IntelliJ :D
 */

@file:DependsOn("ch.qos.logback:logback-core:1.2.6")
@file:DependsOn("ch.qos.logback:logback-classic:1.2.6")
@file:DependsOn("org.slf4j:slf4j-api:1.7.32")

@file:DependsOn("com.fasterxml.jackson.core:jackson-databind:2.12.1")
@file:DependsOn("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.1")
@file:DependsOn("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.1")
@file:DependsOn("org.springframework.boot:spring-boot-starter-webflux:3.2.5")
@file:DependsOn("io.netty:netty-all:4.1.109.Final")
@file:DependsOn("org.apache.commons:commons-lang3:3.12.0")
@file:DependsOn("io.dropwizard.metrics:metrics-core:4.1.2")

@file:DependsOn("io.micrometer:micrometer-registry-prometheus:1.13.0")

import com.codahale.metrics.Meter
import com.codahale.metrics.MetricRegistry
import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.Meter.Id
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.apache.commons.lang3.time.DurationFormatUtils
import org.slf4j.Logger
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.util.retry.Retry
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.absoluteValue
import kotlin.system.measureTimeMillis

object ScriptLogger {
    val logger: Logger = org.slf4j.LoggerFactory.getLogger("LOG")
}

object ScriptParameters {
    const val SOURCE_DIRECTORY_WITH_FILES = "source22"
    const val BATCH_SIZE = 50
    const val APP_URL = "http://localhost:8082"
}

object Prometheus {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    init {
        prometheus
            .config()
            .meterFilter(object : MeterFilter {

                override fun configure(id: Id, config: DistributionStatisticConfig): DistributionStatisticConfig? {
                    return DistributionStatisticConfig.builder()
                        .percentiles(0.99)
                        .build()
                        .merge(config)
                }
            })
    }


    val senderSummary: DistributionSummary = prometheus.summary("time")
}

object Converter {
    fun convertSecondsToHumanReadableFormat(seconds: Long): String {
        return convertMillisToHumanReadableFormat(seconds * 1000)
    }

    fun convertMillisToHumanReadableFormat(miliSeconds: Long): String {
        return DurationFormatUtils.formatDuration(miliSeconds, "HH'hrs' mm'mins' ss'sec'")
    }
}

object ETA {
    private var scriptStartDate: LocalDateTime = LocalDateTime.now()

    private val metricRegistry = MetricRegistry()
    private val postRequestsMeter: Meter = metricRegistry.meter("sendRequests")
    val numberOfIdsToBeProcessed: Long =
        calculateNumberOfLines(
            File(ScriptParameters.SOURCE_DIRECTORY_WITH_FILES).toPath()
        )

    init {
        report()
    }

    fun markRequestSent() {
        postRequestsMeter.mark()
    }

    fun meanRate(): Int =
        postRequestsMeter.meanRate.toInt()

    private fun calculateNumberOfLines(sourceDirectoryWithFiles: Path): Long =
        sourceDirectoryWithFiles.toFile()
            .walk()
            .filter { it.isFile }
            .map { countNumberOfLines(it) }
            .sum()
            .also { ScriptLogger.logger.info("Number of lines is: $it") }

    private fun countNumberOfLines(file: File): Long =
        Files.lines(file.toPath()).count()

    private fun report() {
        Mono.just("dummy")
            .delayElement(Duration.ofSeconds(1))
            .map {
                printETA()
                it
            }
            .repeat()
            .subscribe()

        Mono.just("dummy")
            .delayElement(Duration.ofSeconds(10))
            .map {
                val asd = Prometheus.prometheus.scrape()
                ScriptLogger.logger.info("\n\n$asd")
                it
            }
            .repeat()
            .subscribe()
    }

    private fun printETA() {
        val numberOfLinesToBeSend = numberOfIdsToBeProcessed - postRequestsMeter.count
        val numberOfSentLines = numberOfIdsToBeProcessed - numberOfLinesToBeSend
        val average = postRequestsMeter.meanRate.toInt()

        if (average != 0) {
            val howManySecondsSenderShouldTake = (numberOfIdsToBeProcessed / average).toInt()
            val howManySecondsPassed =
                (Duration.between(LocalDateTime.now(), scriptStartDate).toMillis() / 1000).toInt().absoluteValue

            val eta =
                Converter.convertSecondsToHumanReadableFormat(
                    (howManySecondsSenderShouldTake - howManySecondsPassed).toLong()
                )

            val progress = (100 * numberOfSentLines) / numberOfIdsToBeProcessed

            ScriptLogger.logger.info(
//                "Progress: $progress% \t\t Sent: $numberOfSentLines \t\t Left: $numberOfLinesToBeSend \t\t Average: $average RPS \t\t ETA: $eta"
                "AVG: $average RPS \t\t ETA: $eta"
            )
        }
    }
}

class FileProcessor(
    private val sourceDirectoryWithFiles: Path,
) {
    fun readLinesAndExecute(consumesLinesAction: (Sequence<String>) -> Unit) {
        sourceDirectoryWithFiles
            .toFile()
            .walk()
            .filter { it.isFile }
            .sortedBy { it.name }
            .forEach { useFile(it, consumesLinesAction) }
    }

    private fun useFile(
        currentFile: File,
        consumesLinesAction: (Sequence<String>) -> Unit
    ) {
        ScriptLogger.logger.info("Processing file ${currentFile.name}")
        currentFile
            .bufferedReader()
            .use { it ->
                val linesFromFile: Sequence<String> =
                    it.lineSequence().filter { it.isNotBlank() && it.isNotEmpty() }
                consumesLinesAction(linesFromFile)
            }
    }
}

object Client {

    val webClient = createWebClient()

    private fun createWebClient(): WebClient {
        val size = 16 * 1024 * 1024

        val strategies =
            ExchangeStrategies.builder()
                .codecs { codecs -> codecs.defaultCodecs().maxInMemorySize(size) }
                .build()

        return SslContextBuilder
            .forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build()
            .let { sslContext -> HttpClient.create().secure { it.sslContext(sslContext) } }
            .let { ReactorClientHttpConnector(it) }
            .let {
                WebClient
                    .builder()
                    .exchangeStrategies(strategies)
                    .clientConnector(it)
                    .build()
            }
    }
}

object RequestSender {

    fun send(line: String): Mono<String> =
        Client
            .webClient
            .get()
            .uri("${ScriptParameters.APP_URL}/dummy/$line")
            .header("Accept", "application/json")
            .retrieve()
            .toEntity(String::class.java)
            .doOnError { ScriptLogger.logger.error("Error occurred for line: $line", it) }
            .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofMillis(500)))
            .doOnSuccess { ETA.markRequestSent() }
            .map { "Whatever" }
}

class SenderTool(
    private val fileProcessor: FileProcessor,
    private val requestSender: RequestSender,
) {

    fun process() {
        fileProcessor
            .readLinesAndExecute { lines ->
                lines
                    .chunked(ScriptParameters.BATCH_SIZE)
                    .onEach { sendChunkOfLines(it) }
                    .count()
            }
    }

    private fun sendChunkOfLines(lines: List<String>) {
        Flux.fromIterable(lines)
            .flatMap {
                requestSender
                    .send(line = it)
                    .elapsed()
                    .doOnNext { itt ->
                        Prometheus.senderSummary.record(itt.t1.toDouble())
                    }
                    .then()
            }
            .blockLast()
    }
}

class ScriptRunner() {
    fun run() {
        ScriptLogger.logger.info("Script started...")
        processScript()
        ScriptLogger.logger.info("Script Ended")
    }

    private fun processScript() {
        val senderTool = createSenderTool()

        val elapsedTimeMillis = measureTimeMillis { senderTool.process() }

        ScriptLogger.logger.info("Sender summary")
        ScriptLogger.logger.info("Sent ${ETA.numberOfIdsToBeProcessed} lines")
        ScriptLogger.logger.info("Elapsed time ${Converter.convertMillisToHumanReadableFormat(elapsedTimeMillis)}")
        ScriptLogger.logger.info("Average: ${ETA.meanRate()} RPS")
        ScriptLogger.logger.info("Prometheus")
        ScriptLogger.logger.info(Prometheus.prometheus.scrape())
    }

    private fun createSenderTool(): SenderTool =
        SenderTool(
            fileProcessor = FileProcessor(
                sourceDirectoryWithFiles = File(ScriptParameters.SOURCE_DIRECTORY_WITH_FILES).toPath(),
            ),
            requestSender = RequestSender
        )
}

System.setProperty("logback.configurationFile", "logback.xml")
ScriptRunner().run()
