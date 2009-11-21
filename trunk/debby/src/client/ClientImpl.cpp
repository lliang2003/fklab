/**
 * @file	ClientImpl.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年05月16日 12时14分04秒 CST
 *
 */

#include "debby.h"
#include "ClientImpl.hpp"
#include "ClientLogger.hpp"
#include <boost/lexical_cast.hpp>
#include <boost/algorithm/string.hpp>

using namespace std;
using namespace debby;
using namespace boost;

#undef LOG_PREFIX
#define LOG_PREFIX "[" <<_handle <<"] ClientImpl: "

#define _CATCH_EXCEPTIONS \
    _CATCH (slice::UnknownSession, "", _THROW(SessionClosed())) \
    _CATCH (Ice::Exception, ex, _THROW(SessionClosed())) \
    _CATCH (std::exception, ex.what(), _THROW(Exception(ex.what())))

ClientImpl::ClientImpl(const ClientConfig &conf) {
    // Initialzie logger.
    static bool logInited = false;
    Logger logger = Logger::getInstance("debby_client");
    if (!logInited) {
        logInited = true;
        SharedAppenderPtr append_1(
                new RollingFileAppender("debby_client.log", 4 * 1024 * 1024, 5));
        append_1->setName("debby_client");
        string pattern = "%D %p - %m\n";
        append_1->setLayout(std::auto_ptr<Layout>(new PatternLayout(pattern)));
        logger.addAppender(append_1);
        SharedAppenderPtr append_2(new ConsoleAppender(true));
        append_2->setName("console");
        append_2->setLayout(std::auto_ptr<Layout>(new PatternLayout(pattern)));
        if (conf.printLog)
            logger.addAppender(append_2);
        LOG_INFO("Log service initialized.");
    }
    string level = conf.logLevel;
    to_upper(level);
    if (level == "FATAL") 
        logger.setLogLevel(FATAL_LOG_LEVEL);
    else if (level == "ERROR") 
        logger.setLogLevel(ERROR_LOG_LEVEL);
    else if (level == "WARN") 
        logger.setLogLevel(WARN_LOG_LEVEL);
    else if (level == "INFO") 
        logger.setLogLevel(INFO_LOG_LEVEL);
    else if (level == "DEBUG") 
        logger.setLogLevel(DEBUG_LOG_LEVEL);
    else 
        logger.setLogLevel(TRACE_LOG_LEVEL);

    LOG_INFO("Get log level: " <<level);
    LOG_INFO("Construct.");
    _closed = false;
    _pss = new SessionService(conf);
    _handle = _pss->service()->connect(lexical_cast<string>(getpid()));
    _deamon_ptr = new ClientDeamon(_pss, _handle);
    LOG_INFO("Start Debby Client instance.");
    _deamon_ptr->start();
}

ClientImpl::~ClientImpl() {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("Destruct.");
    if (!_closed) close();
}

void ClientImpl::checkPath(const string &path) {
    if (_closed) throw SessionClosed();
    if (path.size() == 0) return;
    if (path[0] != '/' || path[path.size()-1] == '/')
        _THROW(InvalidName());
}

void ClientImpl::close() {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("Close.");
    if (_closed) return;
    _pss->service()->close(_handle);
    LOG_DEBUG("Join ClientDeamon thread.");
    _deamon_ptr->getThreadControl().join();
    delete _pss;
    _closed = true;
    LOG_DEBUG("Closed.");
}

bool ClientImpl::create(const string &path, bool e) {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("Create " <<path);
    checkPath(path);
    try {
        return _pss->service()->create(_handle, path, e);
    } _CATCH_EXCEPTIONS
}

void ClientImpl::remove(const string &path) {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("Remove " <<path);
    checkPath(path);
    try {
        _pss->service()->remove(_handle, path);
    } _CATCH_EXCEPTIONS
}

bool ClientImpl::exists(const string &path) {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("Exists " <<path);
    checkPath(path);
    try {
        return _pss->service()->exists(_handle, path);
    } _CATCH_EXCEPTIONS
}

bool ClientImpl::istmp(const string &path) {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("Isdir " <<path);
    checkPath(path);
    try {
        return _pss->service()->istmp(_handle, path);
    } _CATCH_EXCEPTIONS
}

vector<string> ClientImpl::list(const string &path) {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("List " <<path);
    string np = path;
    if (path == "/") np = "";
    checkPath(np);
    try {
        return _pss->service()->list(_handle, np);
    } _CATCH_EXCEPTIONS
}

bool ClientImpl::lock(const string &name) {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("Lock " <<name);
    checkPath(name);
    try {
        return _pss->service()->create(_handle, "lock" + name, true);
    } _CATCH_EXCEPTIONS
}

void ClientImpl::release(const string &name) {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("Release " <<name);
    checkPath(name);
    try {
        _pss->service()->remove(_handle, "lock" + name);
    } _CATCH_EXCEPTIONS
}

string ClientImpl::read(const string &path) {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("Read " <<path);
    checkPath(path);
    try {
        return _pss->service()->read(_handle, path);
    } _CATCH (slice::FileNotExist, "", _THROW(FileNotExist())) 
    _CATCH_EXCEPTIONS
}

void ClientImpl::write(const string &path, const string &data) {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("Write " <<path);
    checkPath(path);
    try {
        _pss->service()->write(_handle, path, data);
    } _CATCH (slice::FileNotExist, "", _THROW(FileNotExist()))
     _CATCH_EXCEPTIONS
}

void ClientImpl::regcb(const string &path, EventType et, shared_ptr<Callback> cb) {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("Register callback on " <<path);
    checkPath(path);
    try {
        _deamon_ptr->regcb(path, et, cb);
    } _CATCH_EXCEPTIONS
}

void ClientImpl::clearcb(const string &path, EventType et) {
    IceUtil::RecMutex::Lock lock(_mutex);
    LOG_INFO("Clear callback on " <<path);
    checkPath(path);
    try {
        _deamon_ptr->clearcb(path, et);
    } _CATCH_EXCEPTIONS
} 
