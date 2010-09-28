#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Sep 24 09:17:43 PM CST 2010

import random, Queue

class Task:
    def __init__(self, machine_count, backup_count):
        self.machines = random.sample(range(machine_count), backup_count)
        self.done = False

class Slot:
    def __init__(self, machine_id):
        self.machine = machine_id
        self.id = random.randint(0, 99)


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


def run():
    nm = 200
    nt = 800
    slots = []
    tasks = []
    squeue = Queue.Queue()
    for i in range(nt):
        tasks.append(Task(nm, 3))
    for i in range(nm*3):
        slots.append(Slot(i/3))
    random.shuffle(slots)
    for slot in slots:
        #print slot.machine
        squeue.put(slot)

    nlocal, nremote = 0, 0
    #for i in range(nt):
    while True:
        slot = squeue.get()
        task = find_task(tasks, slot.machine)
        if task == None: break
        #print task, task.done
        #print "%d local=%s"%(slot.id, slot.machine in task.machines)
        if slot.machine in task.machines:
            nlocal += 1
        else:
            nremote += 1
        squeue.put(slot)

    print nlocal, nremote


run()



