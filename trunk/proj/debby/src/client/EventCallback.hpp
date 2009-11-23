/**
 * @file    EventCallback.hpp
 *  
 * 
 * @author  Fan Kai(fk), Peking University
 *
 */

#ifndef  EVENTCALLBACK_FILE_HEADER_INC
#define  EVENTCALLBACK_FILE_HEADER_INC

#include "debby.h"
#include "SessionService.hpp"
#include "ClientLogger.hpp"
#include <IceUtil/Thread.h>
using namespace std;

namespace debby {

class EventCallback: public IceUtil::Thread {
public:
    EventCallback(boost::shared_ptr<Callback> &cb): _cb(cb) { }

    virtual void run() { 
        LOG_DEBUG("EventCallback: Run.");
        _cb->run();
        LOG_DEBUG("EventCallback: Run over.");
    }

private:
    boost::shared_ptr<Callback> _cb;
};  // class EventCallback  

}   // namespace debby

#endif   /* ----- #ifndef EVENTCALLBACK_FILE_HEADER_INC  ----- */

