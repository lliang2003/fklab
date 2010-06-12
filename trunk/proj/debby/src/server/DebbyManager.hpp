/**
 * @file    DebbyManager.hpp
 *  
 * 
 * @author  Fan Kai(fk), Peking University
 * @date    2008年07月01日 08时44分49秒 CST
 *
 */

#ifndef  DEBBYMANAGER_FILE_HEADER_INC
#define  DEBBYMANAGER_FILE_HEADER_INC

#include "DataManager.hpp"
#include <set>

namespace debby {

class DebbyManager {
public:
    DebbyManager();
    bool create(const std::string &path, int handle = -1, bool tmp = false );
    void remove(const std::string &path);
    bool exists(const std::string &path);
    std::vector<std::string> list(const std::string &path);

    std::string read(const std::string &path);
    void write(const std::string &path, const std::string &data);

    int addSession(const std::string &name);
    void updateSession(int handle, int t = time(0));
    void removeSession(int handle);
    void removeExpiredSessions(int et = 15);
    bool existsSession(int handle);
 
    void addKA(int handle, const slice::AMD_Session_keepAlivePtr&);
    void replyTimeoutKA(int timeout = 5);
    void invoke(const slice::Event &e);

    void regEvent(int handle, const slice::Event &e);
    void unregEvent(int handle, const slice::Event &e);

private:
    slice::Event makeEvent(slice::EventType t, const std::string &path);
    std::map<int, int> _sessionUpdateTime;
    std::map<int, std::set<slice::Event> > _sessionEvents;
    std::map<int, slice::AMD_Session_keepAlivePtr> _cbs;
    std::map<slice::Event, std::set<int> > _events;
    IceUtil::RecMutex _mutex;
    DataManager _data;

};  // class DebbyManager

}   // namespace debby

#endif   /* ----- #ifndef DEBBYMANAGER_FILE_HEADER_INC  ----- */

