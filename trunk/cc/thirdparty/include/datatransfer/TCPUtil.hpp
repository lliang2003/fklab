
/**
 * @file   TCPUtil.hpp
 * @author Tu Qichen <tqc@net.pku.edu.cn>
 * @date   Thu Jul  5 08:22:31 2007
 * 
 * @brief  a Utility class of TCP
 * 
 * 
 */


#ifndef _TCPUTIL_CXX_H_
#define _TCPUTIL_CXX_H_


#include "SocketMsg.hpp"
#include "SockException.hpp"
#include <string>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <time.h>
#include <assert.h>

#define LISTENQ 5

//using namespace std;
namespace tfs
{
    //the max data len
    const int PACKAGE_LEN=1400;
        
    class ClientInfo
    {
    public:
        std::string GetIP(){return ip;};
        int GetPort(){return port;};
        std::string ip;
        int port;
    };

    class TCPUtil
    {
    public:
        TCPUtil();
        ~TCPUtil();
        int OpenService(int port);
        static int ConnectServer(const std::string& ip,int port);
        int Accept(int fd,ClientInfo & info);
        int Accept(ClientInfo & info);
        int GetSockfd(){return sockfd;};

        static int SendData(int sock_fd,const char * buf,int len);
        static int RecvData(int sock_fd,char * buf,int len);

        //封装socket, 出错返回Exception
        static int SendDataE(int sock_fd,const char * buf,int len,const char * e_info="") throw(TCPException);
        static int RecvDataE(int sock_fd,char * buf,int len,const char * e_info="") throw(TCPException);

        static void Close(int sock);
    private:
        int sockfd;
        int Listen(int sock_fd,int backlog);
        int SockAddr2Str(std::string & str,struct sockaddr_in addr);
    };

    
}
#endif
