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


jak chce bez walla to trzeba:
```shell
sudo su
sysctl kernel.kptr_restrict=0
sysctl kernel.perf_event_paranoid=1
exit
```

```shell
[parallel-3] INFO LOG - AVG: 192 RPS 		 ETA: 01hrs 17mins 01sec
./asprof -d 60 -f apache_6GB_wall.html -e wall -t 9412
```

```shell
./asprof -d 60 -f netty_6GB_wall.html -e wall -t 10242
```




```text
sys_sendto
```

Tego w ogóle nie ma dla Apache


# Inne jeszcze XD
```shell
#perf record -F 99 -p 58360 -g -- sleep 10
perf record -F 99 -p 58360 -g -- sleep 10
i dopiero po chwili te XD


perf script > apache_perf_8.perf &&
./stackcollapse-perf.pl apache_perf_8.perf > apache_perf_8.folded && 
./flamegraph.pl apache_perf_8.folded > apache_perf_8.svg
```

oraz
```shell
perf record -F 99 -p 90624 -g -- sleep 120 && 
perf script > apache_perf.perf &&
./stackcollapse-perf.pl netty_perf.perf > netty_perf.folded && 
./flamegraph.pl netty_perf.folded > netty_perf.svg
```



### Nowe

sudo su
sysctl kernel.kptr_restrict=0
sysctl kernel.perf_event_paranoid=1
exit

./asprof -d 60 -f nettyDuzo5.html -e cpu 5519
./asprof -d 60 -f nettyMalo5.html -e cpu 6489


pidstat -w -p 5519 1


cat /proc/5519/sched | grep "switch\|migration"


#### poczytaj o jeamalloc i ometrykuj PoolArena xd