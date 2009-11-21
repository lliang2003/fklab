/**
 * @file    ClientDeamon.hpp
 *  
 * 
 * @author  Fan Kai(fk), Peking University
 * @date    2008年05月17日 09时52分41秒 CST
 *
 */

#ifndef  CLIENTDEAMON_FILE_HEADER_INC
#define  CLIENTDEAMON_FILE_HEADER_INC

#include "SessionService.hpp"
#include "debby.h"
#include <IceUtil/Thread.h>
#include <IceUtil/Lock.h>
#include <string>
#include <map>

namespace debby {

class ClientDeamon: public IceUtil::Thread {
public:
    ClientDeamon(SessionService *pss, int handle);

    void run();

    void invoke(const slice::Event &e);

    void regcb(const std::string &path, EventType t, boost::shared_ptr<Callback> cb);

    void clearcb(const std::string &path, EventType t);
    
private:
    typedef std::vector<boost::shared_ptr<Callback> > Callbacks;

    SessionService *_pss;

    int _handle;

    std::vector<IceUtil::ThreadControl> _threads;

    std::map<slice::Event, Callbacks> _cbs;

    IceUtil::Mutex _mutex;

    bool _started;

};  // class ClientDeamon

}   // namespace debby

#endif   /* ----- #ifndef CLIENTDEAMON_FILE_HEADER_INC  ----- */

