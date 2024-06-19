package com.mateusznalepa.http.clients

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebfluxApacheConnector

fun main(args: Array<String>) {
	runApplication<WebfluxApacheConnector>(*args)
}
