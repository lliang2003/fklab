#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Apr 27 09:38:36 PM CST 2009

import heapq, time, sys, os, random, readline, atexit

def get_history():
    review.wordheap = []
    if not os.path.exists(dict.history_file):return {}
    hist = {}
    score = {}
    for line in open(dict.history_file):
        #print line
        type, word, timestamp = line.split()
        hist.setdefault(word, [])
        hist[word].append("%s at %s"%(type, timestamp))
        tspan = time.time() - time.mktime(time.strptime(timestamp, '%Y-%m-%d,%H:%M:%S'))
        score.setdefault(word, 1)
        if type == "lookup":
            score[word] += 3600*24/tspan
        elif type == "review":
            score[word] /= 2

    for word in score:
        review.wordheap.append((-score[word], word))
    heapq.heapify(review.wordheap)

    return hist

def review():
    if not review.wordheap:
        get_history()
        if not review.wordheap:
            print "No word in heap."
            return
    s, word = heapq.heappop(review.wordheap)
    for i in range(20): print
    print "%s score=%f"%(word, -s)
    print
    raw_input()
    ss = wordnet.synsets(word)
    timestamp = time.strftime('%Y-%m-%d,%H:%M:%S')
    open(dict.history_file, 'a').write("review %s\t%s\n"%(word, timestamp))
    for i in range(len(ss)):
        print '%d.[%s].%s'%(i, ss[i].pos, ss[i].definition)
        if len(ss[i].lemma_names) > 1:
            print '\t',' ~ '.join(ss[i].lemma_names)
        if ss[i].examples: 
            print 'Example:', random.choice(ss[i].examples)
    print 


def command(line):
    cmd = line.split()[0]
    if cmd.startswith(".h"):
        print get_history()
    elif cmd.startswith(".r"):
        review()
    elif cmd.startswith(".rr"):
        review.wordheap = []
    else:
        sys.exit(0)

def lookup(word, fullmode=False):
    if word == "":
        word = lookup.lastword
        fullmode = True
    ss = wordnet.synsets(word)
    if not ss: 
        print 'Unknown word.'
        print 
        return
    lookup.lastword = word
    timestamp = time.strftime('%Y-%m-%d,%H:%M:%S')
    open(dict.history_file, 'a').write("lookup %s\t%s\n"%(word, timestamp))
    for i in range(len(ss)):
        print '%d.[%s].%s'%(i, ss[i].pos, ss[i].definition)
        if fullmode:
            if len(ss[i].lemma_names) > 1:
                print '\t',' ~ '.join(ss[i].lemma_names)
            #print ss[i].hypernyms()
            #print ss[i].hyponyms()
            #print ss[i].similar_tos()
            #print ss[i].also_sees()
            #for example in ss[i].examples: print '\t', example
            if ss[i].examples: 
                print 'Example:', random.choice(ss[i].examples)
    print

def dict():
    sys.stdout = sys.stderr
    print 'init ...',
    #init nltk
    sys.path.append(os.path.expanduser("~/archive/nltk-0.9.8"))
    #os.environ['NLTK_DATA'] = os.path.expanduser("~/archive/nltk-0.9.8")
    os.environ['NLTK_DATA'] = os.path.expanduser("~/archive/nltk_data")
    dict.history_file = os.path.expanduser("~/fklab/proj/fkdict/dict.history")
    get_history()
    global wordnet
    from nltk.corpus import wordnet
    wordnet.synsets('wordnet')
    lookup.lastword = 'wordnet'
    print 'done'

    while True:
        #print '>',
        line = raw_input('>')
        if line and line[0] == ".":
            command(line)
            continue
        lookup('_'.join(line.split()))

if __name__ == "__main__":
    dict()

