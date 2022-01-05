# favourite-color-kafka-scala

The aim of this project is to experiment with Kafka Streams with Scala programming language

Starting from a Kafka topic **favourite-color-input**, our goal is to

- Read the favourite color of every user in the format `userId,color`
- Filter out the data that it's not in that format
- Take only **blue**, **green** and **red** as allowed colors
- For every color, count the number of users that choosen that color as it's favourite one, in the format `color,count`
- Write the result to the topic **favourite-color-output**

Please note that after specifying it's favourite color, a user can change mind and choose another color, so the overall count may decrease

## Prerequisites

You need Docker and Docker Compose to run this project as the Kafka infrastructure (a minimal Zookeeper cluster and a minimal Kafka cluster) is provided by a `docker-compose.yml` file

In alternative, you can use an existing Kafka installation



## Start the Kafka infrastructure

`docker-compose up`



## Create the Kafka topics

```
bin/kafka-topics.sh --create \
    --zookeeper localhost:2181 \
    --replication-factor 1 \
    --partitions 1 \
    --topic favourite-color-input
```

```
bin/kafka-topics.sh --create \
    --zookeeper localhost:2181 \
    --replication-factor 1 \
    --partitions 1 \
    --config cleanup.policy=compact \
    --topic favourite-color-by-user
```

```
bin/kafka-topics.sh --create \
    --zookeeper localhost:2181 \
    --replication-factor 1 \
    --partitions 1 \
    --config cleanup.policy=compact \
    --topic favourite-color-output
```



## Stop the Kafka infrastructure

`docker-compose stop`



## Desytroy the infrastructure

`docker-compose down`



## Build the application

`sbt clean compile`



## Run the application

`sbt run`



## Produce some records to the input topic

```sh
bin/kafka-console-producer.sh \
    --broker-list localhost:9092 \
    --topic favourite-color-input
    
    >Adam,green
    >Andrew,red
    >Sandra,blue
    >Oliver,blue
    >Sandra,red
```



## Consume on the output topic

```sh
bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --topic favourite-color-output \
    --from-beginning
    
    >green 1
    >red 1
    >blue 1
    >blue 2
    >blue 1
    >red 2
```
