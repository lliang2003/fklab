/**
 * @file   ClientMsg.hpp
 * @author Tu Qichen <tqc@net.pku.edu.cn>
 * @date   Thu Jul  5 08:22:06 2007
 * 
 * @brief  the message is send from chunkserver to client
 * 
 * 
 */

#ifndef _CLIENT_MSG_H_
#define _CLIENT_MSG_H_

namespace tfs
{
    enum ClientCMD{OK,RECEIVESUCE,ERROR};
    enum ErrorType{WRITEERROR,READERROR,DISKFAIL,NOSUCHCHUNK,SYSTEMBUZY,OTHER};

    class ClientMsg
    {
    public:
        ClientMsg();
        ~ClientMsg();
        ClientMsg cmd;
        ErrorType errorType;
    
    };
}


#endif
