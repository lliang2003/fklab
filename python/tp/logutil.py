#!/usr/bin/env python
# @author   FAN Kai, Peking University  
# @date     Dec 22 06:31:00 CST 2008


#task {'taskid':taskid, 'ip':ip, 'pid':pid, 'tid':tid, 'ts':ts, 'te':te, 'cid':chunkID,
#       'jobid':jobid, 'part':partition}
#job {taskid:task, ...}

import re, shelve

head_pat = '[\d-]+ (?P<time>..:..:..) \w+ - (?P<src>\w+\((?P<id>\d+)\):) '
jobid_pat = 'jobId = (?P<jobid>\d+)'
taskid_pat = 'taskId = (?P<taskid>\d+)'

def get_time(line):
    mp = re.match('200.-..-.. (?P<h>..):(?P<m>..):(?P<s>..)', line)
    if mp:
        h = int(mp.group('h'))
        m = int(mp.group('m'))
        s = int(mp.group('s'))
        #print h, m, s, h*3600+m*60+s
        return h*3600+m*60+s

def match_get_task(line):
    pat = '%sGet (?P<type>\w+) task, %s %s' % (head_pat, jobid_pat, taskid_pat)
    mp = re.search(pat, line)
    if not mp: return 
    id = mp.group('taskid')
    taskdb.setdefault(id, {})
    taskdb[id]['taskid'] = mp.group('taskid')
    taskdb[id]['jobid'] = mp.group('jobid')
    taskdb[id]['type'] = mp.group('type')
    taskdb[id]['start_time'] = mp.group('time')

def match_complete_task(line):
    pat = '%sComplete task, %s %s' % (head_pat, jobid_pat, taskid_pat)
    mp = re.search(pat, line)
    if not mp: return 
    id = mp.group('taskid')
    taskdb.setdefault(id, {})
    taskdb[id]['taskid'] = mp.group('taskid')
    taskdb[id]['jobid'] = mp.group('jobid')
    taskdb[id]['comp_time'] = mp.group('time')

def match_trans_begin(line):
    pat = head_pat + 'Transfer (?P<file>[/\w]+) to (?P<addr>[\d\.:]+) sockfd = (?P<fd>\d+)' 
    #pat = head_pat + 'Transfer (?P<file>[/\w]+) to (?P<addr>[\d\.:]+) sockfd = (?P<fd>\d+) id = (?P<id>\d+)' 
    mp = re.search(pat, line)
    if not mp: return 
    print id, line
    id = mp.group('fd')
    transdb[id] = {}
    transdb[id]['file'] = mp.group('file')
    transdb[id]['fd'] = mp.group('fd')
    transdb[id]['addr'] = mp.group('addr')
    transdb[id]['start_time'] = mp.group('time')

def match_trans_end(line):
    pat = head_pat + 'Transfer (?P<size>\d+) bytes for (?P<file>[/\w]+) in (?P<cost_time>\d+) seconds.'
    mp = re.search(pat, line)
    if not mp: return 
    id = mp.group('id')
    print id, line
    transdb.setdefault(id, {})
    transdb[id]['size'] = mp.group('size')
    transdb[id]['cost_time'] = mp.group('cost_time')
    transdb[id]['comp_time'] = mp.group('time')
    print mp.group('cost_time')

def match_thread_start(line):
    pat = head_pat +'Start thread (?P<tid>\d+) for (?P<type>\w+) (?P<id>\d+)'
    mp = re.search(pat, line)
    if not mp: return 
    tid = m.group('tid')
    assert tid not in threaddb
    threaddb[tid] = {}
    threaddb[tid]['type'] = m.group('type')
    threaddb[tid]['id'] = m.group('id')

taskdb = shelve.open('taskdb', flag='n', writeback=True)
transdb = shelve.open('transdb', flag='n', writeback=True)
threaddb = shelve.open('threaddb', flag='n', writeback=True)
for line in open('log').read().split('\n'):
    match_get_task(line)
    match_complete_task(line)
    match_trans_begin(line)
    match_trans_end(line)

print len(transdb)
print len(taskdb)


