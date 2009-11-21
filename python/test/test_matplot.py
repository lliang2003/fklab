#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 27 17:48:52 CST 2008

from pylab import *

subplot(221)
plot([0.5,1,2,3], [0.25,1,4,9])
t = arange(0,5,.2)
plot(t,t,'r--',t,t**2,'bs')
axis([0,4,0,20])

subplot(222)
plot([4,5,6])

subplot(223)
t = arange(0,3,.1)
plot(t,t,'r--',t,t**2,'bs',t,t**3)
xlabel('xxx')
ylabel('yyy')
title(r'Expo')
text(1.2,5,r'$y=x^3$')
annotate(r'$y=x^2$', xy=(2,4), xytext=(2.5,4.1), 
        arrowprops=dict(facecolor='black', shrink=0.01),)
grid(True)
axis([0,3,0,10])

subplot(224)
title('TEXT')
text(1,1,r'fuck the world',
        horizontalalignment='center',
        verticalalignment='center',
        fontsize = 22,
        bbox={'facecolor':'red','alpha':0.5})
axis([0,2,0,2])
show()
#savefig('a.png')
