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