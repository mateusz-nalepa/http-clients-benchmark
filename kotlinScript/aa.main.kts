//@file:DependsOn("ch.qos.logback:logback-core:1.2.6")
//@file:DependsOn("ch.qos.logback:logback-classic:1.2.6")
//@file:DependsOn("org.slf4j:slf4j-api:1.7.32")

    @file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    @file:DependsOn("com.squareup.okhttp3:okhttp:4.9.2")

import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

fun main() {
    val targetUrl = "http://localhost:8081/dummy/1" // Zmień na docelowy URL
    val requestsPerSecond = 100
    val duration = 60L * 60L // Czas trwania w sekundach

    runBlocking {
        sendRequestsPerSecond(targetUrl, requestsPerSecond, duration)
    }
}

suspend fun sendRequestsPerSecond(url: String, requestsPerSecond: Int, duration: Long) {
    val client = OkHttpClient()
    val interval = 1000L / requestsPerSecond

    withContext(Dispatchers.IO) {
        val endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(duration)
        while (System.currentTimeMillis() < endTime) {
            launch {
                val request = Request.Builder()
                    .url(url)
                    .build()

                client.newCall(request).execute().use { response ->
                    println("Status Code: ${response.code}")
                }
            }
            delay(interval)
        }
    }
}

main()