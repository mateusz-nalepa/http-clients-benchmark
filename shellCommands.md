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
sudo bpftrace -e 'profile:hz:99 /pid == 12216/ { @[kstack()] = count(); } interval:s:120 { exit(); }' > apache10.bpftrace &&
./stackcollapse-bpftrace.pl apache10.bpftrace > apache10.folded && 
./flamegraph.pl apache10.folded > apache10.bpftrace.svg
```

```shell
sudo bpftrace -e 'profile:hz:99 /pid == 12552/ { @[kstack()] = count(); } interval:s:120 { exit(); }' > netty10.bpftrace &&
./stackcollapse-bpftrace.pl netty10.bpftrace > netty10.folded && 
./flamegraph.pl netty10.folded > netty10.bpftrace.svg
```


# Async profiler

```shell
./asprof -d 60 -f apache_12.html -e wall -t 6154
```

```shell
./asprof -d 60 -f netty_12.html -e wall -t 5831
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