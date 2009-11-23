/**
 * @file    Master.hpp
 *  
 * 
 * @author  Fan Kai(fk), Peking University
 * @date    12/06/08 09:13:32 CST
 *
 */

#ifndef  MASTER_FILE_HEADER_INC
#define  MASTER_FILE_HEADER_INC

#include "Client.hpp"
namespace tfs {

class Master {
public:
    //virtual void open(Address client, const std::string &name, int mode) = 0;
    //virtual void reportBadChunks(LocatedChunks chunks) = 0;
    virtual void setReplication(const std::string &name, short replication) = 0;
    virtual void create(const std::string &name, short rep, long chunkSize) = 0;
    virtual void rename(const std::string &src, const std::string &dst) = 0;
    virtual void remove(const std::string &src) = 0;
    virtual void mkdir(const std::string &src) = 0;
    virtual void renamedir(const std::string &src, const std::string &dst) = 0;
    virtual void rmdir(const std::string &src) = 0;
    virtual FilesInfo lsdir(const std::string &src) = 0;
    virtual void close(Address client, const std::string &name) = 0;
    virtual LocatedChunks getChunksInfo( Address client, const std::string &name) = 0;
    virtual void updateChunkInfo(LocatedChunk lchk) = 0;
    virtual void completeWrittenChunk(LocatedChunk lchk) = 0;
    virtual LocatedChunk addChunk(Address client, const std::string &name) = 0;
    virtual void abandonAddChunk(Chunk chk) = 0;
    virtual FileInfo getFileInfo(std::string name) = 0;
    virtual ~Master() {}
};

class MockMaster: public Master {
public:
    static Master* instance() {
        if (m_instance == NULL)
            m_instance = new MockMaster();
        return m_instance;
    }
    void create(const std::string &name, short rep, long chunkSize);
    void mkdir(const std::string &name);
    FilesInfo lsdir(const std::string &src);
    void rename(const std::string &src, const std::string &dst);
    void renamedir(const std::string &src, const std::string &dst);
    void remove(const std::string &src);
    void rmdir(const std::string &src);
    void setReplication(const std::string &name, short rep);
    void close(Address client, const std::string &name){}
    LocatedChunks getChunksInfo(Address client, const std::string &name);
    void updateChunkInfo(LocatedChunk lchk);
    void completeWrittenChunk(LocatedChunk lchk){}
    LocatedChunk addChunk(Address client, const std::string &name);
    void abandonAddChunk(Chunk chk){}
    FileInfo getFileInfo(std::string name);
private:
    static Master *m_instance;
    MockMaster() { m_entries[""].flag = FileModeTypeDir; }

    struct Entry {
        std::vector<int64_t> chunks;
        std::set<std::string> children;
        long chunkSize;
        short replication;
        short flag;
        std::string name;
    };
    std::map<std::string, Entry> m_entries;
    std::map<int64_t, LocatedChunk> m_lchks;

    bool exists(const std::string &name);
    bool existsDir(const std::string &name);
    std::string parent_dir(const std::string &path);
    void create_entry(const std::string &name);
    void remove_entry(const std::string &name);
    void rename_entry(const std::string &src, const std::string &dst);

};  // class MockMaster

class RealMaster: public Master {
    public:
        static Master* instance() {
            if (m_instance == NULL)
                m_instance = new RealMaster();
            return m_instance;
        }

        void create(const std::string &name, short rep, long chunkSize) {
            Client::service()->create(name, rep, chunkSize);
        }
        void mkdir(const std::string &name) {
            Client::service()->mkdir(name);
        }
        void rename(const std::string &src, const std::string &dst) {
            Client::service()->rename(src, dst);
        }
        void renamedir(const std::string &src, const std::string &dst) {
            Client::service()->renamedir(src, dst);
        }
        void remove(const std::string &name) {
            Client::service()->remove(name);
        }
        void rmdir(const std::string &name) {
            Client::service()->rmdir(name);
        }
        FilesInfo lsdir(const std::string &name) {
            return Client::service()->lsdir(name);
        }

        void close(Address client, const std::string &name) {
            Client::service()->close(client, name);
        }

        void setReplication(const std::string &name, short rep) {
            Client::service()->setReplication(name, rep);
        }

        LocatedChunks getChunksInfo(Address client, const std::string &name) {
            return Client::service()->getChunksInfo(client, name);
        }

        void updateChunkInfo(LocatedChunk lchk) {
            Client::service()->updateChunkInfo(lchk);
        }

        void completeWrittenChunk(LocatedChunk lchk) {
            Client::service()->completeWrittenChunk(lchk);
        }

        LocatedChunk addChunk(Address client, const std::string &name) {
            return Client::service()->addChunk(client, name);
        }

        void abandonAddChunk(Chunk chk) {
            Client::service()->abandonAddChunk(chk);
        }

        FileInfo getFileInfo(std::string name) {
            return Client::service()->getFileInfo(name);
        }
    private:
        RealMaster() { }
        static Master *m_instance;

};  // class RealMaster

}   // namespace tfs

#endif   /* ----- #ifndef MASTER_FILE_HEADER_INC  ----- */

