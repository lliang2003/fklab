/**
 * @file	MapTask.cpp
 *  Implementation of MapTask class.	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	12/08/2007 06:19:37 PM CST
 *
 */
#include "Worker.hpp"
#include "MapTask.hpp"
#include "mapred/SetMapper.hpp"
#include "mapred/Sorter.hpp"
#include "mapred/SetReducer.hpp"
#include <tfs/tfs.h>
#include <tianwang/Transfer.h>

#include <Ice/Ice.h>
#include <util/Config.hpp>
#include <boost/filesystem.hpp>
#include <boost/lexical_cast.hpp>
#include <fstream>
#include <signal.h>
#include <sys/wait.h>
#include <sstream>
#include <boost/scoped_array.hpp>
#include <stdio.h>
#include <errno.h>

using namespace mapreduce;
using namespace tfs::api;
using namespace std;

namespace fs = boost::filesystem;

#define LOG_PRX "MapTask: "
#define LOG_SFX " job=" <<m_command.jobId <<" task=" <<m_command.taskId

MapTask::MapTask(const TaskConfigure &conf, 
        const MasterCommand &mc, const string &taskDir, Worker *worker) {
    m_command = mc;
    m_taskDir = taskDir;
    m_worker = worker;
    m_conf = conf;
}

bool MapTask::getFile(const MasterCommand &mc, string &path) {
    LOG_DEBUG(LOG_PRX <<"GetChunk chunkid=" <<mc.chunkID <<LOG_SFX);
    //tfs::ChunkTransfer ct;
    /**
     * add by Chen Rishan
     * the module of datatransfer had been replaced of ice
     */
    try {
        Ice::CommunicatorPtr ic = Ice::initialize();
        Ice::ObjectPrx base = ic->stringToProxy("Transfer:tcp -h " 
                + mc.chunkLocation.name + " -p " 
                + boost::lexical_cast<string>(mc.chunkLocation.port) );
        tfs::slice::TransferPrx tp = tfs::slice::TransferPrx::checkedCast(base);

        if (mc.chunkLocation.name == m_worker->address().name) {
            path = tp->getLocalPath(mc.chunkID);
            LOG_INFO(LOG_PRX <<"MapLocalFile path=" <<path <<LOG_SFX);
        } else {
            path = m_taskDir + "_" + boost::lexical_cast<string>(mc.chunkID) + ".chk.tmp";
            LOG_INFO(LOG_PRX <<"GetRemoteFile src=" <<mc.chunkLocation.name
                    <<":" <<mc.chunkLocation.port <<" localpath=" <<path <<LOG_SFX);

            // Get remote file
            ofstream fout(path.c_str(), ios::binary);

            int n = 500000;
            for (int i = 0; i < (mc.chunkSize-1) / n + 1; ++i) {
                int nleft = mc.chunkSize - n * i;
                vector<unsigned char> vc = tp->read(mc.chunkID, n * i, min(n, nleft));
                assert(vc.size() == min(n, nleft));
                fout.write((const char *)&vc[0], vc.size());
                if (!fout.good()) {
                    LOG_ERROR(LOG_PRX <<"Error type=GetRemoteFile localpath=" <<path <<LOG_SFX);
                    return false;
                }
            }
            fout.close();
            LOG_DEBUG(LOG_PRX <<"Get map file done.")
        }
        ic->destroy();
    } catch (const Ice::Exception &ex) {
        LOG_ERROR(LOG_PRX <<"Error type=GetChunk chunkid=" <<mc.chunkID <<LOG_SFX);
        return false;
    }
    return true;
}

void MapTask::map(const string &path, const string &m_taskDir) {
    try {
        Logger::getInstance("mapred_worker").removeAllAppenders();
        helpers::LogLog::getLogLog()->setInternalDebugging(false);
        SharedAppenderPtr append_1(
                new RollingFileAppender(m_taskDir + ".log", 4 * 1024 * 1024, 5));
        append_1->setName(m_taskDir);
        std::string pattern = "%D %p - %m\n";
        append_1->setLayout(std::auto_ptr<Layout>(new PatternLayout(pattern)));
        Logger::getInstance("mapred_worker").addAppender(append_1);

        vector<string> vs;

        LOG_DEBUG(LOG_PRX <<"Set mapper with reduceTaskNum = " 
                <<m_command.reduceTaskNum);
        for (int i = 0; i < m_command.reduceTaskNum; ++i) {
            string name = m_taskDir + "/" + boost::lexical_cast<string>(i);
            vs.push_back(name);
        }

        LOG_DEBUG(LOG_PRX <<"Begin mapping." <<LOG_SFX);
        SetMapper *sm = new SetMapper(m_conf, path, vs);
        int r = sm->map();
        LOG_DEBUG(LOG_PRX <<"End mapping." <<LOG_SFX);

        if (r < 0) {
            LOG_ERROR(LOG_PRX <<"Failed to map." <<LOG_SFX);
            exit(1);
        } else {
            LOG_INFO(LOG_PRX <<"Map " <<r <<" records." <<LOG_SFX);
        } 
        delete sm;

        // Do local combine.
        if (m_command.doLocalCombine) {
            LOG_INFO(LOG_PRX <<"Begin local combination." <<LOG_SFX);
            vector<string> vf;
            for (int i = 0; i < vs.size(); ++i) {
                // Sort
                vf.clear();
                vf.push_back(vs[i]);
                string sfile = vs[i] + "_sorted";
                Sorter s(m_conf, sfile, vf);
                if (!s.sort()) {
                    LOG_ERROR(LOG_PRX <<"Sort failed in local combination." <<LOG_SFX);
                    exit(1);
                }

                // Reduce
                string outfile = vs[i] + "_combined";
                SetReducer sr(m_conf, sfile, outfile, true, true);
                if (sr.reduce() < 0) {
                    LOG_ERROR(LOG_PRX <<"Reduce failed in local combination." <<LOG_SFX);
                    exit(1);
                }

                // Delete original and intermediate file.
                fs::remove(sfile);
                //fs::remove(vs[i]);
            }
            // LOG_INFO(LOG_PRX <<"End local combination." <<LOG_SFX);
        }
        LOG_DEBUG(LOG_PRX <<"Map completed." <<LOG_SFX);
        exit(0); 
    } catch (exception &ex) {
        LOG_ERROR(LOG_PRX <<"Catch " <<ex.what() <<endl)
            exit(-1);
    } catch (...) {
        LOG_ERROR(LOG_PRX <<"Catch unknown exception")
            exit(-1);
    }
}

void MapTask::run() {
    LOG_INFO("Worker: StartThread type=Map tid=" <<syscall(SYS_gettid) <<LOG_SFX)
    string path;

    // Prepare source file.
    bool flag = getFile(m_command, path);
    if (!flag) {
        LOG_ERROR(LOG_PRX <<"Can not get map file, abandon task." <<LOG_SFX);
        if (m_worker != NULL)
            m_worker->abandonTask(m_command.jobId, m_command.taskId);
        return;
    }

    if (m_command.doLocalCombine) {
        LOG_DEBUG(LOG_PRX <<"Will do local combination." <<LOG_SFX);
    }

    int nretry = 0;
    int nsec;
    // Begin map.
    for (int round = 0; round < nretry + 1; ++round) {
        nsec = 0;
        if (fs::exists(m_taskDir)) {
            fs::remove_all(m_taskDir);
            LOG_DEBUG("RemoveDir dir=" <<m_taskDir <<LOG_SFX);
        }

        fs::create_directory(fs::path(m_taskDir));

        if(!fs::is_directory(fs::path(m_taskDir))){
            LOG_ERROR(LOG_PRX <<"Error type=CreateDir dir=" <<m_taskDir <<LOG_SFX);
            IceUtil::ThreadControl::sleep(IceUtil::Time::seconds(10));
            continue;
        }

        int pid = fork();
        int r, exitcode;
        if (pid == 0) {
            // child process

            string confString;
            m_conf.writeToString(confString);

            //char pathBuffer[512];
            //char* currentPath = getcwd(pathBuffer, 512);
            //string execFileName = string(currentPath)  + "/bin/mapred_mapper";
            
            string execFileName = m_worker->getHomeDir() + "/mapred_mapper";
            string logFileName = m_taskDir + "/../" + boost::lexical_cast<string>(m_command.jobId);

            LOG_INFO(LOG_PRX <<"Exec maptask as :" << execFileName <<LOG_SFX);
            LOG_INFO(LOG_PRX <<"Exec maptask dataFile as :" << path <<LOG_SFX);
            LOG_INFO(LOG_PRX <<"Exec maptask taskDir as :" << m_taskDir <<LOG_SFX);
            LOG_INFO(LOG_PRX <<"Exec maptask confString as :" << confString <<LOG_SFX);
            LOG_INFO(LOG_PRX <<"Exec maptask reduceNum as :" << m_command.reduceTaskNum << " localCombine as" <<
                    m_command.doLocalCombine);
            
            //test fork-safety for PageLen test
            int ret = execl(execFileName.c_str(), execFileName.c_str(),
                path.c_str(),m_taskDir.c_str(),confString.c_str(),
                (boost::lexical_cast<string>(m_command.reduceTaskNum)).c_str(),
                (m_command.doLocalCombine?"1":"0"),
                logFileName.c_str(),
                NULL);

            if(ret == -1){
                perror("MapRed-MapTask");
            }
            return;
        } else if(pid < 0){
            LOG_ERROR(LOG_PRX <<"Error type=Fork" <<LOG_SFX);
            IceUtil::ThreadControl::sleep(IceUtil::Time::seconds(10));
            continue;
        } else {
            LOG_INFO(LOG_PRX <<"Fork pid=" <<pid <<LOG_SFX);
            while (true) {
                // check process status
                if ((r = waitpid(pid, &exitcode, WNOHANG)) < 0) {
                    LOG_ERROR(LOG_PRX <<"Error type=Wait pid=" <<pid <<" return=" <<r <<LOG_SFX);
                    kill(pid, 9);
                    waitpid(pid, NULL, 0);
                    break;
                } else if (r > 0) {
                    if (WIFEXITED(exitcode) && (WEXITSTATUS(exitcode) == 0)) {
                        LOG_DEBUG(LOG_PRX <<"MapDone seconds=" <<nsec <<LOG_SFX);
                        // Remove chunk file
                        if (path.substr(path.length() - 3, 3) == "tmp") {
                            LOG_DEBUG(LOG_PRX <<"RemoveFile path=" <<path <<LOG_SFX);
                            fs::remove(path);
                        }
                        if (m_worker != NULL)
                            m_worker->completeTask(m_command.jobId, m_command.taskId);
                        goto exit;
                    } else {
                        if (!WIFEXITED(exitcode)) {
                            LOG_ERROR(LOG_PRX <<"Error type=WaitAbnormalExit pid=" <<pid <<LOG_SFX);
                        } else {
                            LOG_ERROR(LOG_PRX <<"Error type=WaitReturnError pid=" <<pid <<" exitcode=" <<WEXITSTATUS(exitcode));
                        }
                    }
                    break;
                }

                // check whether this task is abandoned
                if (m_worker->killed(m_command.taskId)) {
                    LOG_INFO(LOG_PRX <<"JobCancelled " <<LOG_SFX);
                    kill(pid, 9);
                    waitpid(pid, NULL, 0);
                    if (m_worker != NULL)
                        m_worker->abandonTask(m_command.jobId, m_command.taskId);
                    return;
                }
                //sleep(1);
                IceUtil::ThreadControl::sleep(IceUtil::Time::seconds(1));
                ++ nsec;

                if (nsec % 60 == 0) {
                    LOG_DEBUG(LOG_PRX <<"LongMap wait reach " <<nsec / 60 << " minutes for pid=" << pid << " taskDir=" << m_taskDir <<LOG_SFX);
                }
                if (nsec > 20 * 60) {
                    LOG_WARN(LOG_PRX <<"Timeout type=Map seconds=" <<nsec <<LOG_SFX);
                    kill(pid, 9);
                    waitpid(pid, NULL, 0);
                    break;
                }
            }
        }
    }
    LOG_INFO(LOG_PRX <<"AbandonTask retry=" <<nretry <<LOG_SFX);
    if (m_worker != NULL)
        m_worker->abandonTask(m_command.jobId, m_command.taskId);
exit:
    LOG_INFO(LOG_PRX <<"FinishThread tid=" <<syscall(SYS_gettid) <<LOG_SFX);
}
