/**
 * @file	TransFile.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	12/10/2007 02:00:22 PM CST
 *
 */

#include "Worker.hpp"
#include "TransFile.hpp"
#include <fstream>
#include <ctime>
#include <pthread.h>
#include <sys/syscall.h>
using namespace std;

namespace mapreduce {

#define LOG_PRX "TransFile: "
#define LOG_SFX " trans=" <<m_id

TransFile::TransFile(const string file, int socketfd, int id) {
    m_filename = file;
    m_socketfd = socketfd;
    m_id = id;
}

int TransFile::writen(int sockfd, char *p, int n) {
    int nleft = n, nwritten;
    while (nleft > 0) {
        if ((nwritten = write(sockfd, p, nleft)) <= 0) {
            if (nwritten < 0 && errno == EINTR) {
                LOG_ERROR(LOG_PRX <<"Write socket error, errno = EINTR, continue.");
                nwritten = 0;
            } else {
                LOG_ERROR(LOG_PRX <<"Write socket error, errno = EINTR, continue.");
                return -1;
            }
        }
        nleft -= nwritten;
        p += nwritten;
    }
    return n;
}

void TransFile::run() {
    LOG_INFO("Worker: StartThread type=TransFile tid=" <<syscall(SYS_gettid) <<LOG_SFX);
    clock_t t = clock();
    int n = 0;
    char tmp[1024*1024];
    int size;
    ifstream fin(m_filename.c_str(), ios::binary);
    if (!fin.is_open()) {
        LOG_ERROR(LOG_PRX <<"Error type=Open path=" << m_filename <<LOG_SFX);
        size = -1;
        writen(m_socketfd, (char *)&size, sizeof(int));
        goto exit;
    }

    fin.seekg(0, ios::end);
    size = fin.tellg();
    if (writen(m_socketfd, (char *)&size, sizeof(int)) != sizeof(int)) {
        LOG_ERROR(LOG_PRX <<"Error type=Write" <<LOG_SFX);
        goto exit;
    }
    fin.seekg(0, ios::beg);
    while(fin.read(tmp, 1024*1024), fin.gcount() > 0) {
        if (writen(m_socketfd, tmp, fin.gcount()) != fin.gcount() ) {
            LOG_ERROR(LOG_PRX <<"Error type=Write" <<LOG_SFX);
            goto exit;
        }
        n += fin.gcount();
    }
    LOG_INFO(LOG_PRX <<"Done bytes=" <<n <<" seconds=" 
            <<(clock() - t) / CLOCKS_PER_SEC <<LOG_SFX);

exit:
    close(m_socketfd);
    fin.close();
    LOG_INFO(LOG_PRX <<"FinishThread tid=" <<syscall(SYS_gettid) <<LOG_SFX);
}

}   // namespace mapreduce
