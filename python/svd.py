from numpy import matrix
from scipy import *
A=matrix([[1,0,1,0,0,0],[0,1,0,0,0,0],[1,1,0,0,0,0],[1,0,0,1,1,0],[0,0,0,1,0,1]])
# svd
T,s,D=linalg.svd(A)
#print T
#print D
#print 'SIGMA :',s

# simplify svd
TS=T[0:,0:2]
DS=D[0:2]
ss=matrix([[0.0,0],[0,0]])
ss[0,0]=s[0]
ss[1,1]=s[1]
print 'new SIGMA Matrix :'
print ss
print 'Simplified Term Matrix :'
print TS
print 'Simplified Doc Matrix :'
print DS

#query
q=matrix([1,0,1,1,0])
ssI=ss.I
qq=q*TS*ssI
print 'Query in LSI space :', qq
print 'Similarity with each doc :'
for i in range(0,6):
    v = DS.T[i]
    print inner(v,qq)/sqrt(inner(v,v))/sqrt(inner(qq,qq))
