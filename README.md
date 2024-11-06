# Apache Kafka Stream Processing with Kafka Streams

## Interacting with Kafka

Starting the cluster

```bash
docker compose up
```

Execution command in the cluster

```bash
docker exec -it java-kafka-streams-demo-kafka-1 bash
```

```bash
kafka-console-producer --broker-list localhost:9092 --topic orders
```