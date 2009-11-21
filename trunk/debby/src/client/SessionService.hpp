/**
 * @file    SessionService.hpp
 *  
 * 
 * @author  Fan Kai(fk), Peking University
 * @date    2008年05月16日 20时17分14秒 CST
 *
 */

#ifndef  SESSIONSERVICE_FILE_HEADER_INC
#define  SESSIONSERVICE_FILE_HEADER_INC

#include "debby.h"
#include <Ice/Ice.h>
#include <Session.h>

namespace debby {

class SessionService {
public:
    SessionService(const ClientConfig &conf);
    ~SessionService();

    slice::SessionPrx &service();

private:
    slice::SessionPrx _service;
    Ice::CommunicatorPtr _ic;

};  // class SessionService

}   // namespace debby

#endif   /* ----- #ifndef SESSIONSERVICE_FILE_HEADER_INC  ----- */

