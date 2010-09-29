#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Sep 24 09:17:43 PM CST 2010

import random, Queue, heapq
import time

class Task:
    def __init__(self, machine_count, backup_count):
        self.machines = random.sample(range(machine_count), backup_count)
        self.done = False

class Slot:
    def __init__(self, machine_id):
        self.machine = machine_id
        self.id = random.randint(0, 99)
        self.free_time = 0

    def __cmp__(self, other):
        return self.free_time - other.free_time


def find_task(tasks, machine):
    for task in tasks:
        if not task.done and machine in task.machines:
            #print task, task.done
            task.done = True
            return task
    for task in tasks:
        if not task.done:
            #print task, task.done
            task.done = True
            return task
    return None


def run(nm, nt):
    slots = []
    tasks = []
    for i in range(nt):
        tasks.append(Task(nm, 3))
    for i in range(nm*3):
        slots.append(Slot(i/3))
    random.shuffle(slots)

    nlocal, nremote = 0, 0
    #for i in range(nt):
    while True:
        slot = heapq.heappop(slots)
        task = find_task(tasks, slot.machine)
        if task == None: break
        #print task, task.done
        #print "%d local=%s"%(slot.id, slot.machine in task.machines)
        if slot.machine in task.machines:
            nlocal += 1
        else:
            nremote += 1
        slot.free_time = time.time() +  random.normalvariate(10, 5)
        heapq.heappush(slots, slot)
        #print slot.id, slot.free_time-time.time()

    #print nlocal, nremote
    return nremote


for nm in [200, 400, 800]:
    for nt in range(nm/2, nm*10, nm/2):
        nremote = 0
        for i in range(5):
            nremote += run(nm, nt)
        nremote /= 5
        #print nm, nt, "\t", nremote, nt-nremote
        print nm, nt, "\t", nremote*100.0/nt



