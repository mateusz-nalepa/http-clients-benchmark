#!/bin/bash

# Run bpftrace in background
bpftrace aaRead1.bt &

# PID of bpftrace process
BPF_PID=$!

# Wait 10 seconds
sleep 10

# end bpftrace process
kill $BPF_PID

echo "Script has finished after 10 seconds"
