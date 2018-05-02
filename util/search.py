import json
import sys
from os import listdir
from os.path import isfile, join

def searchByMemberName(input_dir, value):
    input_files = [join(input_dir, f) for f in listdir(input_dir) if isfile(join(input_dir, f))]
    # print(input_files)
    for file in input_files:
        with open(file, 'r') as f:
            for line in f:
                try:
                    dic = json.loads(line)
                    if dic['event'] and dic['member']['member_name'] == value:
                        print(file, dic['event']['event_name'], dic['member']['member_name'])
                except Exception as e:
                    # print('Decoding JSON has failed')
                    pass
                

if __name__ == '__main__':
    if len(sys.argv) != 3:
        raise Exception('incorrect args')
    searchByMemberName(sys.argv[1], sys.argv[2])