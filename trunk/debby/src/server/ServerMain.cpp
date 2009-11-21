/**
 * @file	ServerMain.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年06月28日 11时30分54秒 CST
 *
 */

#include "DebbyManager.hpp"
#include "SessionImpl.hpp"
#include "KeepAliveDeamon.hpp"
#include "ServerLogger.hpp"
#include <iostream>
#include <common/Config.hpp>
#include <IceUtil/Thread.h>
#include <Ice/Ice.h>
#include <boost/algorithm/string.hpp>
using namespace std;

namespace debby {

class ServerMain {
public:
    ServerMain(const string &path) {
        Config conf;
        if (!conf.load(path.c_str())) {
            cerr <<"Can not load configuration file." <<endl;
            throw exception();
        }
        conf.setSection("server");
        string ip = conf.getStringValue("server_ip");
        string port = conf.getStringValue("server_port");
        string loglevel = conf.getStringValue("log_level");
        if (ip.empty() || port.empty()) {
            cerr <<"Load configuration error." <<endl;
            throw exception();
        }
        Logger logger = Logger::getInstance("debby_server");
        SharedAppenderPtr append_1(
                new RollingFileAppender("debby_server.log", 4 * 1024 * 1024, 5));
        append_1->setName("debby_server");
        string pattern = "%D %p - %m\n";
        append_1->setLayout(std::auto_ptr<Layout>(new PatternLayout(pattern)));
        SharedAppenderPtr append_2(new ConsoleAppender());
        append_2->setName("console");
        append_2->setLayout(std::auto_ptr<Layout>(new PatternLayout(pattern)));
        logger.addAppender(append_1);
        logger.addAppender(append_2);
        boost::to_upper(loglevel);
        if (loglevel == "FATAL") 
            logger.setLogLevel(FATAL_LOG_LEVEL);
        else if (loglevel == "ERROR") 
            logger.setLogLevel(ERROR_LOG_LEVEL);
        else if (loglevel == "WARN") 
            logger.setLogLevel(WARN_LOG_LEVEL);
        else if (loglevel == "INFO") 
            logger.setLogLevel(INFO_LOG_LEVEL);
        else if (loglevel == "DEBUG") 
            logger.setLogLevel(DEBUG_LOG_LEVEL);
        else 
            logger.setLogLevel(TRACE_LOG_LEVEL);

        LOG_INFO("Server: Starts Debby Server.");
        LOG_INFO("Server: Get service port: " <<port);
        LOG_INFO("Server: Get log level: " <<loglevel);


        try {
            _ic = Ice::initialize();
            _adapter = _ic->createObjectAdapterWithEndpoints(
                    "DebbyAdapter", "default -p " + port);
            _pdm = new DebbyManager();
            _pkad = new KeepAliveDeamon(_pdm);
            Ice::ObjectPtr obj = new SessionImpl(_pdm);
            _adapter->add(obj, _ic->stringToIdentity("DebbySession"));
        } catch (Ice::Exception &ex) {
            cerr <<ex.ice_name() <<endl;
            throw ex;
        }


        LOG_INFO("Server: Ice service initialized.");
    }

    void startMaster() {
        LOG_INFO("Server: Debby master started.");
        try {
        _pkad->start();
        _adapter->activate();
        _ic->waitForShutdown();
        } catch (Ice::Exception &ex){
            cerr <<ex.ice_name() <<endl;
            throw ex;
        } catch (exception &ex) {
            cerr <<ex.what() <<endl;
            throw ex;
        }
    }

    void stopMaster() {
        LOG_INFO("Server: Debby master stopped.");
        _ic->shutdown();
        _pkad->getThreadControl().join();
    }

    ~ServerMain() {
        if (_ic) 
            _ic->destroy();

    }
private:
    DebbyManager *_pdm;
    KeepAliveDeamon *_pkad;
    Ice::CommunicatorPtr _ic;
    Ice::ObjectAdapterPtr _adapter;
};

}   // namespace debby

int main(int argc, char **argv) {
    if (argc != 2) {
        cerr <<"Usage: " <<argv[0] <<" config_file" <<endl;
        return -1;
    }
    try {
        debby::ServerMain sm(argv[1]);
        sm.startMaster();
    } catch (exception &ex) {
        LOG_ERROR("Server: " <<ex.what());
        return -1;
    }
}

