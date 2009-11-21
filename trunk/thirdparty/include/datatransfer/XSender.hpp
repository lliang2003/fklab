/**
 * @file   XSender.hpp
 * @author Tu Qichen <tqc@net.pku.edu.cn>
 * @date   Mon Jul  9 10:45:12 2007
 * 
 * @brief  
 * 
 * 
 */


#ifndef _X_SENDER_HPP_
#define _X_SENDER_HPP_

#include "TCPUtil.hpp"
#include <string>
#include <fstream>

namespace tfs
{
    class XSender
    {
    public:
        /** 
         * if you want to initial a thread to send file,
         * you shoud use give file_path, the the send use to send the file over
         * @param sockfd 
         * @param file_path 
         */
        XSender(int sockfd=-1,std::string file_path="");
        int init(int sockfd);
        int sendData(const char * data,const int data_size);
        void operator()();
    private:
        int mSockfd;
        std::string mFilePath;
        
    };
}

#endif
