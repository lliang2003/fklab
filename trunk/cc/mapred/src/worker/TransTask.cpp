/**
 * @file	TransTask.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	12/08/2007 06:17:08 PM CST
 *
 */

#include "TransTask.hpp"
#include "Worker.hpp"
#include <boost/lexical_cast.hpp>

#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <boost/filesystem.hpp>
#include <ctime>

using namespace mapreduce;
using namespace std;
namespace fs = boost::filesystem;

#define LOG_PRX "TransferTask: "
#define LOG_SFX " job=" <<m_command.jobId <<" task=" <<m_command.taskId

TransTask::TransTask(const MasterCommand &mc, const std::string &jobDir, 
                     Worker *worker) {
    m_command = mc;
    m_jobDir = jobDir;
    m_worker = worker;
}

int TransTask::readn(int sockfd, char *p, int n) {
    int nleft = n;
    int nread;
    while (nleft > 0) {
        if ((nread = read(sockfd, p, nleft)) < 0) {
            if (errno == EINTR) {
                LOG_ERROR(LOG_PRX <<"Read socket error, errno = EINTR, continue." <<LOG_SFX);
                nread = 0;
            } else {
                LOG_ERROR(LOG_PRX <<"Read socket error, errno = " <<errno <<LOG_SFX);
                return -1;
            }
        } else if (nread == 0) {
            //LOG_ERROR(LOG_PRX <<"Read socket error, EOF reached." <<LOG_SFX);
            break;
        }
        nleft -= nread;
        p += nread;
    }
    return n - nleft;
}

void TransTask::run() {
    LOG_INFO(LOG_PRX <<"StartThread type=Transfer tid=" <<syscall(SYS_gettid) <<LOG_SFX);
    clock_t t = clock();
    char tmp[1024*1024];
    int sockfd;
    struct sockaddr_in addr;
    int size = -1, n = 0, m;
    ofstream fout;
    string taskDir, outfile;
    if (m_worker == NULL) goto exit_run;

    // Local file
    if (m_command.transMapWorker == m_worker->address()) {
        string file = m_jobDir + "/" 
                + boost::lexical_cast<string>(m_command.transMapTaskId)
                + "/" + boost::lexical_cast<string>(m_command.partition);
        if (fs::exists(file + "_combined"))
            file += "_combined";
        LOG_INFO(LOG_PRX <<"AddLocal file=" <<file <<LOG_SFX);
        m_worker->addMappedFile(m_command.jobId, m_command.partition, file);
        m_worker->completeTask(m_command.jobId, m_command.taskId);
        goto exit_run;
    }

    // Remote file.
    taskDir = m_jobDir + "/" + boost::lexical_cast<string>(m_command.partition);
    if (!fs::exists(taskDir)) {
        fs::create_directory(taskDir);
        if(!fs::is_directory(taskDir)) {
            LOG_ERROR(LOG_PRX <<"Error type=CreateDir dir=" <<taskDir <<LOG_SFX);
            //IceUtil::ThreadControl::sleep(IceUtil::Time::seconds(10));
            m_worker->abandonTask(m_command.jobId, m_command.taskId);
            goto exit_run;
        }

        LOG_DEBUG(LOG_PRX <<"Done type=CreateDir dir=" <<taskDir <<LOG_SFX);
    }
    outfile = taskDir + "/" 
        + boost::lexical_cast<string>(m_command.transMapTaskId) + "_"
        + m_command.transMapWorker.name + ":"
        + boost::lexical_cast<string>(m_command.transMapWorker.port);
    LOG_DEBUG(LOG_PRX <<"Try type=Get path=" <<outfile <<" addr=" 
              <<m_command.transMapWorker.name <<":" <<m_command.transMapWorker.port);

    // Connect transfer server.
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) {
        LOG_ERROR(LOG_PRX <<"Error type=GetSocket errno=" <<errno <<LOG_SFX);
        m_worker->abandonTask(m_command.jobId, m_command.taskId);
        goto exit_run;
    }

    bzero(&addr, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(short(m_command.transMapWorker.port));
    inet_pton(AF_INET, m_command.transMapWorker.name.c_str(), &addr.sin_addr);

    //LOG_DEBUG(LOG_PRX <<"Connecting ..." <<LOG_SFX); 
    if (connect(sockfd, (sockaddr*)&addr, sizeof(addr)) < 0) {
        LOG_ERROR(LOG_PRX <<"Error type=Connect errno=" <<errno <<LOG_SFX);
        m_worker->abandonTask(m_command.jobId, m_command.taskId);
        close(sockfd);
        goto exit_run;
    }
    //LOG_DEBUG(LOG_PRX <<"Connect over." <<LOG_SFX); 

    // Specify the file to be transferred.
    if (write(sockfd, &m_command.jobId, sizeof(int)) < sizeof(int) 
           || write(sockfd, &m_command.transMapTaskId, sizeof(int)) < sizeof(int)
           || write(sockfd, &m_command.partition, sizeof(int)) < sizeof(int)) {
        LOG_ERROR(LOG_PRX <<"Error type=Write errno=" <<errno <<LOG_SFX);
        m_worker->abandonTask(m_command.jobId, m_command.taskId);
        close(sockfd);
        goto exit_run;
    }
    //LOG_DEBUG(LOG_PRX <<"Write file info over" <<LOG_SFX);

    fout.open(outfile.c_str(), ios::binary);
    if (!fout.is_open()) {
        LOG_ERROR(LOG_PRX <<"Error type=Open " <<outfile <<LOG_SFX);
        m_worker->abandonTask(m_command.jobId, m_command.taskId);
        close(sockfd);
        goto exit_run;
    }

    // Get file.
    //LOG_DEBUG(LOG_PRX <<"Try to get file size." <<LOG_SFX);
    if (readn(sockfd, (char *)&size, sizeof(int)) < sizeof(int)) {
        LOG_ERROR(LOG_PRX <<"Error type=GetSize" <<LOG_SFX);
        m_worker->abandonTask(m_command.jobId, m_command.taskId);
        close(sockfd);
        goto exit_run;
    }
    //LOG_DEBUG(LOG_PRX <<"Get file size " <<size <<LOG_SFX);

    if (size == -1) {
        LOG_ERROR(LOG_PRX <<"Error type=AbnormalSize" <<LOG_SFX);
        m_worker->abandonTask(m_command.jobId, m_command.taskId);
        close(sockfd);
        goto exit_run;
    }

    while ((m = readn(sockfd, tmp, 1024*1024)) > 0) {
        n += m;
        fout.write(tmp, m);
    } 
    //LOG_DEBUG(LOG_PRX <<"Transfer over." <<LOG_SFX);

    if (n == size) {
        LOG_INFO(LOG_PRX <<"Done bytes=" <<n <<" path=" <<outfile <<" seconds=" 
                <<(clock() - t) / CLOCKS_PER_SEC <<LOG_SFX);
        m_worker->addMappedFile(m_command.jobId, m_command.partition, outfile);
        m_worker->completeTask(m_command.jobId, m_command.taskId);
    } else {
        LOG_ERROR(LOG_PRX <<"Error type=SizeError get=" <<n <<"expect="
                  <<size <<LOG_SFX); 
        m_worker->abandonTask(m_command.jobId, m_command.taskId);
    }
    close(sockfd);

exit_run:
    LOG_INFO(LOG_PRX <<"FinishThread tid=" <<syscall(SYS_gettid) <<LOG_SFX);
}
