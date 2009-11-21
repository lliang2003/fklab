/**
 * @file	KeepAliveDeamon.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008��06��28�� 16ʱ09��19�� CST
 *
 */

#include "KeepAliveDeamon.hpp"
using namespace std;
using namespace debby;

void KeepAliveDeamon::run() {
    IceUtil::Monitor<IceUtil::Mutex>::Lock lock(_monitor);
    slice::Event event;
    event.type = slice::ENothing;
    while (!_done) {
        _pdm->replyTimeoutKA();
        _pdm->removeExpiredSessions();

        _monitor.timedWait(IceUtil::Time::seconds(1));
    }
}

void KeepAliveDeamon::stop() {
    IceUtil::Monitor<IceUtil::Mutex>::Lock lock(_monitor);

    _done = true;
    _monitor.notify();
}
