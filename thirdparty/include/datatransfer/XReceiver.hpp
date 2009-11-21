
/**
 * @file   XReceiver.hpp
 * @author Tu Qichen <tqc@net.pku.edu.cn>
 * @date   Tue Jul 10 20:07:21 2007
 * 
 * @brief  
 * 
 * 
 */


#ifndef _X_RECEIVER_H_
#define _X_RECEIVER_H_

#include "TCPUtil.hpp"
#include <fstream>

namespace tfs
{    
    class XReceiver
    {
    public:
        XReceiver(int sockfd,std::string file_path="");
        void operator()();
    private:
        int mSockfd;
        std::string mFilePath;
//         int recvData(std::ofstream & outfile,int sockfd);
//         int recvData(char * dest,int len);
    };
}
#endif
