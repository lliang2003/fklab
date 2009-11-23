
/**
 * @file   XServer.hpp
 * @author Tu Qichen <tqc@net.pku.edu.cn>
 * @date   Thu Jul  5 09:46:26 2007
 * 
 * @brief  
 * 
 * 
 */


#ifndef _X_SERVER_H_
#define _X_SERVER_H_

#include "TCPUtil.hpp"

namespace tfs
{
    class XServer
    {
    public:
        XServer();
        /** 
         * 
         * @param port the server port of server
         * 
         * @return -1 if error
         */
        int init(int port);
        /** 
         * here use boost_thread, and it will run the operator()
         */
        void operator()();
    private:
        TCPUtil _server;
    };
}


#endif
