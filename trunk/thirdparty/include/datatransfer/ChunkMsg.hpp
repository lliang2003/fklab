/**
 * @file   ChunkMsg.hpp
 * @author Tu Qichen <tqc@net.pku.edu.cn>
 * @date   Wed Jul  4 10:11:43 2007
 * 
 * @brief  
 * 
 * ths class is used for communication between server and client
 */


#ifndef _CHUNK_MSG_CXX_H_
#define _CHUNK_MSG_CXX_H_

#include "Types.h"

namespace tfs
{
    enum ChunkCMD{READ,WRITE,APPEND};

    class ChunkMsg
    {
    public:
        ChunkMsg();
        ChunkMsg(ChunkCMD cmd,int64_t chunk_id,int data_len,int pipe_line_num,
                 int shift_pos):mCMD(cmd),mChunkID(chunk_id),mDataLen(data_len),
                                mPipeLineNum(pipe_line_num),mShiftPos(shift_pos)
        {
        };
    
        ChunkCMD mCMD;
        int64_t mChunkID;
        int mDataLen;
        int mPipeLineNum;
        int mShiftPos;    
    
    };
}
#endif
