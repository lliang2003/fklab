#!/usr/bin/env python
# @author   FAN Kai (fankai@pku.edu.cn), Peking University  
# @date     Dec 25 06:57:37 CST 2008

import re, shelve, time, sys

log_pattern = '(?P<time>[\d-]+ ..:..:..) \w+ - (?P<src>\w+): (?P<event>\w+) (?P<stats>.+=.+)'

fields = 'type id start end complete abandon job task transid addr retry'.split()

def parse_stats(line):
    entry = {}
    for stat in line.split(' '):
        if '=' not in stat: continue
        k = stat.split('=')[0]
        v = stat.split('=')[1]
        entry[k] = v
    return entry

def proc(logfile='worker1/mapred_worker.log'):
    for line in open(logfile).read().split('\n'):
        mp = re.match(log_pattern, line)
        if not mp: continue
        ts = mp.group('time')
        #tt = time.mktime(time.strptime(t, "%Y-%m-%d %H:%M:%S"))
        src = mp.group('src')
        event = mp.group('event')
        stats = parse_stats(mp.group('stats'))

        if event not in events:
            events.add(event)

        taskid = stats.get('task', None)
        jobid = stats.get('job', None)
        type = stats.get('type', None)
        id = stats.get('id', None)
        id = stats.get('id', None)
        if event == 'GetTask':
            task = stats
            task['start'] = ts
            tasks[taskid] = task
        elif event == 'AbandonTask':
            task = tasks[taskid]
            task['abandon'] = ts
            task['retry'] = stats['retry']
        elif event == 'CompleteTask':
            task = tasks[taskid]
            task['complete'] = ts
        elif event == 'StartThread':
            thread = stats
            thread['start'] = ts
            threads[id] = thread
        elif event == 'FinishThread':
            thread = threads[id]
            thread['complete'] = ts

def display(db):
    for item in db.values(): 
        for f in fields:
            if f in item.keys():
                print '%s=%s\t' % (f, item[f]), 
        print

tasks = {}
threads = {}
events = set()
def proc_dir(dir):
    global tasks, threads, events
    tasks = {}
    threads = {}
    events = set()
    proc(dir + '/mapred_worker.log')
    print 'Dir: ' + dir
    print 'Tasks:'
    display(tasks)
    print 'Threads:'
    display(threads)
    print


if len(sys.argv) > 1:
    proc_dir(sys.argv[1])
else:
    proc_dir('worker1')
    proc_dir('worker2')
    proc_dir('worker3')



