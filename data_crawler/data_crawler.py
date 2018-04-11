from ws4py.client.threadedclient import WebSocketClient
from datetime import datetime
from threading import Thread
import time
import boto3
import os

def upload_to_s3(name, dir):
    print('uploading', name)
    s3 = boto3.resource('s3')
    s3.meta.client.upload_file(dir, 'meetup-data-khwu', name)
    try:
        os.remove(dir)
        print('removing', name)
    except OSError:
        pass

class DummyClient(WebSocketClient):
    def __init__(self, url):
        super().__init__(url=url)
        self.file = FileHandler(dir='.')
        self.counter = 0
        self.max = 100000

    def opened(self):
        print("starting")
        

    def closed(self, code, reason=None):
        self.file.close()
        print("Closed down", code, reason)

    def received_message(self, m):
        self.file.write(str(m))
        self.counter += 1
        if self.counter > 0 and self.counter % self.max == 0:
            self.file.close()
            self.file = FileHandler('.')
            print(self.counter, 'messages')

class FileHandler:
    def __init__(self, dir):
        now = datetime.now().strftime('%Y-%m-%d_%H%M%S')
        self.name = '{:s}.json'.format(now)
        self.dir = '{:s}/{:s}'.format(dir, self.name)
        self.f = open(self.dir, 'w+')
    
    def write(self, msg):
        self.f.write(msg)

    def close(self):
        self.f.close()
        t = Thread(target=upload_to_s3, args=(self.name, self.dir))
        t.start()

if __name__ == '__main__':
    try:
        ws = DummyClient(url='ws://stream.meetup.com/2/rsvps')
        ws.connect()
        ws.run_forever()
    except KeyboardInterrupt:
        print("stopped")
        ws.close()
        time.sleep(200)