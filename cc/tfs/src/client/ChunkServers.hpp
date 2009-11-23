/**
 * @file    ChunkServers.hpp
 *  
 * 
 * @author  Fan Kai(fk), Peking University
 * @date    12/06/08 09:45:10 CST
 *
 */

#ifndef  CHUNKSERVERS_FILE_HEADER_INC
#define  CHUNKSERVERS_FILE_HEADER_INC

#include "Client.hpp"
#include <vector>
#include <Ice/Ice.h>
#include <boost/lexical_cast.hpp>
#include <fstream>
using namespace tfs::slice;
using namespace std;

namespace tfs {

class ChunkServers {
public:
    virtual int64_t read(char *buf, int64_t chkoff, int64_t nbytes) = 0;
    virtual int64_t write(const char *buf, int64_t chkoff, int64_t nbytes) = 0;
    virtual void setChunk(const LocatedChunk &lchk) = 0;
    virtual void addChunk(int64_t chkid) = 0;
    virtual ~ChunkServers() {}
};

class MockChunkServers: public ChunkServers {
public:
    int64_t read(char *buf, int64_t chkoff, int64_t nbytes) {
        ifstream fin(m_filename.c_str(), ios::binary);
        fin.seekg(chkoff, ios::beg);
        fin.read(buf, nbytes);
        LOG_DEBUG("MockChunkServers: Read " <<fin.gcount() <<" bytes from pos " 
                <<chkoff <<" of file " <<m_filename);
        return fin.gcount();
    }

    int64_t write(const char *buf, int64_t chkoff, int64_t nbytes) {
        ofstream fout(m_filename.c_str(), ios::binary|ios::app);
        //fout.seekp(chkoff, ios::beg);
        fout.write(buf, nbytes);
        LOG_DEBUG("MockChunkServers: Write " <<nbytes <<" bytes from pos " 
                <<chkoff <<" of file " <<m_filename);
        fout.close();
        return nbytes;
    }
    
    void setChunk(const LocatedChunk &lchk) {
        m_filename = boost::lexical_cast<string>(lchk.chk.id) + ".chk";
        LOG_DEBUG("MockChunkServers: Set to file " <<m_filename);
    }

    void addChunk(int64_t chkid) {
        string str = boost::lexical_cast<string>(chkid) + ".chk";
        LOG_DEBUG("MockChunkServers: Add " <<str);
        ofstream fout(str.c_str(), ios::binary);
        fout.close();
    }

private:
    string m_filename;
};  // class MockChunkServers

class RealChunkServers: public ChunkServers {
public:
    int64_t read(char *buf, int64_t chkoff, int64_t nbytes) {
        std::vector<unsigned char> vc = m_vt[0]->read(m_lchk.chk.id, chkoff, nbytes); 
        LOG_DEBUG("File: Read from pos " <<chkoff <<" of chunk " <<m_lchk.chk.id);
        assert(vc.size() == nbytes);
        memcpy(buf, &vc[0], nbytes);
        return nbytes;
    }

    int64_t write(const char *buf, int64_t chkoff, int64_t nbytes) {
        std::vector<unsigned char> vc(nbytes);
        vc.assign(buf, buf + nbytes);
        for (int i = 0; i < m_vt.size(); ++i) {
            m_vt[i]->write(m_lchk.chk.id, vc, chkoff, nbytes);
        }
        /*
           slice::Addresses addrs;
           slice::Address addr1, addr2;
           addr1.name = m_currentChunk.locations[1].name;
           addr1.port = m_currentChunk.locations[1].port;
           addrs.push_back(addr1);
           addr2.name = m_currentChunk.locations[2].name;
           addr2.port = m_currentChunk.locations[2].port;
           addrs.push_back(addr2);
           m_vt[0]->writeAll(m_currentChunk.chk.id, vc, 
           m_fileInfo.chunkSize - m_availableSize, nbytes, addrs);
           */
        return nbytes;
    }

    void setChunk(const LocatedChunk &lchk) {
        m_lchk = lchk;
        static Ice::CommunicatorPtr ic;
        static bool inited = false;
        if (!inited) {
            Ice::InitializationData id;
            Ice::PropertiesPtr props = Ice::createProperties();
            //props->setProperty("Ice.MessageSizeMax", "2048");
            props->setProperty("Ice.MessageSizeMax", "65536");
            //props->setProperty("Ice.Override.Timeout", "10000");
            //props->setProperty("Ice.RetryIntervals", "0 1000 5000");
            id.properties = props;
            ic = Ice::initialize(id);
            inited = true;
        }
        m_vt.clear();
        int n = lchk.locations.size();
        LOG_DEBUG("File: Init " <<n <<" chunk addresses.");
        int r = rand();

        try {
            for (int i = 0; i < n; ++i) {
                Ice::ObjectPrx base = ic->stringToProxy("Transfer:tcp -h " 
                        + lchk.locations[(r+i)%n].name + " -p " 
                        + boost::lexical_cast<std::string>(lchk.locations[(r+i)%n].port) );
                m_vt.push_back(TransferPrx::checkedCast(base));
                LOG_DEBUG("File: Address " <<lchk.locations[(r+i)%n].name 
                        <<":" <<lchk.locations[(r+i)%n].port);
            }
        } catch (const Ice::Exception &ex) {
            LOG_FATAL("File: Get Ice Exception: " <<ex);
            LOG_FATAL("File: Throw api::ConnectionError");
            throw api::ConnectionError();
        }

    }

    void addChunk(int64_t chkid) {
        for (int i = 0; i < m_vt.size(); ++i) {
            m_vt[i]->addChunk(m_lchk.chk.id);
        }
    }
private:
    std::vector<tfs::slice::TransferPrx> m_vt;
    LocatedChunk m_lchk;
};  // class RealChunkServers

}   // namespace tfs


#endif   /* ----- #ifndef CHUNKSERVERS_FILE_HEADER_INC  ----- */

