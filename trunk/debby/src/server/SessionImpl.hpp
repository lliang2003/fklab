/**
 * @file    SessionImpl.hpp
 *  
 * 
 * @author  Fan Kai(fk, const ::Ice::Current& = ::Ice::Current()), Peking University
 * @date    2008年06月28日 11时31分05秒 CST
 *
 */

#ifndef  SESSIONIMPL_FILE_HEADER_INC
#define  SESSIONIMPL_FILE_HEADER_INC

#include <Session.h>
#include "DebbyManager.hpp"

namespace debby {

class SessionImpl : public debby::slice::Session {
public:
    SessionImpl(DebbyManager *pdm);
    ~SessionImpl();

    virtual void keepAlive_async(const slice::AMD_Session_keepAlivePtr&, int handle,
            const ::Ice::Current& = ::Ice::Current());

    int connect(const std::string &name, 
            const ::Ice::Current& = ::Ice::Current());
    void close(int handle, 
            const ::Ice::Current& = ::Ice::Current());

    // Add or remove events.
    void regEvent(int handle, const slice::Event &e, 
            const ::Ice::Current& = ::Ice::Current());
    void unregEvent(int handle, const slice::Event &e, 
            const ::Ice::Current& = ::Ice::Current());

    // Read or write file.
    std::string read(int handle, const std::string &path, 
            const ::Ice::Current& = ::Ice::Current());
    void write(int handle, const std::string &path, const std::string &data, 
            const ::Ice::Current& = ::Ice::Current());

    // Directory operations.
    bool create(int handle, const std::string &path, bool tmp, 
            const ::Ice::Current& = ::Ice::Current());
    void remove(int handle, const std::string &path, 
            const ::Ice::Current& = ::Ice::Current());
    bool exists(int handle, const std::string &path, 
            const ::Ice::Current& = ::Ice::Current());
    bool istmp(int handle, const std::string &path, 
            const ::Ice::Current& = ::Ice::Current());
    std::vector<std::string> list(int handle, const std::string &path, 
            const ::Ice::Current& = ::Ice::Current());

private:
    DebbyManager *_pdm;

};  // class SessionImpl

}   // namespace debby

#endif   /* ----- #ifndef SESSIONIMPL_FILE_HEADER_INC  ----- */

