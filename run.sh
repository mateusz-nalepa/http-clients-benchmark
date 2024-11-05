#!/usr/bin/env sh

echo "Hello World!"

MOCK_SERVER_PORT=8091
NETTY_PORT_ONE=8081
NETTY_PORT_TWO=8082


kill_app_on_port_if_any() {
    PORT=$1

    PID_IF_ANY=$(lsof -t -i :$PORT)

    if [ -z "$PID_IF_ANY" ]; then
      echo "No process found running on port $PORT"
    else
      for PID in $PID_IF_ANY; do
        echo "Killing PID $PID"
        kill -9 $PID
      done
      echo "Killed app on port $PORT"
    fi
}

kill_app_on_port_if_any $MOCK_SERVER_PORT
kill_app_on_port_if_any $NETTY_PORT_ONE
kill_app_on_port_if_any $NETTY_PORT_TWO

wait_for_port() {
  PORT=$1

  if [ -z "$PORT" ]; then
    echo "Usage: wait_for_port <port>"
    return 1
  fi

  echo "Waiting for port $PORT to become available..."

  while ! nc -z localhost $PORT; do
    sleep 1
  done

  echo "Port $PORT is now available!"
}

./gradlew :mock-server:assemble
java -jar mock-server/build/libs/mock-server-0.0.1-SNAPSHOT.jar \
 --server.port=$MOCK_SERVER_PORT &


wait_for_port $MOCK_SERVER_PORT


./gradlew :webflux-netty-connector:assemble

java -jar webflux-netty-connector/build/libs/webflux-netty-connector-0.0.1-SNAPSHOT.jar \
 --czyJestWiecejWatkow=true \
 --typAlokatora=pooled \
 --mockServerPort=$MOCK_SERVER_PORT \
 --server.port=$NETTY_PORT_ONE &

wait_for_port $NETTY_PORT_ONE

java -jar webflux-netty-connector/build/libs/webflux-netty-connector-0.0.1-SNAPSHOT.jar \
 --czyJestWiecejWatkow=false \
 --typAlokatora=pooled \
 --mockServerPort=$MOCK_SERVER_PORT \
 --server.port=$NETTY_PORT_TWO &

wait_for_port $NETTY_PORT_TWO

echo "Apps started!"

