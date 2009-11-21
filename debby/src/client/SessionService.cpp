/**
 * @file	SessionService.cpp
 * 	
 * @author	Fan Kai(fk), Peking University	
 *
 */

#include "SessionService.hpp"
#include "ClientLogger.hpp"

#include <boost/lexical_cast.hpp>
#include <IceUtil/Lock.h>

using namespace debby;
using namespace boost;
using namespace std;

slice::SessionPrx &SessionService::service() {
    LOG_TRACE("SessionService: get service proxy.");
    return _service;
}

SessionService::SessionService(const ClientConfig &conf) {
   // Initialize ICE.
    try {
        Ice::PropertiesPtr props = Ice::createProperties();
        props->setProperty("Ice.Override.ConnectTimeout", "45000");
        props->setProperty("Ice.Override.Timeout", "45000");
        props->setProperty("Ice.RetryIntervals", "0 1000 5000");
        Ice::InitializationData id;
        id.properties = props;
        _ic = Ice::initialize(id);
        string proxystr = "DebbySession";
        for (unsigned i = 0; i < conf.addr.size(); ++i) {
            proxystr += ":tcp -h " + conf.addr[i].first + " -p " 
                + lexical_cast<string>(conf.addr[i].second);
        //cout <<proxystr <<endl;
        }

        Ice::ObjectPrx base = _ic->stringToProxy(proxystr);
        _service = slice::SessionPrx::checkedCast(base);
        LOG_INFO("SessionService: Ice service initialized.")
    } _CATCH (Ice::Exception, ex.ice_name(), _THROW(SessionClosed()))
}

SessionService::~SessionService() {
    LOG_TRACE("SessionService: Destruct.")
    if (_ic) _ic->destroy();
}
