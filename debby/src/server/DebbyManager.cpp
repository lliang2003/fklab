/**
 * @file	DebbyManager.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年07月01日 08时57分54秒 CST
 *
 */

#include "DebbyManager.hpp"
#include "ServerLogger.hpp"
using namespace debby;
using namespace std;

DebbyManager::DebbyManager() {
    srand(time(0) + clock());
}

bool DebbyManager::create(const string &path, int handle, bool tmp) {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("DebbyManager: create " <<path);
    if (_data.exists(path)) return false;
    // invoke events
    for (int i = 0; i < path.size(); ++i) 
        if (path[i] == '/') 
            invoke(makeEvent(slice::EDirChanged, path.substr(0, i)));
    invoke(makeEvent(slice::ECreated, path));
    // write data
    if (tmp) _data.create(path, handle);
    else _data.create(path);
    return true;
}

void DebbyManager::remove(const string &path) {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("DebbyManager: remove " <<path);
    if (!_data.exists(path)) return;
    // invoke events
    for (int i = 0; i < path.size(); ++i) 
        if (path[i] == '/') 
            invoke(makeEvent(slice::EDirChanged, path.substr(0, i)));
    invoke(makeEvent(slice::ERemoved, path));
    // write data
    _data.remove(path);
}

bool DebbyManager::exists(const string &path) {
    LOG_INFO("DebbyManager: exists " <<path);
    IceUtil::RecMutex::Lock lock(_mutex);
    return _data.exists(path);
}

vector<string> DebbyManager::list(const string &path) {
    LOG_INFO("DebbyManager: list " <<path);
    IceUtil::RecMutex::Lock lock(_mutex);
    return _data.list(path);
}

string DebbyManager::read(const string &path) {
    LOG_INFO("DebbyManager: read " <<path);
    IceUtil::RecMutex::Lock lock(_mutex);
    if(!_data.exists(path)) throw slice::FileNotExist();
    return _data.read(path);
}

void DebbyManager::write(const string &path, const string &data) {
    LOG_INFO("DebbyManager: read " <<path);
    IceUtil::RecMutex::Lock lock(_mutex);
    if(!_data.exists(path)) throw slice::FileNotExist();
    // invoke events
    invoke(makeEvent(slice::EChanged, path));
    // write data
    _data.write(path, data);
}

int DebbyManager::addSession(const string &name) {
    LOG_INFO("DebbyManager: addSession " <<name);
    IceUtil::RecMutex::Lock lock(_mutex);
    int handle;
    do { 
        handle = rand();
    } while(_data.existsSession(handle));

    _data.addSession(name, handle);
    _sessionUpdateTime[handle] = time(0);
    return handle;
}

bool DebbyManager::existsSession(int handle) {
    //LOG_INFO("DebbyManager: existsSession " <<handle);
    IceUtil::RecMutex::Lock lock(_mutex);
    return _data.existsSession(handle);
}

void DebbyManager::updateSession(int handle, int t) {
    LOG_INFO("DebbyManager: updateSession " <<handle <<" at " <<t);
    assert(existsSession(handle));
    IceUtil::RecMutex::Lock lock(_mutex);
    _sessionUpdateTime[handle] = t;
}

void DebbyManager::removeSession(int handle) {
    LOG_INFO("DebbyManager: removeSession " <<handle);
    assert(existsSession(handle));
    IceUtil::RecMutex::Lock lock(_mutex);
    // Remove cbs, update time, events, then session data
    if (_cbs.find(handle) != _cbs.end()) {
        LOG_DEBUG("DebbyManager: [" <<handle <<"] reply no event.");
        _cbs.find(handle)->second->ice_response(makeEvent(slice::ENothing, ""));
        _cbs.erase(handle);
    }
    _sessionUpdateTime.erase(handle);
    for (set<slice::Event>::iterator it = _sessionEvents[handle].begin();
            it != _sessionEvents[handle].end(); ++it)
        _events[*it].erase(handle);
    _sessionEvents.erase(handle);
    _data.removeSession(handle);
}

void DebbyManager::removeExpiredSessions(int et) {
    IceUtil::RecMutex::Lock lock(_mutex);
    for (map<int, int>::iterator it = _sessionUpdateTime.begin();
            it != _sessionUpdateTime.end(); ) {
        if (time(0) - it->second > et) {
            LOG_INFO("DebbyManager: removeExpiredSession.");
            removeSession(it++->first);
        } else ++it;
    }
}

void DebbyManager::regEvent(int handle, const slice::Event &e) {
    LOG_INFO("DebbyManager: regEvent on " <<e.path);
    assert(existsSession(handle));
    IceUtil::RecMutex::Lock lock(_mutex);
    _events[e].insert(handle);
    _sessionEvents[handle].insert(e);
}

void DebbyManager::unregEvent(int handle, const slice::Event &e) {
    LOG_INFO("DebbyManager: unregEvent on " <<e.path);
    assert(existsSession(handle));
    IceUtil::RecMutex::Lock lock(_mutex);
    _events[e].erase(handle);
    if (_events[e].empty())
        _events.erase(e);
}

void DebbyManager::addKA(int handle, const slice::AMD_Session_keepAlivePtr& cb) {
    LOG_DEBUG("DebbyManager: add keepAlive for session " <<handle);
    assert(existsSession(handle));
    IceUtil::RecMutex::Lock lock(_mutex);
    updateSession(handle);
    if (_data.hasEvent(handle)) {
        LOG_DEBUG("DebbyManager: [" <<handle <<"] invoke event.");
        cb->ice_response(_data.getEvent(handle));
    } else {
        if (_cbs.find(handle) != _cbs.end()) throw slice::DuplicateKeepAlive();
        _cbs[handle] = cb;
    }
}

void DebbyManager::replyTimeoutKA(int timeout) {
    IceUtil::RecMutex::Lock lock(_mutex);
    slice::Event e;
    e.type = slice::ENothing;
    for (map<int, slice::AMD_Session_keepAlivePtr>::iterator it = _cbs.begin(); 
            it != _cbs.end();) {
        if (time(0) - _sessionUpdateTime[it->first] > timeout) {
            LOG_DEBUG("DebbyManager: [" <<it->first <<"] reply no event.");
            it->second->ice_response(e);
            _cbs.erase(it++);
        } else ++it;
    }
}

void DebbyManager::invoke(const slice::Event &e) {
    IceUtil::RecMutex::Lock lock(_mutex);
    set<int> &handles = _events[e];
    for(set<int>::iterator it = handles.begin(); it != handles.end(); ++it) {
        if (_cbs.find(*it) == _cbs.end()) {
            LOG_DEBUG("DebbyManager: [" <<*it <<"] add ready event.");
            _data.addEvent(*it, e);
        } else {
            LOG_DEBUG("DebbyManager: [" <<*it <<"] invoke event.");
            _cbs[*it]->ice_response(e);
            _cbs.erase(*it);
        }
    }
}

slice::Event DebbyManager::makeEvent(slice::EventType t, const std::string &path = "") {
    slice::Event e;
    e.type = t;
    e.path = path;
    return e;
}
