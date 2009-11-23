/**
 * @file	DataManager.cpp
 * 	
 * 
 * @author	Fan DataManager::Kai(fk), Peking University	
 * @date	2008年06月28日 17时09分30秒 CST
 *
 */

#include "DataManager.hpp"
#include "ServerLogger.hpp"
#include <boost/scoped_ptr.hpp>

using namespace debby;
using namespace std;

#define PREFIX "DataManager: "
#ifdef CXX_TEST
#define LOG(x) cout <<PREFIX <<x <<endl
#else
#define LOG(x) 
#endif

DataManager::DataManager(): _db_files(NULL, 0), _db_sessions(NULL, 0),
    _db_sfiles(NULL, 0), _db_sevents(NULL, 0) 
{
    LOG("Construct.");
    DBTYPE type = DB_BTREE;
    uint32_t flag = DB_CREATE;
    try {
        _db_files.open(NULL, "data/dm_files.db", NULL, type, flag, 0);
        _db_sessions.open(NULL, "data/dm_sessions.db", NULL, type, flag, 0);
        _db_sfiles.open(NULL, "data/dm_sfiles.db", NULL, type, flag, 0);
        _db_sevents.set_flags(DB_DUP);
        _db_sevents.open(NULL, "data/dm_sevents.db", NULL, type, flag, 0);
    } _CATCH_THROW(DbException, ex.what(), close());
    init();
}

DataManager::~DataManager() {
    LOG("Destruct.");
    close();
}

void DataManager::init() {
    LOG("Init data.");
    int r;
    try {
        Dbc *pc;
        // init files
        _db_files.cursor(NULL, &pc, 0);
        while ((r = pc->get(&_dbkey, &_dbdata, DB_NEXT)) == 0) {
            string path = (char *)_dbkey.get_data();
            string dat =  (char *)_dbdata.get_data();
            _files[path] = dat;
            LOG("get file: " <<path);
        }
        pc->close();

        // init sessions
        _db_sessions.cursor(NULL, &pc, 0);
        while ((r = pc->get(&_dbkey, &_dbdata, DB_NEXT)) == 0) {
            int handle = *(int *)_dbkey.get_data();
            LOG("get handle: " <<handle);
            _sessions[handle] = (char *)_dbdata.get_data();
        }
        pc->close();

        // init sfiles
        _db_sfiles.cursor(NULL, &pc, 0);
        while ((r = pc->get(&_dbkey, &_dbdata, DB_NEXT)) == 0) {
            string path = (char *)_dbkey.get_data();
            int handle = *(int *)_dbdata.get_data();
            _sessionFiles[path] = handle;
            LOG("get sfile: " <<path <<" " <<handle  <<" " <<path.size());
            assert(exists(path));
            assert(existsSession(handle));
        }
        pc->close();

        // init sevents
        _db_sevents.cursor(NULL, &pc, 0);
        Dbt key, data;
        while ((r = pc->get(&_dbkey, &_dbdata, DB_NEXT)) == 0) {
            int handle = *(int *)_dbkey.get_data();
            slice::Event e = make_event(_dbdata);
            _sessionEvents[handle].push(e);
            LOG("add event for session " <<handle);
            assert(existsSession(handle));
        }
        pc->close();
    } _CATCH_THROW(DbException, ex.what(), cout <<__LINE__<<endl);
}

void DataManager::close() {
    LOG("Close.");
    _db_files.close(0);
    _db_sessions.close(0);
    _db_sfiles.close(0);
    _db_sevents.close(0);
}

void DataManager::create(const string &path, int handle) {
    LOG("Create " <<path);
    assert(handle < 0 || existsSession(handle));
    assert(!exists(path));
    _files[path] = "";
    if (handle >= 0) settmp(path, handle);

    try {
        make_dbkey(path.c_str(), path.size() + 1);
        make_dbdata("", 1);
        _db_files.put(0, &_dbkey, &_dbdata, DB_NOOVERWRITE);
        _db_files.sync(0);
    } _CATCH_THROW(DbException, ex.what(), cout <<__LINE__<<endl);
}

void DataManager::settmp(const string &path, int handle) {
    LOG("Settmp " <<path <<" handle " <<handle);
    assert(_sessionFiles.find(path) == _sessionFiles.end());
    _sessionFiles[path] = handle;
    //cout <<handle <<endl;

    try {
        make_dbkey(path.c_str(), path.size() + 1);
        make_dbdata(&handle, sizeof(int));
        _db_sfiles.put(0, &_dbkey, &_dbdata, 0);
        _db_sfiles.sync(0);
    } _CATCH_THROW(DbException, ex.what(), cout <<__LINE__<<endl);
}

bool DataManager::exists(const string &path) {
    return _files.find(path) != _files.end();
}

void DataManager::remove(const string &path) {
    LOG("Remove " <<path);
    assert(exists(path));
    _files.erase(path);
    if (_sessionFiles.find(path) != _sessionFiles.end()) {
        _sessionFiles.erase(path);
    }

    try {
        make_dbkey(path.c_str(), path.size() + 1);
        _db_files.del(NULL, &_dbkey, 0);
        _db_sfiles.del(NULL, &_dbkey, 0);
        _db_files.sync(0);
        _db_sfiles.sync(0);
    } _CATCH_THROW(DbException, ex.what(), cout <<__LINE__<<endl);
}

vector<string> DataManager::list(const string &path) {
    LOG("List " <<path);
    map<string, string>::iterator it = _files.lower_bound(path + "/");
    vector<string> vs;
    while(it != _files.end() && it->first.find(path + "/", 0) == 0) {
        vs.push_back(it++->first);
    }
    return vs;
}

string DataManager::read(const string &path) {
    LOG("Read " <<path);
    assert(exists(path));
    return _files[path];
}

void DataManager::write(const string &path, const string &data) {
    LOG("Write " <<path <<" datasize = " <<data.size());
    assert(exists(path));
    _files[path] = data;

    try {
        make_dbkey(path.c_str(), path.size() + 1);
        make_dbdata(data.c_str(), data.size() + 1);
        _db_files.put(0, &_dbkey, &_dbdata, 0);
        _db_files.sync(0);
    } _CATCH_THROW(DbException, ex.what(), cout <<__LINE__<<endl);
}

void DataManager::addSession(const string &name, int handle) {
    LOG("Add session " <<name <<" handle = " <<handle);
    assert(!existsSession(handle));
    _sessions[handle] = name;

    try {
        make_dbkey(&handle, sizeof(int));
        make_dbdata(name.c_str(), name.size() + 1);
        _db_sessions.put(0, &_dbkey, &_dbdata, 0);
        _db_sessions.sync(0);
    } _CATCH_THROW(DbException, ex.what(), cout <<__LINE__<<endl);
}

bool DataManager::existsSession(int handle) {
    return _sessions.find(handle) != _sessions.end();
}

void DataManager::removeSession(int handle) {
    LOG("Remove session " <<handle);
    //cout <<existsSession(handle) <<endl;
    assert(existsSession(handle));
    removeSessionFiles(handle);
    removeSessionEvents(handle);
    _sessions.erase(handle);

    try {
        make_dbkey(&handle, sizeof(int));
        _db_sessions.del(NULL, &_dbkey, 0);
        _db_sessions.sync(0);
    } _CATCH_THROW(DbException, ex.what(), cout <<__LINE__<<endl);
}

void DataManager::addEvent(int handle, const slice::Event &e) {
    LOG("Add event for session " <<handle);
    assert(existsSession(handle));
    _sessionEvents[handle].push(e);

    try {
        make_dbkey(&handle, sizeof(int));
        make_dbdata(e);
        _db_sevents.put(NULL, &_dbkey, &_dbdata, 0);
        _db_sevents.sync(0); 
    } _CATCH_THROW(DbException, ex.what(), cout <<__LINE__<<endl);
}

bool DataManager::hasEvent(int handle) {
    LOG("Has event for session " <<handle <<" ?");
    return _sessionEvents.find(handle) != _sessionEvents.end()
        && !_sessionEvents[handle].empty();
}

slice::Event DataManager::getEvent(int handle) {
    LOG("Get event for session " <<handle);
    assert(existsSession(handle));
    assert(hasEvent(handle));
    slice::Event e = _sessionEvents[handle].front();
    _sessionEvents[handle].pop();

    try {
        Dbc *pc;
        _db_sevents.cursor(NULL, &pc, 0);
        make_dbkey(&handle, sizeof(int));
        pc->get(&_dbkey, &_dbdata, DB_SET);
        pc->del(0);
        char *sd = (char *)_dbdata.get_data();
        assert(*(slice::EventType *)sd == e.type);
        //cout <<e.path <<" " <<sd+4 <<endl;
        assert(e.path == string(sd + 4));
        _db_sevents.sync(0);
    } _CATCH_THROW(DbException, ex.what(), cout <<__LINE__<<endl);

    return e;
}

void DataManager::removeSessionFiles(int handle) {
    LOG("Remove tmp files for session " <<handle);
    assert(existsSession(handle));
    for (map<string, int>::iterator it = _sessionFiles.begin();
            it != _sessionFiles.end();) {
        if (it->second == handle) {
            string s = it++->first;
            remove(s);
        } else ++it;
    }
}

void DataManager::removeSessionEvents(int handle) {
    LOG("Remove events for session " <<handle);
    assert(existsSession(handle));
    _sessionEvents.erase(handle);

    try {
        Dbt key, data;
        Dbc *pc;
        _db_sessions.cursor(NULL, &pc, 0);
        int r;
        while ((r = pc->get(&_dbkey, &_dbdata, DB_NEXT)) == 0) {
            if (*(int *)_dbkey.get_data() == handle) {
                pc->del(0);
            }
        }
        _db_sessions.sync(0);
    } _CATCH_THROW(DbException, ex.what(), cout <<__LINE__<<endl);
}

slice::Event DataManager::make_event(const Dbt &data) {
    slice::Event e;
    e.type = *(slice::EventType *)data.get_data();
    e.path = (char *)data.get_data() + 4;
    return e;
}

void DataManager::make_dbkey(const void *p, int size) {
    static char buf[100];
    assert(size <= 100);
    memcpy(buf, p, size);
    _dbkey.set_data(buf);
    _dbkey.set_size(size);
}

void DataManager::make_dbdata(const void *p, int size) {
    static char buf[1024 * 1024];
    assert(size <= 1024 * 1024);
    memcpy(buf, p, size);
    _dbdata.set_data(buf);
    _dbdata.set_size(size);
}

void DataManager::make_dbdata(const slice::Event &e) {
    static char buf[100];
    assert(e.path.size() + 5 <= 100);
    memcpy(buf, &e.type, 4);
    memcpy(buf + 4, e.path.c_str(), e.path.size() + 1);
    _dbdata.set_data(buf);
    _dbdata.set_size(e.path.size() + 5);
}

