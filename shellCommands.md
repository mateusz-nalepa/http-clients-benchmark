```shell
sudo bpftrace -e 'profile:hz:99 /pid == 23163/ { @[kstack(3)] = count(); } interval:s:1 { exit(); }' > wyniki4.txt
```


```shell
./stackcollapse-bpftrace.pl wyniki4.txt > out.foldedV4
```

```shell
./flamegraph.pl out.foldedV4 > kernel4.svg
```

# bpftrace dla kernel stack
```shell
sudo bpftrace -e 'profile:hz:99 /pid == 77060/ { @[kstack()] = count(); } interval:s:120 { exit(); }' > apache11_8t.bpftrace &&
./stackcollapse-bpftrace.pl apache11_8t.bpftrace > apache11_8t.folded && 
./flamegraph.pl apache11_8t.folded > apache11_8t.bpftrace.svg
```

```shell
sudo bpftrace -e 'profile:hz:99 /pid == 77396/ { @[kstack()] = count(); } interval:s:120 { exit(); }' > netty11_8t.bpftrace &&
./stackcollapse-bpftrace.pl netty11_8t.bpftrace > netty11_8t.folded && 
./flamegraph.pl netty11_8t.folded > netty11_8t.bpftrace.svg
```


# Async profiler

```shell
./asprof -d 300 -f apache_12_200t_bez_walla.html 140482
```

```shell
./asprof -d 300 -f netty_12_200t_bez_walla.html 140146
```




```text
sys_sendto
```

Tego w ogóle nie ma dla Apache


# Inne jeszcze XD
```shell
perf record -F 99 -p 6730 -g -- sleep 5
i dopiero po chwili te XD


perf script > apache_perf.perf &&
./stackcollapse-perf.pl apache_perf.perf > apache_perf.folded && 
./flamegraph.pl apache_perf.folded > apache_perf.svg
```

oraz
```shell
perf record -F 99 -p 90624 -g -- sleep 120 && 
perf script > apache_perf.perf &&
./stackcollapse-perf.pl netty_perf.perf > netty_perf.folded && 
./flamegraph.pl netty_perf.folded > netty_perf.svg
```