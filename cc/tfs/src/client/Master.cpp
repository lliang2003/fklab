/**
 * @file	Master.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	12/06/08 10:22:19 CST
 *
 */

#include "Master.hpp"
#include <map>
#include <set>
#include <iostream>
using namespace tfs::slice;
using namespace std;

namespace tfs {

Master*  MockMaster::m_instance;
Master*  RealMaster::m_instance;

void MockMaster::create(const string &name, short rep, long chunkSize) {
    LOG_DEBUG("MockMaster: Create " <<name);
    if (chunkSize == 0) chunkSize = 64*1024*1024;
    if (rep == 0) rep = 3;
    if (rep < 0 || rep > 5) throw ReplicationOutOfRange();
    if (chunkSize < 1024*1024 || chunkSize > 128*1024*1024) 
        throw ChunkSizeOutOfRange();
    create_entry(name);
    m_entries[name].chunkSize = chunkSize;
    m_entries[name].replication = rep;
    m_entries[name].flag = 0;
}
void MockMaster::mkdir(const string &name) {
    LOG_DEBUG("MockMaster: Mkdir " <<name);
    create_entry(name);
    m_entries[name].flag = FileModeTypeDir;
}
FilesInfo MockMaster::lsdir(const string &src) {
    LOG_DEBUG("MockMaster: List dir " <<src);
    if (src[0] != '/') throw InvalidFileOrDirName();
    if (m_entries.find(src) == m_entries.end()
            || (m_entries[src].flag & FileModeTypeDir) == 0)
        throw NoSuchFileOrDir();
    FilesInfo infos;
    for (set<string>::iterator it = m_entries[src].children.begin();
            it != m_entries[src].children.end(); ++it)
        infos.push_back(getFileInfo(src + "/" + *it));
    return infos;
}
void MockMaster::rename(const string &src, const string &dst) {
    LOG_DEBUG("MockMaster: Rename " <<src <<" -> " <<dst);
    if (src[0] != '/' || dst[0] != '/') 
        throw InvalidFileOrDirName();
    if (m_entries.find(src) == m_entries.end()
            || (m_entries[src].flag & FileModeTypeDir) == FileModeTypeDir)
        throw NoSuchFileOrDir();
    rename_entry(src, dst);
}
void MockMaster::renamedir(const string &src, const string &dst) {
    LOG_DEBUG("MockMaster: Rename dir " <<src <<" -> " <<dst);
    if (src[0] != '/' || dst[0] != '/')
        throw InvalidFileOrDirName();
    if (dst.substr(0, src.size()) == src && dst[src.size()] == '/') 
        throw InvalidFileOrDirName();
    if (m_entries.find(dst) != m_entries.end())
        throw FileOrDirAlreadyExists();
    if (m_entries.find(src) == m_entries.end()
            || (m_entries[src].flag & FileModeTypeDir) == 0)
        throw NoSuchFileOrDir();
    for (map<string, Entry>::iterator it = ++m_entries.find(src);
            it != m_entries.end();) {
        if (it->first.size() <= src.size() || it->first[src.size()] != '/') break; 
        m_entries[dst+it->first.substr(src.size())] = it->second;
        m_entries.erase(it++);
    }
    rename_entry(src, dst);
}
void MockMaster::remove(const string &src) {
    LOG_DEBUG("MockMaster: Remove " <<src);
    if (src[0] != '/') 
        throw InvalidFileOrDirName();
    if (m_entries.find(src) == m_entries.end()
            || (m_entries[src].flag & FileModeTypeDir) == FileModeTypeDir)
        throw NoSuchFileOrDir();
    for (int i = 0; i < m_entries[src].chunks.size(); ++i)
        m_lchks.erase(m_entries[src].chunks[i]);
    remove_entry(src);
}
void MockMaster::rmdir(const string &src) {
    LOG_DEBUG("MockMaster: Rmdir " <<src);
    if (src[0] != '/') 
        throw InvalidFileOrDirName();
    if (m_entries.find(src) == m_entries.end()
            || (m_entries[src].flag & FileModeTypeDir) == 0)
        throw NoSuchFileOrDir();
    remove_entry(src);
}
void MockMaster::setReplication(const string &name, short rep) {
    if (name[0] != '/') 
        throw InvalidFileOrDirName();
    if (m_entries.find(name) == m_entries.end()
            || (m_entries[name].flag & FileModeTypeDir) == FileModeTypeDir)
        throw NoSuchFileOrDir();
    if (rep == 0) rep = 3;
    if (rep < 0 || rep > 5) throw ReplicationOutOfRange();
    m_entries[name].replication = rep;
}
LocatedChunks MockMaster::getChunksInfo(Address client, const string &name) {
    std::vector<LocatedChunk> vlchks;
    for (int i = 0; i < m_entries[name].chunks.size(); ++i)
        vlchks.push_back(m_lchks[m_entries[name].chunks[i]]);
    return vlchks;
}
void MockMaster::updateChunkInfo(LocatedChunk lchk){
    m_lchks[lchk.chk.id] = lchk;
}
LocatedChunk MockMaster::addChunk(Address client, const string &name){
    LocatedChunk lchk;
    lchk.chk.id = rand();
    lchk.chk.size = 0;
    lchk.chk.version = 0;
    Address addr;
    addr.name = "0.0.0.0";
    addr.port = 0;
    lchk.locations.push_back(addr);
    m_entries[name].chunks.push_back(lchk.chk.id);
    m_lchks[lchk.chk.id] = lchk;
    return lchk;
}
FileInfo MockMaster::getFileInfo(string name) {
    while(name[name.size()-1] == '/')
        name.erase(name.size()-1);
    LOG_DEBUG("MockMaster: getFileInfo " + name);
    if (!exists(name) && !existsDir(name))
        LOG_DEBUG("MockMaster: " + name + " unexists.");
    if (!exists(name) && !existsDir(name)) throw NoSuchFileOrDir();
    FileInfo info;
    info.name = m_entries[name].name;
    info.replication = m_entries[name].replication;
    info.chunkSize = m_entries[name].chunkSize;
    info.flag = m_entries[name].flag;
    info.size = 0;
    for (int i = 0; i < m_entries[name].chunks.size(); ++i)
        info.size += m_lchks[m_entries[name].chunks[i]].chk.size;
    return info;
}
    bool MockMaster::exists(const string &name) {
        return m_entries.find(name) !=  m_entries.end()
            && (m_entries[name].flag & FileModeTypeDir) == 0;
    }
    bool MockMaster::existsDir(const string &name) {
        return m_entries.find(name) !=  m_entries.end()
            && (m_entries[name].flag & FileModeTypeDir) != 0;
    }
string MockMaster::parent_dir(const string &path) {
    int i = path.size() - 2;
    while (i > 0 && path[i] != '/') --i;
    return path.substr(0, i);
}
void MockMaster::create_entry(const string &name) {
    if (name[0] != '/') throw InvalidFileOrDirName();
    if (name.size() > 1 && name[name.size()-1] == '/') throw InvalidFileOrDirName();
    if (exists(name) || existsDir(name)) 
        throw FileOrDirAlreadyExists();
    string pd = parent_dir(name);
    if (exists(pd)) throw FileOrDirAlreadyExists();
    if (!existsDir(pd)) mkdir(pd);
    m_entries[pd].children.insert(name.substr(pd.size()+1));
    m_entries[name].name = name.substr(pd.size()+1);
}
void MockMaster::remove_entry(const string &name) {
    string pd = parent_dir(name);
    m_entries[pd].children.erase(name.substr(pd.size()+1));
    m_entries.erase(name);
}
void MockMaster::rename_entry(const string &src, const string &dst) {
    create_entry(dst);
    string name = m_entries[dst].name;
    m_entries[dst] = m_entries[src];
    m_entries[dst].name = name;
    remove_entry(src);
}
}   //namespace tfs

