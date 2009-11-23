/**
 * @file    File.cpp
 *   This file implement the File class.
 * 
 * @author    Fan Kai(FK), Peking University    
 * @date    05/26/2007 06:26:00 PM CST
 *
 */

#include "File.hpp"
#include "Client.hpp"
#include "Master.hpp"
#include "ChunkServers.hpp"
#include <boost/lexical_cast.hpp>
#include <common/Exception.hpp>

using namespace tfs;
using namespace tfs::slice;
using namespace std;

File::File() {
    Client::init();
    m_address = Client::address();
    m_path = "";
    if (Client::mock()) {
        m_master = MockMaster::instance();
        m_css = new MockChunkServers();
    } else {
        m_master = RealMaster::instance();
        m_css = new RealChunkServers();
    }
}

File::~File() {
    close();
}

void File::open(const string &path, FileOpenMode mode) {
    try {
        if (mode == FileOpenRead) { 
            LOG_INFO("File: Open file " <<path <<" for read:")
        } else if (mode == FileOpenAppend){
            LOG_INFO("File: Open file " <<path <<" for append.")
        }

        m_path = path;
        m_offset = 0;
        m_chunkIndex = 0;
        m_availableSize = -1;
        // Set lastChunkIndex as impossible.
        m_lastChunkIndex = -1;    

        m_fileInfo = m_master->getFileInfo(m_path);
        LOG_DEBUG("File: Size of " <<m_path <<" : " <<m_fileInfo.size);
        if (mode == FileOpenRead) {
            int64_t size = 0;
            m_locatedChunks = m_master->getChunksInfo(m_address, m_path);
            for (int i = 0; i < m_locatedChunks.size(); ++i) {
                m_chunkOffset.push_back(size);
                size += m_locatedChunks[i].chk.size;
                LOG_DEBUG("File: Chunk " <<i <<" : id = " 
                        <<m_locatedChunks[i].chk.id <<" , size = "
                        <<m_locatedChunks[i].chk.size)
            }
            m_chunkOffset.push_back(size);
            assert(size == m_fileInfo.size);
        }
    } catch (NoSuchFileOrDir &ex) {
        LOG_ERROR("File: Throw api::NoSuchFileOrDir")
        throw api::NoSuchFileOrDir();
    } catch (Ice::Exception &ex) {
        LOG_FATAL("File: Get Ice Exception: " <<ex);
        LOG_FATAL("File: Throw api::ConnectionError");
        throw api::ConnectionError();
    }
}

void File::close() {
    try {
        if (m_path.size() == 0) return; 
        LOG_INFO("File: Close file: " <<m_path <<".");
        m_master->close(m_address, m_path);
        m_path = "";
    } catch (NoSuchFileOrDir &ex) {
        // This exception should never be thrown.
        LOG_ERROR("File: Throw api::NoSuchFileOrDir")
        throw api::NoSuchFileOrDir();
    } catch (NoChunkAllocated &ex) {
        LOG_ERROR("File: Throw api::AllocateFail")
        throw api::AllocateFail();
    } catch (Ice::Exception &ex) {
        LOG_FATAL("File: Get Ice Exception: " <<ex);
        LOG_FATAL("File: Throw api::ConnectionError");
        throw api::ConnectionError();
    }
}

int64_t File::read(char *buf, int64_t nbytes) {
    LOG_DEBUG("File: Try to read " << nbytes << " bytes from pos " <<m_offset);
    int64_t nread = 0;
    int64_t n = 0;

    while (nbytes > 0) {
        if (m_chunkIndex >= m_locatedChunks.size()) break;
        LOG_DEBUG("File: Read from chunk " <<m_chunkIndex
                <<" chunkid = " <<m_locatedChunks[m_chunkIndex].chk.id);
        if (m_lastChunkIndex != m_chunkIndex) 
            m_css->setChunk(m_locatedChunks[m_chunkIndex]);
        m_lastChunkIndex = m_chunkIndex;
        n = min(m_chunkOffset[m_chunkIndex + 1] - m_offset, nbytes);
        n = min(n, (int64_t)512 * 1024);
        
        try {
            m_css->read(buf, m_offset - m_chunkOffset[m_chunkIndex], n);
        } catch (const Ice::Exception &ex) {
            LOG_FATAL("File: Get Ice Exception: " <<ex);
            LOG_FATAL("File: Throw api::TransferFail");
            throw api::TransferFail();
        }

        buf += n;
        nbytes -= n;
        nread += n;

        LOG_DEBUG("File: Read " << n << " bytes from chunk " <<m_chunkIndex);
        LOG_DEBUG("File: " <<nbytes << " bytes left.");

        m_offset += n;
        if (m_offset >= m_chunkOffset[m_chunkIndex + 1]) m_chunkIndex ++;
    }

    LOG_INFO("File: Read " << nread << " bytes from " <<m_path);
    return nread;
}

void File::addChunk() {
    try {
        LOG_DEBUG("File: Try to add a new chunk.");

        m_currentChunk = m_master->addChunk(m_address, m_path);
        m_master->completeWrittenChunk(m_currentChunk);
        assert(m_currentChunk.chk.size == 0);
        LOG_DEBUG("File: New chunk: " <<m_currentChunk.chk.id);
        m_css->setChunk(m_currentChunk);
        m_css->addChunk(m_currentChunk.chk.id);

        m_availableSize = m_fileInfo.chunkSize;
        LOG_INFO("File: Added new chunk:" <<m_currentChunk.chk.id);
    } catch (NoSuchFileOrDir &ex) {
        // this exception should never be thrown out
        LOG_ERROR("File: Throw api::NoSuchFileOrDir");
        throw api::NoSuchFileOrDir();
    } catch (NoChunkAllocated &ex) {
        LOG_ERROR("File: Throw api::AllocateFail");
        throw api::AllocateFail();
    } catch (Ice::Exception &ex) {
        LOG_ERROR("File: Get Ice Exception1: " <<ex);
        LOG_INFO("File: Abandon add chunk:" <<m_currentChunk.chk.id);
        m_master->abandonAddChunk(m_currentChunk.chk);
        LOG_ERROR("File: Throw api::TransferFail");
        throw api::TransferFail();
    }
}

void File::append(const char *buf, int nbytes) {
    LOG_DEBUG("File: Try to append " << nbytes << " bytes to file " <<m_path);
    if (nbytes <= 0) return;
    if (m_availableSize < nbytes) addChunk();

    try {
        m_css->write(buf, m_fileInfo.chunkSize - m_availableSize, nbytes);
        m_currentChunk.chk.size += nbytes;
        m_availableSize -= nbytes;
        LOG_DEBUG("File: Update chunk " <<m_currentChunk.chk.id <<" size = " 
                <<m_currentChunk.chk.size);
        m_master->updateChunkInfo(m_currentChunk);
    } catch (const slice::GenericException &ex) {
        LOG_FATAL("File: Get GenericException: " <<ex.ice_name() <<"," <<ex.reason);
        //LOG_INFO("File: Abandon added chunk " <<m_currentChunk.chk.id);
        //m_master->abandonAddChunk(m_currentChunk.chk);
        LOG_ERROR("File: Throw api::TransferFail");
        throw api::TransferFail();
    } catch (const Ice::Exception &ex) {
        LOG_FATAL("File: Get Ice Exception: " <<ex);
        //LOG_INFO("File: Abandon added chunk " <<m_currentChunk.chk.id);
        //m_master->abandonAddChunk(m_currentChunk.chk);
        LOG_ERROR("File: Throw api::TransferFail");
        throw api::TransferFail();
    }
    LOG_INFO("File: Append " <<nbytes <<" bytes to " <<m_path);
}

int64_t File::seek(int64_t offset, int whence) {
    int64_t newoff = m_offset;
    if (whence == SEEK_SET) {
        LOG_INFO("File: Seek from start: " << offset);
        newoff = offset;
    }
    else if (whence == SEEK_CUR) {
        LOG_INFO("File: Seek from current: " <<offset);
        newoff = m_offset + offset;
    }
    else {
        LOG_INFO("File: Seek from end: " <<offset);
        newoff = m_fileInfo.size + offset;
    }

    // FIXME: whether this is suitable
    if (newoff < 0) 
        m_offset = 0;
    else if (newoff > m_fileInfo.size)
        m_offset = m_fileInfo.size;
    else 
        m_offset = newoff;

    while (m_chunkIndex < m_locatedChunks.size()
            && m_chunkOffset[m_chunkIndex + 1] <= m_offset) {
        ++ m_chunkIndex;
        LOG_DEBUG("File: Seek to next chunk, chunk index: " 
                <<m_chunkIndex);
    }

    while (m_chunkIndex > 0 && m_chunkOffset[m_chunkIndex] > m_offset) {
        -- m_chunkIndex;
        LOG_DEBUG("File: Seek to previous chunk, chunk index: " 
                <<m_chunkIndex);
    }

    LOG_DEBUG("File: offset after seek:" <<m_offset);
    LOG_DEBUG("File: chunk index after seek:" <<m_chunkIndex);

    return m_offset;
}

