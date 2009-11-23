
#ifndef _TFS_XCONNECTOR_HPP
#define _TFS_XCONNECTOR_HPP

#include <string>
#include <vector>
#include "SocketMsg.hpp"
namespace tfs
{
    
    class XConnector
    {
    public:
        XConnector();
        ~XConnector();
        bool init(std::string strip, int port);
        bool closeConnect();
        std::vector<ChunkStatus> getChunks();

    private:
        int m_sock;
    };

}
#endif
