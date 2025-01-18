#!/bin/bash

# Uruchomienie skryptu bpftrace w tle
bpftrace aaRead1.bt &

# PID procesu bpftrace
BPF_PID=$!

# Czekanie określony czas (np. 10 sekund)
sleep 10

# Wysłanie sygnału zakończenia do procesu bpftrace
kill $BPF_PID

echo "Skrypt bpftrace został zakończony po 10 sekundach."
