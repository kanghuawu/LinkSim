import json
import sys

def search():
    with open('./data/2018-03-29_061608.json', 'r') as f:
        for line in f:
            dic = json.loads(line)
            if dic['event'] and dic['event']['event_id'] == '248239522':
                print(dic['member']['member_id'], dic['member']['member_name'])

if __name__ == '__main__':
    
    search()