#!/bin/sh
set -e

echo "waiting kafka..."

until /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --list >/dev/null 2>&1; do
  sleep 2
done

while IFS='|' read topic partitions rf; do
  echo "creating $topic"

  /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 \
    --create --if-not-exists \
    --topic "$topic" \
    --partitions "$partitions" \
    --replication-factor "$rf"

done < /config/topics.txt