#!/usr/bin/env python
# @author   FAN Kai, Peking University  
# @date     Dec 22 12:22:58 CST 2008

import re, os, time, shelve, sys, ConfigParser

def filter(cmd):
    ''' Filter unwanted commands. '''
    for pat in filter.patterns:
        if re.search(pat, cmd):
            return False
    return True

def sample_sys_status():
    ''' Sample current status of the global system. '''
    try:
        stats = {}
        for line in open('/proc/meminfo'):
            if ':' not in line: continue
            words = line.split(':')
            stats[words[0]] =  words[1].split()[0]

        for line in open('/proc/net/dev').readlines()[2:]:
            if ':' not in line: continue
            face = line.split(':')[0].strip()
            words = line.split(':')[1].split()
            stats[face + ':RB'] = words[0]
            stats[face + ':RP'] = words[1]
            stats[face + ':TB'] = words[8]
            stats[face + ':TP'] = words[9]

        for line in open('/proc/diskstats'):
            words = line.split()
            if len(words) < 11: continue
            stats[words[2] + ':R'] = words[3]
            stats[words[2] + ':RS'] = words[5]
            stats[words[2] + ':RT'] = words[6]
            stats[words[2] + ':W'] = words[7]
            stats[words[2] + ':WS'] = words[9]
            stats[words[2] + ':WT'] = words[10]

        for line in open('/proc/stat'):
            words = line.split()
            if len(words) < 2: continue
            stats[words[0]] = words[1]
        stats['PR'] = stats['procs_running']

        stats['loadavg'] = open('/proc/loadavg').read().split()[0]

        sys_status = []
        for f in sys_status_fields:
            name, w = parse_field(f)
            if name in stats:
                sys_status.append(stats[name])
            else:
                sys_status.append('null')
        return sys_status
    except IOError:
        return [0]*len(sys_status_fields)

def sample_proc_status(pdir):
    ''' Sample current status of process. '''
    stats = {}
    for line in open(pdir + '/status'):
        key = line.split(':')[0]
        value = line.split(':')[1].split()
        if value: stats[key] = value[0]

    stats['threads'] = ' '.join(os.listdir(pdir + '/task'))
    stats['Command'] = open(pdir + '/cmdline').read()

    proc_status = []
    for f in proc_status_fields:
        name, w = parse_field(f)
        if name in stats:
            proc_status.append(stats[name])
        else:
            proc_status.append('null')
    return proc_status

def make_sample():
    ''' Sample current status of system. 
    Sample format: 
        [sys_status, proc1_info, proc2_info, ...] '''
    sample = [sample_sys_status()]
    for pdir in os.listdir('/proc'):
        if not re.match('\d+', pdir): continue
        cmd = open('/proc/' + pdir + '/cmdline').read()
        if filter(cmd): continue
        pinfo = sample_proc_status('/proc/' + pdir)
        sample.append(pinfo)
    return sample

def monitor():
    ''' Monitor the system. '''
    while True:
        conf.read(conf_files)
        filter.patterns = conf.get('monitor', 'patterns').split(',')
        sample_interval = conf.getfloat('monitor', 'sample_interval')
        sampledb = shelve.open(dbfile)
        sampledb[str(time.time())] = make_sample()
        sampledb.close()
        time.sleep(60 * sample_interval)

def get_samples(start, num, interval):
    ''' Get samples from db. '''
    sampledb = shelve.open(dbfile)
    keys = sampledb.keys()
    keys.sort()
    times = []
    samples = []
    print 'Got sample indexes:',
    for i in range(start, start + num * interval, interval):
        if i >= len(keys) or i < -len(keys): continue
        print i,
        times.append(keys[i])
        samples.append(sampledb[keys[i]])
    print
    sampledb.close()
    return times, samples

def parse_field(field):
    name = field.split('#')[0].strip()
    if len(field.split('#')) > 1:
        width = int(field.split('#')[1])
    else:
        width = len(name)
    return name, width

def format_title(fields):
    ''' Generate table title. '''
    parts = []
    for field in fields:
        name, width = parse_field(field)
        parts.append(name.ljust(width))
    return 'Time  |' + '|'.join(parts)

def format_status(fields, t, info):
    ''' Generate table row. '''
    parts = []
    timestr = time.strftime('%H:%M |', time.localtime(float(t))) 
    for i in range(len(fields)):
        name, width = parse_field(fields[i])
        parts.append(info[i].ljust(width))
    return timestr + '|'.join(parts)

examples = '''Examples: 
    monitor.py -m -c ~/monitor.ini
        Start monitoring with config in ~/monitor.ini.
    monitor.py -l -c ~/monitor.ini
        Show sample lists with config in ~/monitor.ini.
    monitor.py -s 100
        Show the 100th sample.
    monitor.py -g -s -100 -n 100
        Show global system information of the latest 100 samples.
    monitor.py -p 12345 -s 10 -n 10 -i 10
        Show information for process 12345 in sample 10, 20, ..., 100. 
        '''.replace('monitor.py', os.path.basename(sys.argv[0]))

import optparse
parser = optparse.OptionParser(description='Monitor the system or display monitored information.')
parser.add_option('-e', '--example', action='store_true', default=False,
        help='show usage examples')
parser.add_option('-m', '--deamon', action='store_true', default=False, 
        help='start monitoring deamon')
parser.add_option('-c', '--conf', metavar='FILE', default='monitor.ini',
        help='speicify configuration file')
parser.add_option('-l', '--list', action='store_true', default=False,
        help='list all monitored samples')
parser.add_option('-s', '--start', type='int', default=-1,
        help='first sample to process')
parser.add_option('-n', '--count', type='int', default=1,
        help='samples count')
parser.add_option('-i', '--interval', type='int', default=-1,
        help='sample interval')
parser.add_option('-p', '--pid', type='int', default='-1',
        help='only display information of process with this pid')
parser.add_option('-g', '--globalonly', action='store_true', default=False,
        help='only display global system information')
(opts, args) = parser.parse_args()
if opts.example:
    print examples
    sys.exit(0)

# Load configuration.
conf = ConfigParser.ConfigParser()
conf_files=[os.path.expanduser('~/.monitor.ini'), 'monitor.ini', opts.conf]
conf.read(conf_files)
dbfile = os.path.expanduser(conf.get('monitor', 'dbfile'))
sys_status_fields = conf.get('monitor', 'sys_status_fields').split(',')
proc_status_fields = conf.get('monitor', 'proc_status_fields').split(',')

# Begin monitoring.
if opts.deamon:
    monitor()

# List samples.
if opts.list:
    keys = shelve.open(dbfile).keys()
    keys.sort()
    interval = len(keys)>60 and 60 or 1
    for i in range(0, len(keys), interval):
        print 'sample %d:\t' % i, time.asctime(time.localtime(float(keys[i])))
    sys.exit(0)

# Get samples from sampledb.
times, samples = get_samples(opts.start, opts.count, opts.interval)
if len(times) == 0: sys.exit(1)
    
# Display system information.
if opts.pid == -1:
    print 'System Info(memory stats in KB):'
    print format_title(sys_status_fields)
    for t, sample in zip(times, samples):
        print format_status(sys_status_fields, t, sample[0])
    print
if not opts.globalonly:
    print 'Processes(memory stats in KB):'
    print format_title(proc_status_fields) 
    for t, sample in zip(times, samples):
        for i in range(1, len(sample)):
            pinfo = sample[i]
            if opts.pid != -1 and pinfo[0] != opts.pid: continue
            row = format_status(proc_status_fields, t, pinfo)
            print row
