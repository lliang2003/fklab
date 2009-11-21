/**
 * @file	ClientDeamon.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年05月17日 09时58分06秒 CST
 *
 */

#include "ClientDeamon.hpp"
#include "EventCallback.hpp"
#include <Session.h>

using namespace debby;
using namespace std;
using namespace boost;

#undef LOG_PREFIX
#define LOG_PREFIX "[" <<_handle <<"] ClientDeamon: "

ClientDeamon::ClientDeamon(SessionService *pss, int handle):
    _pss(pss), _handle(handle), _started(false) { }

void ClientDeamon::invoke(const slice::Event &e) {
    map<slice::Event, Callbacks>::iterator it = _cbs.find(e);
    if (it == _cbs.end()) return;
    LOG_DEBUG("invoke " <<it->second.size() <<" callbacks" );
    IceUtil::Mutex::Lock lock(_mutex);
    for (unsigned j = 0; j < it->second.size(); ++j) 
        _threads.push_back((new EventCallback(it->second[j]))->start());
}

void ClientDeamon::run() {
    {
        IceUtil::Mutex::Lock lock(_mutex);
        if (_started) {
            LOG_ERROR("Restart.");
            return;
        }
        _started = true;
    }
    LOG_DEBUG("Started.");
    char *eventNames[] = {"ECreated", "ERemoved", "EChanged", "EDirChanged"};
    while (true) {
        try {
            slice::Event event = _pss->service()->keepAlive(_handle);
            //cout <<getpid() <<" : Send heartbeat" <<endl;
            switch (event.type) {
                case slice::ECreated:
                case slice::ERemoved:
                case slice::EChanged:
                case slice::EDirChanged:
                    {
                        LOG_DEBUG("Get " <<eventNames[event.type] 
                                <<" event on path " <<event.path);
                        invoke(event);
                        break;
                    }
                case slice::EElected: 
                    {
                        // Resend all events;
                        LOG_DEBUG("Reregister all events.");
                        IceUtil::Mutex::Lock lock(_mutex);
                        for (map<slice::Event, Callbacks>::iterator it = _cbs.begin();
                                it != _cbs.end(); ++it)
                            _pss->service()->regEvent(_handle, event);
                        break;
                    }
                case slice::ENothing:
                    LOG_DEBUG("Get empty event.");
                    break;
            }
        }
        _CATCH(slice::UnknownSession, "", break)
        _CATCH(Ice::Exception, ex.ice_name(), break)
        _CATCH(std::exception, ex.what(), break)
    }

    // Wait for all threads to terminate
    LOG_DEBUG("Join " <<_threads.size() <<" threads.");
    for (int i = 0; i < _threads.size(); ++i)
        _threads[i].join();
    _threads.clear();
    LOG_DEBUG("Terminated.");
}

void ClientDeamon::regcb(const string &path, EventType t, shared_ptr<Callback> cb) {
    LOG_TRACE("Register callback on " <<path);
    IceUtil::Mutex::Lock lock(_mutex);

    slice::Event event;
    if (t == ELockReleased) {
        event.type = slice::ERemoved;
        event.path = "lock" + path;
    } else {
        event.type = (slice::EventType)t;
        event.path = path;
    }
    if (_cbs.find(event) == _cbs.end()) 
        _pss->service()->regEvent(_handle, event);
    _cbs[event].push_back(cb);
}

void ClientDeamon::clearcb(const string &path, EventType t) {
    LOG_TRACE("Clear callback on " <<path);
    IceUtil::Mutex::Lock lock(_mutex);

    slice::Event event;
    if (t == ELockReleased) {
        event.type = slice::ERemoved;
        event.path = "lock" + path;
    } else {
        event.type = (slice::EventType)t;
        event.path = path;
    }
    if (_cbs.find(event) != _cbs.end()) {
        _pss->service()->unregEvent(_handle, event);
        _cbs.erase(event);
    }
}
