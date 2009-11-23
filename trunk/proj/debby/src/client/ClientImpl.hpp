/**
 * @file    ClientImpl.hpp
 *  
 * 
 * @author  Fan Kai(fk), Peking University
 * @date    2008年06月28日 10时48分04秒 CST
 *
 */

#ifndef  CLIENTIMPL_FILE_HEADER_INC
#define  CLIENTIMPL_FILE_HEADER_INC

#include "debby.h"
#include "ClientDeamon.hpp"
#include <IceUtil/Thread.h>
#include <IceUtil/RecMutex.h>

namespace debby {

class ClientImpl {
public:
    ClientImpl(const ClientConfig &conf);
    ~ClientImpl();

    bool create(const std::string &path, bool tmp);
    void remove(const std::string &path);
    bool exists(const std::string &path);
    bool istmp(const std::string &path);
    std::vector<std::string> list(const std::string &path);

    bool lock(const std::string &name);
    void release(const std::string &name);
    std::string read(const std::string &path);
    void write(const std::string &path, const std::string &data);
    void regcb(const std::string &path, EventType t, boost::shared_ptr<Callback>);
    void clearcb(const std::string &path, EventType t);

    void close();

private:
    void checkPath(const std::string &path);

    SessionService *_pss;

    bool _closed;

    int _handle;

    IceUtil::RecMutex _mutex;

    IceUtil::Handle<ClientDeamon> _deamon_ptr;

};  // class ClientImpl

}   // namespace debby


#endif   /* ----- #ifndef CLIENTIMPL_FILE_HEADER_INC  ----- */

