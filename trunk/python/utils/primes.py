#!/usr/bin/env python

import math
import timeit

def prime1(m):
    num = 1
    p = [2]
    i = 3
    while True:
        flag = True
        k = int(math.sqrt(i)) + 1
        for j in p:
            if i % j == 0:
                flag = False
                break
            if j*j>i:break
        if flag:
            num += 1
            p.append(i)
        i += 1
        if i > m:break
    return p

def prime2(m):
    pp = range(2, m+1)
    p = []
    while pp[0] ** 2 <= m:
        p.append(pp[0])
        pp = [e for e in pp if e % pp[0] != 0]
    p.extend(pp)
    return p

def prime3(m):
    p = range(2, m+1)
    for i in range(0, m):
        p = [e for e in p if (e % p[i] != 0 or e == p[i])]
        if (p[i] ** 2 > m): break
    return p

def prime4(m):
    p = range(2, m+1)
    for i in range(0, m):
        p = filter(lambda x: x % p[i] != 0 or x <= p[i] , p)
        if (p[i] ** 2 > m): break
    return p


if __name__ == '__main__':
    print prime1(121)
    print prime2(121)
    print prime3(121)
    print prime4(121)
    
    t = timeit.Timer("primes.prime1(10000)", "import primes")
    print t.repeat(3, 10)
    t = timeit.Timer("primes.prime2(10000)", "import primes")
    print t.repeat(3, 10)
    t = timeit.Timer("primes.prime3(10000)", "import primes")
    print t.repeat(3, 10)
    t = timeit.Timer("primes.prime4(10000)", "import primes")
    print t.repeat(3, 10)


    
