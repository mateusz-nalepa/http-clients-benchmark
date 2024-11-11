#!/bin/bash

# Uruchomienie skryptu bpftrace w tle
sudo bpftrace bbRead1.bt >> nalepa2.txt &

# PID procesu bpftrace
BPF_PID=$!

# Czekanie określony czas (np. 10 sekund)
sleep 5

# Wysłanie sygnału zakończenia do procesu bpftrace
kill -SIGINT $BPF_PID

echo "Skrypt bpftrace został zakończony po 10 sekundach."
