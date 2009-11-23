/**
 * @file	SessionImpl.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年06月28日 11时40分37秒 CST
 *
 */

#include "SessionImpl.hpp"
#include "ServerLogger.hpp"
using namespace debby;
using namespace std;

#undef LOG_PREFIX
#define LOG_PREFIX "[" <<handle <<"] SessionImpl: "

SessionImpl::SessionImpl(DebbyManager *pdm):_pdm(pdm) {}
SessionImpl::~SessionImpl() {}

int SessionImpl::connect(const string &name, const ::Ice::Current&){
    int handle = _pdm->addSession(name);
    LOG_INFO("New session " <<name <<" with handle " <<handle);
    return handle;
}

void SessionImpl::close(int handle, const ::Ice::Current&){
    LOG_INFO("Close session.");
    if (!_pdm->existsSession(handle)) return;
    _pdm->removeSession(handle);
}

void SessionImpl::keepAlive_async(const slice::AMD_Session_keepAlivePtr &cb, 
        int handle, const ::Ice::Current&) {
    LOG_INFO("KeepAlive");
    if (!_pdm->existsSession(handle)) _THROW(slice::UnknownSession());
    _pdm->addKA(handle, cb);
}

// Add or remove events.
void SessionImpl::regEvent(int handle, const slice::Event &e, const ::Ice::Current&){
    LOG_INFO("Add event");
    if (!_pdm->existsSession(handle)) _THROW(slice::UnknownSession());
    _pdm->regEvent(handle, e);
}
void SessionImpl::unregEvent(int handle, const slice::Event &e, const ::Ice::Current&){
    LOG_INFO("Remove event");
    if (!_pdm->existsSession(handle)) _THROW(slice::UnknownSession());
    _pdm->unregEvent(handle, e);
}

// Read or write file.
string SessionImpl::read(int handle, const string &path, const ::Ice::Current&){
    LOG_INFO("Read " <<path );
    if (!_pdm->existsSession(handle)) _THROW(slice::UnknownSession());
    return _pdm->read(path);
}

void SessionImpl::write(int handle, const string &path, const string &data, 
        const ::Ice::Current&){
    LOG_INFO("Write " <<path );
    if (!_pdm->existsSession(handle)) _THROW(slice::UnknownSession());
    _pdm->write(path, data);
}

// Directory operations.
bool SessionImpl::create(int handle, const string &path, bool tmp, 
        const ::Ice::Current&){
    LOG_INFO("Create " <<path );
    if (!_pdm->existsSession(handle)) _THROW(slice::UnknownSession());
    return _pdm->create(path, handle, tmp);
}

void SessionImpl::remove(int handle, const string &path, const ::Ice::Current&){
    LOG_INFO("Remove " <<path );
    if (!_pdm->existsSession(handle)) _THROW(slice::UnknownSession());
    _pdm->remove(path);
}

bool SessionImpl::exists(int handle, const string &path, const ::Ice::Current&){
    LOG_INFO("Exists " <<path );
    if (!_pdm->existsSession(handle)) _THROW(slice::UnknownSession());
    return _pdm->exists(path);
}

bool SessionImpl::istmp(int handle, const string &path, const ::Ice::Current&){
    LOG_INFO("Istmp " <<path );
    if (!_pdm->existsSession(handle)) _THROW(slice::UnknownSession());
    // Not implemented.
    return false;
}

vector<string> SessionImpl::list(int handle, const string &path, const ::Ice::Current&){
    LOG_INFO("List " <<path );
    if (!_pdm->existsSession(handle)) _THROW(slice::UnknownSession());
    return _pdm->list(path);
}

