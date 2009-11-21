#!/usr/bin/env python
# @author   FAN Kai, Peking University  
# @date     Dec 24 06:14:26 CST 2008

import os, time, sys

if len(sys.argv) < 2:
    print 'Usage: %s [taskfile]' % sys.argv[0]
    sys.exit(1)

task_file = sys.argv[1]
output_file = task_file + '.out'

while True:
    if not os.path.exists(task_file):
        os.mknod(task_file)
    lines = open(task_file).readlines()
    for i in range(len(lines)-1, 0, -1):
        if lines[i][:4] == 'Done':
            lines = lines[i+1:]
            break
    for line in lines:
        for task in line.split(';'):
            task = task.strip() + ' >>%s 2>&1 &' % output_file
            os.system(task)
            cmd = '''echo 'Done "%s"' at %s. >>%s''' % (task, time.asctime(), task_file)
            os.system(cmd)
    try:
        time.sleep(1)
    except:
        break
