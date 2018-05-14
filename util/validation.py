import json
import sys
from os import listdir
from os.path import isfile, join

def validateUser(file):
    user = {}
    with open(file, 'r') as f:
        for line in f:
            try:
                dic = json.loads(line)
                # print(dic)
                if dic['member']['member_id']:
                    if dic['member']['member_id'] in user:
                        user[dic['member']['member_id']] += 1
                    else:
                        user[dic['member']['member_id']] = 1

            except Exception as e:
                # print(e)
                pass
    n = 1
    for k, v in user.items():
        print(n, k, v)
        n += 1
                

if __name__ == '__main__':
    print('Validate user: ', sys.argv[1])
    if len(sys.argv) != 2:
        raise Exception('incorrect args')
    validateUser(sys.argv[1])