#!/usr/bin/env python
import sys

g = 9
def ff():
    """function ff.

        Test basic function
        The first function"""

    a = 1
    print 'file:', sys._getframe().f_code.co_filename
    print 'func:', sys._getframe().f_code.co_name
    print 'stacksize:', sys._getframe().f_code.co_stacksize
    print 'consts:', sys._getframe().f_code.co_consts
    print 'argcount:', sys._getframe().f_code.co_argcount
    print 'varnames:', sys._getframe().f_code.co_varnames
    print 'first lineno:', sys._getframe().f_code.co_firstlineno
    print 'line:', sys._getframe().f_lineno
    print 'lasti:', sys._getframe().f_lasti
    print 'locals:', sys._getframe().f_locals
    print 'globals:', sys._getframe().f_globals
    print 'f_exc_type:', sys._getframe().f_exc_type
    print 'f_exc_traceback:', sys._getframe().f_exc_traceback
    b = ''
    print 'locals:', sys._getframe().f_locals
    print 'trace:', sys._getframe().f_trace
    return "a first function"
print ff()
print ff.__doc__
print ff.__name__

def test_param(a, b=4, c=5):
    print a, b, c 
test_param(4,3,5)
test_param(c=3, a=6)

print map(lambda x:x *2, [4])
print reduce(lambda x, y: x + y, [1, 2, 3], 4)
print filter(lambda x:x > 3, [5, 6, 4, 2, 7])
def function(a, b):
    print a, b
apply(function, ("crunchy", "frog"))
apply(function, (), {"b": "frog", "a": "crunchy"})
apply(function, (), {"a": "crunchy", "b": "frog"})

def counter(start_at=0):
     count = [start_at]
     def incr():
         count[0] += 1
         return count[0]
     return incr

print counter(4)
c = counter(5)
d = counter(66)
print c()
print c()
print d()
print c()


def info(object, spacing=10, collapse=1):   
    """Print methods and doc strings.
    
    Takes module, class, list, dictionary, or string."""
    methodList = [method for method in dir(object) if callable(getattr(object, method))]
    processFunc = collapse and (lambda s: " ".join(s.split())) or (lambda s: s)
    print "\n".join(["%s %s" %
              (method.ljust(spacing),
               processFunc(str(getattr(object, method).__doc__)))
             for method in methodList])
#print info(info)
#print callable(info)

def test_generator():
    print sum(1 for i in range(1000) for j in range(1000))
    for item in gen():
        print item

def gen():
    yield 1
    yield 2

#test_generator()
