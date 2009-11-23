/**
 * @file   SocketMsg.hpp
 * @author Tu Qichen <tqc@net.pku.edu.cn>
 * @date   Tue Jul 10 16:58:35 2007
 * 
 * @brief  
 * 
 * 
 */

#ifndef _SOCKET_MSG_HPP_
#define _SOCKET_MSG_HPP_
//#include "Types.hpp"
#include <stdint.h>
#include <string>

namespace tfs
{
    enum ChunkCMD{READ,WRITE,APPEND,DISCONNECT,REMOTE_OP, GET_LOCAL_PATH};

    class Client2ServerMsg
    {
    public:
        Client2ServerMsg(){};
        Client2ServerMsg(ChunkCMD cmd,int64_t chunk_id,int data_len,int shift_pos,
                         int pipe_line_num)
            :   mCMD(cmd),mChunkID(chunk_id),mDataLen(data_len),
                mPipeLineNum(pipe_line_num),mShiftPos(shift_pos)
        {
        };

        ChunkCMD mCMD;
        int64_t mChunkID;    
        int mDataLen;
        int mPipeLineNum; //for write and append
        int mShiftPos;    // only for write
        
    };

    enum ClientCMD{OK,RECEIVESUCE,ERROR};
    enum ErrorType{WRITEERROR=1,READERROR,DISKFAIL,NOSUCHCHUNK,SYSTEMBUZY,OTHER};

    class Server2ClientMsg
    {
    public:
        Server2ClientMsg(){};
        Server2ClientMsg(ClientCMD cmd,ErrorType error):mCMD(cmd),errorType(error){};        
        ClientCMD mCMD;
        ErrorType errorType;

        static std::string getErrorStr(const ErrorType & error);
    };

    //用于区分后面紧跟的socket数据类型
    enum SockDataType{ S2C_MSG,C2S_MSG, DATA_MSG};


    //用于控制chunkserve的命令
    enum RemoteCMD {LIST_CHUNK_ID, LIST_CHUNK_INFO, REMOTE_DISCONNECT};
    class ChunkStatus
    {
    public:
        ChunkStatus(){}
        ChunkStatus(int64_t id,int size,int version):m_id(id),m_size(size),m_version(version)
        {}
        int64_t m_id;
        int  m_size;
        int m_version;
    };



    class ChunkServerAddr
    {
    public:
        ChunkServerAddr()
        {
            memset(mIP,0,64);
            mPort=0;
        }
        ChunkServerAddr(std::string ip,int port):mPort(port)
        {
            memset(mIP,0,64);
            strncpy(mIP,ip.c_str(),63);
        };
        std::string getIP() const{ return std::string(mIP);};
        int getPort() const{return mPort;};
        char mIP[64];
        int mPort;
    };

    
}

#endif
