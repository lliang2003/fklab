/**
 * @file    KeepAliveDeamon.hpp
 *  
 * 
 * @author  Fan Kai(fk), Peking University
 * @date    2008��06��28�� 15ʱ55��50�� CST
 *
 */

#ifndef  KEEPALIVEDEAMON_FILE_HEADER_INC
#define  KEEPALIVEDEAMON_FILE_HEADER_INC

#include <Session.h>
#include <ctime>
#include <IceUtil/Thread.h>
#include "DebbyManager.hpp"

namespace debby {

class KeepAliveDeamon : public IceUtil::Thread {
public:
    KeepAliveDeamon(DebbyManager *pdm):_pdm(pdm){}

    virtual void run();

    void stop();

private:
    IceUtil::Monitor<IceUtil::Mutex> _monitor;

    bool _done;

    DebbyManager *_pdm;

};  // class KeepAliveDeamon

}   // namespace debby

#endif   /* ----- #ifndef KEEPALIVEDEAMON_FILE_HEADER_INC  ----- */

