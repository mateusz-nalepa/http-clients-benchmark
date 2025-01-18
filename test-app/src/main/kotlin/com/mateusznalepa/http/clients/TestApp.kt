package com.mateusznalepa.http.clients

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TestApp

fun main(args: Array<String>) {
    runApplication<TestApp>(*args)
    TestAppConfig.printConfig()
}
