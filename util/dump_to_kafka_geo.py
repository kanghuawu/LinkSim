from ws4py.client.threadedclient import WebSocketClient
from datetime import datetime
from kafka import KafkaProducer
import time
import json
import sys
import time
from threading import Thread

TOPIC = 'geo'
KAFKA_HOST = ['localhost:9092']

def send_to_kafka(file):
    producer = KafkaProducer(bootstrap_servers=KAFKA_HOST, value_serializer=str.encode)
    with open(file, 'r') as input_f:
        for line in input_f:
            producer.send(TOPIC, value=str(line))
            time.sleep(0.5)

    producer.flush()


if __name__ == '__main__':
    if len(sys.argv) != 2:
        raise Exception('incorrect args')
    send_to_kafka(sys.argv[1])
