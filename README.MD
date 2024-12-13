[MCVE](https://stackoverflow.com/help/minimal-reproducible-example) for [SpringBoot + Netty: Higher Performance when using dedicated threads for every httpClient](https://stackoverflow.com/questions/79161054/springboot-netty-higher-performance-when-using-dedicated-threads-for-every-ht)


# How To Run
1. Execute given command to start two netty apps and a mock server
```shell
./run.sh
```

2. Run kotlin scripts from IntelliJ

# Expected behaviour:
More RPS for `script1.main.kts`

# Architecture
![architecture.png](images/architecture.png)

# Grafana Screenshot
![grafana.png](images/grafana.png)


https://github.com/pniewiejski/learn-io-polling?tab=readme-ov-file

// tutaj jest info o wielu klientach http xd
https://idea.popcount.org/2017-02-20-epoll-is-fundamentally-broken-12/


https://explog.in/notes/sched.html
https://github.com/torvalds/linux/blob/v5.4/include/trace/events/sched.h


-DisUseDedicatedThreadsPerClient=true -DmemoryAllocatorType=pooled -DmockServerPort=8091 -Dserver.port=8081 -Xms1024m -Xmx1024m -DthreadsForResponse=true

-DisUseDedicatedThreadsPerClient=true -DmemoryAllocatorType=pooled -DmockServerPort=8091 -Dserver.port=8082 -Xms1024m -Xmx1024m -DthreadsForResponse=false