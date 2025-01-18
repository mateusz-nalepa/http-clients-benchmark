package com.mateusznalepa.http.clients

object TestAppConfig {
    const val CPU_BOUND_SCHEDULER_ACTIVE = true

    // Remember about: -Dio.netty.allocator.type=adaptive
    const val CLIENT_MEMORY_ALLOCATOR_TYPE = "pooled"
//    const val CLIENT_MEMORY_ALLOCATOR_TYPE = "unpooled"
//    const val CLIENT_MEMORY_ALLOCATOR_TYPE = "adaptive"

    const val USE_DEDICATED_THREADS_FOR_CLIENT = false
    const val MOCK_SERVER_PORT = 8091

    val HTTP_CLIENTS = mapOf(20 to "l")

    fun printConfig() {
        println("Config")
        println("")
        println("CPU_BOUND_SCHEDULER_ACTIVE: $CPU_BOUND_SCHEDULER_ACTIVE")
        println("CLIENT_MEMORY_ALLOCATOR_TYPE: $CLIENT_MEMORY_ALLOCATOR_TYPE")
        println("USE_DEDICATED_THREADS_FOR_CLIENT: $USE_DEDICATED_THREADS_FOR_CLIENT")
        println("MOCK_SERVER_PORT: $MOCK_SERVER_PORT")
        println("HTTP_CLIENTS: $HTTP_CLIENTS")
        println("")
        println("App Ready!")
    }
}
