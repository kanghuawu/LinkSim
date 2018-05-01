from ws4py.client.threadedclient import WebSocketClient
from datetime import datetime
from threading import Thread
import time
import boto3
import os
import json

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
        x = json.loads(str(m))
        x['now'] = round(time.time() * 1000)
        self.file.write(json.dumps(x) + "\n")
        self.counter += 1
        if self.counter > 0 and self.counter % self.max == 0:
            self.file.close()
            self.file = FileHandler('.')
            print(self.counter, 'messages and ready to close')
            self.counter = 0

        if self.counter > 0 and self.counter % 10000 == 0:
            print(self.counter, 'messages written to file')

class FileHandler:
    def __init__(self, dir):
        now = datetime.now().strftime('%Y-%m-%d_%H%M%S')
        self.name = '{:s}_v2.json'.format(now)
        self.dir = '{:s}/{:s}'.format(dir, self.name)
        self.f = open(self.dir, 'w+')
    
    def write(self, msg):
        self.f.write(msg)

    def close(self):
        self.f.close()
        t = Thread(target=upload_to_s3, args=(self.name, self.dir))
        t.start()

if __name__ == '__main__':
    sleep_time = 60
    retry = 0
    retry_max = 10
    while retry < retry_max:
        try:
            ws = DummyClient(url='ws://stream.meetup.com/2/rsvps')
            ws.connect()
            ws.run_forever()
        except KeyboardInterrupt:
            print("stopped by KeyboardInterrupt")
            ws.close()
            time.sleep(sleep_time)
            break
        print('Something went wrong retry')
        time.sleep(sleep_time)
        retry += 1
