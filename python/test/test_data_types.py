#!/usr/bin/env python
# @author   FAN Kai (fankai@pku.edu.cn), Peking University  
# @date     Dec 25 09:15:15 CST 2008

import sys, collections, heapq, array

def test_dict():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    d = {"user":"fk", "key":"joke", 6:9.9}
    print d, d["user"], d["key"], d[6]
    print d.items(), d.keys(), d.values()
    print d.has_key("fk"), d.has_key("user"), d.get("user")
    print d.__class__
    d["user"] = "fankai"
    d["lab"] = "cnds"
    print d

def test_list():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    li = ["a", 9, "fuck", 8.7]
    print li, li[1], li[-1]
    print li[0:-1], li[2:len(li)], li[:1]
    li.append("world")
    print li[3:]
    li.insert(1, "the")
    print li[:3]
    li.extend(['o', 'p', 'q'])
    print li
    li.append(['r', 's', 't'])
    print li
    print li.index('a'), 'b' in li
    while len(li) > 5:print li.pop()
    print li
    li.remove(9)
    print "List after remove 9:",li
    li += [1, 2] * 2 + [5, 6] 
    print li
    print [elem*2 for elem in li]
    li.sort()
    print li

    print [(x, y) for x in range(3) for y in range(3)]

def test_tuple():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    t = ('t', 'u', 5, 'e')
    print t, t[2:], t[-1]
    print tuple(li)

def test_misc():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    print "%s=%s" % ('a', 'b')
    print "%d > %s" % (9, '8')
    print "%+.2f" % 1.5
    print ';'.join(['1', '2', '3'])
    print '2, 4, 5'.split(',', 1)
    print "a".ljust(5), "b"
    print 'a' and 1
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    print type(1), type('a'), type(()), type([]), type({})
    print dir(())
    print dir([])
    print dir({})
    print dir.__doc__
    print ().__doc__
    print 5<3 and 'yes' or 'no'
    print 1 in (1, 2)
    print 1 in {1:2}
    print 2 in [1, 2]

def test_deque():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    dq = collections.deque([1,2,3])
    print dq
    dq.append(56)
    dq.appendleft(88)
    dq.extend([-1,-2])
    print dq
    print dq.pop()
    print dq.pop()
    print dq.popleft()
    print dq
    dq.rotate(3)
    print dq

def test_heapq():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    h = [9,8,3,4,2,7]
    print heapq.nlargest(3, h)
    print heapq.nsmallest(3, h)
    print h
    heapq.heapify(h)
    print h
    heapq.heappush(h, -2)
    print h
    print heapq.heappop(h)
    print h
    print heapq.heapreplace(h, 99)
    print h

def test_array():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    a = array.array('i')
    a.append(4)
    a.append(0)
    a.append(-1)
    print a
    print a.itemsize
    print a.itemsize
    print a.buffer_info()
    print a.count(3)
    print a.count(4)
    print array.array('c', 'hello world')
    print array.array('d', [1.0, 3, -9])

def test_queue():
    import Queue
    q = Queue.Queue()
    print q.qsize()
    q.put(1)
    print q.get() 
    try:
        print q.get(False) 
    except Queue.Empty, e:
        print 'empty'
    #print q.task_done()
    #q.join()

test_queue()
#test_tuple()
#test_misc()
#test_list()
#test_deque()
#test_heapq()
#test_array()

from bisect import *
def test_bisect():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    a = [1,3,5,5,7,9]
    print bisect_left(a, 4)
    print bisect_left(a, 5)
    print bisect_right(a, 5)
    print bisect(a, 5)
    insort(a, 4)
    print a
test_bisect()

