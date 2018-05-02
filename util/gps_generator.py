import sys
from os import listdir
from os.path import isfile, join
import json
from datetime import datetime
import time

def gps_generator(input_dir, output_file):
    input_files = [f for f in listdir(input_dir) if isfile(join(input_dir, f))]
    with open(output_file, 'w') as output_f:
        output = output_f
        for file in input_files:
            with open(file, 'r') as input_f:
                for line in input_f:
                    try:
                        dic = json.loads(line)
                        if dic.get('member') and dic.get('venue'):
                            output.write('{},{},{}\n'.format(
                                int(dic['member']['member_id']), 
                                float(dic['venue']['lon']), 
                                float(dic['venue']['lat'])))
                    except Exception as e:
                        print('Decoding JSON has failed')
                        pass
                    

if __name__ == '__main__':
    if len(sys.argv) != 3:
        raise Exception('incorrect args')
    gps_generator(sys.argv[1], sys.argv[2])