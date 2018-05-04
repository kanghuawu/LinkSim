from ws4py.client.threadedclient import WebSocketClient
from datetime import datetime
from kafka import KafkaProducer
import time
import json

TOPIC = 'meetup'
KAFKA_HOST = ['localhost:9092']

class DummyClient(WebSocketClient):
    def __init__(self, url):
        super().__init__(url=url)
        self.producer = KafkaProducer(bootstrap_servers=KAFKA_HOST, value_serializer=str.encode)

    def opened(self):
        print("starting")

    def closed(self, code, reason=None):
        print("Closed down", code, reason)
        print('Flushing')
        self.producer.flush()

    def received_message(self, m):
        print(str(m))
        self.producer.send(TOPIC, value=str(m))

if __name__ == '__main__':
    try:
        ws = DummyClient(url='ws://stream.meetup.com/2/rsvps')
        ws.connect()
        ws.run_forever()
    except KeyboardInterrupt:
        print("stopped by KeyboardInterrupt")
        ws.close()
