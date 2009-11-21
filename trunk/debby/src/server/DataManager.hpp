/**
 * @file    DataManager.hpp
 *  
 * 
 * @author  Fan Kai(fk), Peking University
 * @date    2008年06月28日 11时32分14秒 CST
 *
 */

#ifndef  DATAMANAGER_FILE_HEADER_INC
#define  DATAMANAGER_FILE_HEADER_INC

#include <string>
#include <Session.h>
#include <list>
#include <map>
#include <queue>
#include <IceUtil/RecMutex.h>
#include <db_cxx.h>

namespace debby {

class DataManager {
public:
    DataManager();
    ~DataManager();
    void close();
    void create(const std::string &path, int handle = -1);
    bool exists(const std::string &path);
    void remove(const std::string &path);
    std::vector<std::string> list(const std::string &path);

    std::string read(const std::string &path);
    void write(const std::string &path, const std::string &data);

    void addSession(const std::string &name, int handle);
    bool existsSession(int handle);
    void removeSession(int handle);

    void addEvent(int handle, const slice::Event &e);
    bool hasEvent(int handle);
    slice::Event getEvent(int handle);

private:
    void init();
    slice::Event make_event(const Dbt &data);
    void make_dbkey(const void *p, int size);
    void make_dbdata(const void *p, int size);
    void make_dbdata(const slice::Event &e);
    void settmp(const std::string &path, int handle);
    void removeSessionFiles(int handle);
    void removeSessionEvents(int handle);

    std::map<std::string, std::string> _files;
    std::map<int, std::string> _sessions;
    std::map<std::string, int> _sessionFiles;
    std::map<int, std::queue<slice::Event> > _sessionEvents;

    Db _db_files;
    Db _db_sessions;
    Db _db_sfiles;
    Db _db_sevents;

    char _databuf[100];
    Dbt _dbkey;
    Dbt _dbdata;

};  // namespace DataManager

}   // namespace debby

#endif   /* ----- #ifndef DATAMANAGER_FILE_HEADER_INC  ----- */

