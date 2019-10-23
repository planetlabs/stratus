"""Assumes Python 3.6+
"""
from __future__ import print_function
import argparse
import datetime
import time
import csv
import requests
import logging
import threading
import urllib.request
import json

# input: csv file showing how many users, how many minutes, one per line
# e.g., `10,120` = 10 users for 120 minutes

LOG = logging.getLogger("simulate_variable_usage")
csv_file=None

class StatsLogger(threading.Thread):

    def __init__(self,host,output_stats_file):
        super(StatsLogger, self).__init__()
        self.host=host
        self.output_stats_file=output_stats_file


    def run(self):
        print("Starting stats logger")
        while (True):
            stats=get_stats(self.host)
            print(stats)
            with open(self.output_stats_file,"a") as myfile:
                myfile.write(stats)
            time.sleep(1)

def argument_parser():
    parser = argparse.ArgumentParser(
        description="Run load test with variable amount of users specified by csv file of format (users, duration[min])",
    )
    parser.add_argument(
        "--host",
        default="localhost:8089",
    )
    parser.add_argument(
        "--output_stats_file",
        default="output_stats_file.csv",
    )
    parser.add_argument(
        "--csv-file",
        default="usage_load.csv",
    )
    return parser

# "Method","Name","# requests","# failures","Median response time","Average response time","Min response time","Max response time","Average Content Size","Requests/s"
def get_stats(host):
    req=urllib.request.urlopen("http://"+host+"/stats/requests/csv")
    stats=req.read()
    lines=stats.splitlines()
    req=urllib.request.urlopen("http://"+host+"/stats/requests")
    stats=req.read()
    threads=json.loads(stats)["user_count"]
    st = datetime.datetime.fromtimestamp(time.time()).strftime('%Y-%m-%d %H:%M:%S')
    line=st+","+str(lines[1])+","+str(threads)
    return line

def read_load_csv(csv_file): 
    return list(csv.reader(open(csv_file)))

def start_test(host,users):
    r=requests.post('http://'+host+'/swarm',data={'hatch_rate': 1, 'locust_count': users})
    print("Started test with ",users,":",r.status_code, r.reason)

def stop_test(host):
    r=requests.get('http://'+host+'/stop')
    print("Stopped test:",r.status_code, r.reason)

def run_test_static_users(host,users,duration_min):
    start_test(host,users)
    t_end=time.time()+60*float(duration_min)
    while True:
        if (time.time()>t_end):
            break
        time.sleep(1)
    stop_test(host)

def run_test_dynamic(host,output_stats_file,usage_load_table):
    stats_logger = StatsLogger(host,output_stats_file)
    stats_logger.start()
    for row in usage_load_table:
        users,duration_min=row[:2]
        print("Running test with",users,"threads for",duration_min,"minutes")
        run_test_static_users(host,users,duration_min)

def main():
    parser = argument_parser()
    options = parser.parse_args()
    usage_load_table=read_load_csv(options.csv_file)
    run_test_dynamic(options.host,options.output_stats_file,usage_load_table)

main()
