/**
 * @file   ChunkTransfer.hpp
 * @author Tu Qichen <tqc@Altavista>
 * @date   Thu Jun 28 20:29:37 2007
 * 
 * @brief  
 * 
 * 
 */


#ifndef _CHUNK_TRANSFER_H_
#define _CHUNK_TRANSFER_H_

//#include "Types.hpp"
#include "SocketMsg.hpp"
#include <stdint.h>
#include <fstream>
#include <string>
#include <vector>

namespace tfs
{
//     class ChunkServerAddr
//     {
//     public:
//         ChunkServerAddr()
//         {
//             memset(mIP,0,64);
//             mPort=0;
//         }
//         ChunkServerAddr(std::string ip,int port):mPort(port)
//         {
//             memset(mIP,0,64);
//             strncpy(mIP,ip.c_str(),63);
//         };
//         std::string getIP() const{ return std::string(mIP);};
//         int getPort() const{return mPort;};
//         char mIP[64];
//         int mPort;
//     };
    
    class ChunkTransfer
    {
    public:
        ChunkTransfer();
        ~ChunkTransfer();
        int initConnect(const std::vector<ChunkServerAddr> & vec_server);

        /** 
         * add new chunk to chunkserver
         * 添加一个新chunk前，须调用此操作，告知chunkserver此块大小。
           然后可以分批调用appendData(*)写入数据，不能总数不能超过事先声明的sum_len
         * @param chunk_id 
         * @param sum_len chunk的总大小
         * 
         * @return 
         */
        //        int addChunk(const int64_t chunk_id,const int sum_len);
        int addChunk(const int64_t chunk_id);
        int appendData(const char * buf,const int len);
        int finishAppend();

        /** 
         * 返回传输的结果数据，
         * @param vec_server 保存哪些server完成任务
         * @return 
         */
        int getStatus(std::vector<ChunkServerAddr> & vec_server);
        /** 
         * @param id  chunk id
         * @param infile local file that will be send, the file postion should be seek to right postion
         * @param offset the dest chunk offset, -1 means add to chunk tail
         * @param len how len the data will send
         * 
         * @return 
         */
        //        int writeData(int64_t chunk_id,ifstream & infile,int len=0,int offset=-1);
        //        int readData(int64_t chunk_id,ofstream & outfile,int len=0,int offset=0);
        int writeData(const int64_t chunk_id,const char * buf,const int len,const int offset=-1);
        int readData(const int64_t chunk_id,char * buf,const int len,const int offset=0);

        std::string getLocalPath(const int64_t & chunk_id);

        int closeConnect();
    private:
        int mSockfd;

        std::vector<ChunkServerAddr> m_servers;
        //添加新块时用,
        int m_new_chunk_id;  //正在add的chunk id
        int m_complete_len; //已传输的chunk大小
        //        int m_sum_len;
    };
}
#endif
