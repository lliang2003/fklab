/**
 * @file Monitor.cpp
 * A application to display system information using MonitorService. 
 * 
 * @author Zhifeng YANG
 * @date 2007-12-05
 */
#include "common/IceGenerated.hpp"
#include "common/AddressHelper.hpp"
#include "master/TaskInfo.hpp"
#include "Ice/Ice.h"
#include <rude/config.h>
#include <iostream>
#include <iomanip>
#include <string>
#include <sstream>
#include <cstring>
using namespace std;
using namespace mapreduce;

const int ShowDetailThreshold = 1000;

void usage()
{
    cout << "TFS/MapRed Version 0.1" << endl;
    cout << "   Usage: mapred_monitor --jobid=jobId" << endl << endl;
    cout << "   monitor the mapreduce cluster for jobs and tasks." << endl << endl;
    cout << "       --jobid     : get information of this job only." << endl << endl;
    
}

bool setFromFile(const string& path, string& ip, string& port, string& homeDir, string& userName)
{ 
    rude::Config conf;
    if (!conf.load(path.c_str())){
        cout << "LoadConfigurationError: load" << path << "failed!" << endl;
        return false;
    }
    conf.setSection("mapred master");
    ip = conf.getStringValue("master_ip");
    port = conf.getStringValue("master_port");

    conf.setSection("user");
    homeDir = conf.getStringValue("home_dir");
    userName = conf.getStringValue("user_name");
    return (ip != "" && port != "" && homeDir != "" && userName != "");
}

int loadMasterConfigure(string& ip, string& port, string& homeDir, string& userName)
{
    string homedir = getenv("HOME");
    if (!setFromFile("tfs_client.ini", ip, port, homeDir, userName) 
        && !setFromFile(homedir + "/.tfs_client.ini", ip, port, homeDir, userName)
        && !setFromFile("/etc/tfs_client.ini", ip , port, homeDir, userName)) {
            
        cout << "LoadConfigurationError: load tfs_client.ini error." << endl;
        return -1;
    }
    return 0;
}


int main(int argc, char** argv)
{
    int jobId = 0;
    if( argc ==2 && !strncmp(argv[1], "--jobid=", strlen("--jobid=")) ){
        jobId = atoi( argv[1] + strlen("--jobid=") );
    }
    else if( argc != 1){
        usage();
        return -1;
    }
    
    // Get configuration.
    string ip, port, homeDir, userName;
    if(loadMasterConfigure(ip, port, homeDir, userName)){
         return -1;
    }
    
    stringstream ss;
    ss << "MonitorService:tcp -h " << ip << " -p " << port;
    cout << ss.str() << endl;
    Ice::CommunicatorPtr ic = NULL;
    try{
        // Initialize ice.
        ic = Ice::initialize();
        Ice::ObjectPrx base = ic->stringToProxy(ss.str());
        MonitorServicePrx m_service = MonitorServicePrx::checkedCast(base);
        int startTime = m_service->getStartTime();
        cout << "Start Time: " << ctime((time_t*)&startTime);
        time_t now = time(NULL);
        cout << "Current Time: " << ctime(&now);
        int nCompletedJobs = m_service->getCompletedJobs();
        cout << "Completed Jobs: " << nCompletedJobs << endl;
        int nCompletedTasks = m_service->getCompletedTasks();
        cout << "Completed Tasks: " << nCompletedTasks << endl;
        cout << endl;
        
        WorkersDetail workers = m_service->getWorkers();
        cout << "All workers[" << workers.size() << "]: " << endl;
        cout << "NO." << "\t" << "ADDRESS" 
		 << "\t\t\t" << "WORKLOAD"
		 << "\t" << "#MAP"
		 << "\t" << "#RDC"
		 << "\t" << "#TRN"
		 << "\t" << "##doneMAP"
		 << "\t" << "##doneRDC"
		 << "\t" << "##doneTRN"
		 << endl;

        for (int i = 0; i < workers.size(); i++){
            cout << i << "\t" << workers[i].addr 
		 << "\t" << workers[i].workload
		 << "\t" << workers[i].mapTaskCount 
		 << "\t" << workers[i].reduceTaskCount 
		 << "\t" << workers[i].transTaskCount
		 << "\t" << workers[i].mapDoneCount 
		 << "\t" << workers[i].reduceDoneCount 
		 << "\t" << workers[i].transDoneCount 
		 << endl;
        }
        JobsDetail jobs = m_service->getJobs();
        
        cout << "=====================================================" << endl;
        cout << "Running Tasks:" << endl;
        TasksDetail runningTasks = m_service->getCurrentRunningTasks();
        for(int j = 0; j < runningTasks.size(); j ++){
            cout << runningTasks[j].taskId << "\t" << runningTasks[j].jobId << "\t"; 
            switch(runningTasks[j].status){
                case 0:
                {    
                    cout << setw(11) << "IDLE\t";
                    break;
                }
                case 1:
                {    
                    cout << setw(11) << "INPR\t";
                    break;
                }
                case 2:
                {    
                    cout << setw(11) << "COMP\t";
                    break;
                }
                case 3:
                {    
                    cout << setw(11) << "CNCL\t";
                    break;
                }
            }
            switch(runningTasks[j].type){
                case 0:
                {    
                    cout << setw(7) << "MAP\t";
                    break;
                }
                case 1:
                {    
                    cout << setw(7) << "TRN\t";
                    break;
                }
                case 2:
                {    
                    cout << setw(7) << "RDC\t";
                    break;
                }
                case 3:
                {    
                    cout << setw(7) << "CLN\t";
                    break;
                }
            }//end of switch
            time_t start = (time_t)runningTasks[j].startTime;
            time_t end = (time_t)runningTasks[j].endTime;
            cout << "\t" << runningTasks[j].worker << endl;
            cout << "   start: " << ctime(&start);
            cout << "   end: " << ctime(&end);
            cout << "   chunk: " << runningTasks[j].chunkId << endl;
            cout << "   from: " << runningTasks[j].mapId << " to: " << runningTasks[j].reduceId 
                 << " partition: " << runningTasks[j].partition << endl << endl;
        }
        cout << "=====================================================" << endl;


        cout << "All Jobs[" << jobs.size() << "]: " << endl;
        for (int i = 0; i < jobs.size(); i++){

            //check if jobId match
            if(jobId && jobs[i].id != jobId) continue;
            
            cout << jobs[i].id << " ";
            switch(jobs[i].status){
            case 0:
            {    
                cout << setw(10) << "IDLE ";
                break;
            }
            case 1:
            {    
                cout << setw(10) << "INPR ";
                break;
            }
            case 2:
            {    
                cout << setw(10) << "COMP ";
                break;
            }
            case 3:
            {    
                cout << setw(10) << "CNCL ";
                break;
            }
            }
            cout << (jobs[i].detached?"Detached":"NotDetached") << " ";
            cout << jobs[i].jobSpec.inputFile << " "
                 << jobs[i].jobSpec.outputFile << " "
                 << jobs[i].jobSpec.soFile << " "
                 << jobs[i].jobSpec.mapTaskNum << " "
                 << jobs[i].jobSpec.reduceTaskNum << " "
                 << (jobs[i].jobSpec.doLocalCombine?"LocalCombine":"NotLocalCombine") << " ";
            struct tm* t = localtime((time_t*)&(jobs[i].createTime));
            cout << t->tm_mon + 1 << "-" << t->tm_mday << " " << t->tm_hour << ":" << t->tm_min << ":" << t->tm_sec << " ";

            if (jobs[i].finishTime == 0){
                cout << "NotFinish" << " ";
            }
            else{
                t = localtime((time_t*)&(jobs[i].finishTime));
                cout << t->tm_mon + 1 << "-" << t->tm_mday << " " << t->tm_hour << ":" << t->tm_min << ":" << t->tm_sec << " ";
            }
            cout << endl;
            
            cout << "All tasks for job " << jobs[i].id << " : " << endl;
            TasksStatistics stat = m_service->getTasksStat(jobs[i].id);
            for (int m = 0 ; m < 4; m++){
                for (int n = 0; n < 4; n++){
                    switch(m){
                    case 0:
                    {    
                        cout << setw(7) << "MAP\t";
                        break;
                    }
                    case 1:
                    {    
                        cout << setw(7) << "TRN\t";
                        break;
                    }
                    case 2:
                    {    
                        cout << setw(7) << "RDC\t";
                        break;
                    }
                    case 3:
                    {    
                        cout << setw(7) << "CLN\t";
                        break;
                    }
                    }
                    
                    switch(n){
                    case 0:
                    {    
                        cout << setw(11) << "IDLE\t";
                        break;
                    }
                    case 1:
                    {    
                        cout << setw(11) << "INPR\t";
                        break;
                    }
                    case 2:
                    {    
                        cout << setw(11) << "COMP\t";
                        break;
                    }
                    case 3:
                    {    
                        cout << setw(11) << "CNCL\t";
                        break;
                    }
                    }
                    cout << stat[m*4+n+1] << endl;
                } // end for
            }     // end for

            cout << setw(18) << "TOTAL Tasks: " << stat[0] << endl;
            if (stat[0] > ShowDetailThreshold){
                continue;
            }
            
            cout << "come on now" << endl;
            TasksDetail tasks = m_service->getTasks(jobs[i].id);
            cout << "taskId" << "\t" << "jobId" << "\t" << "status" << "\t" << "type" << "\t" 
                 << "worker" << "\t" << "startTIme" << "\t" << "endTime" << "\t" << "chunkId" << "\t" 
                 << "mapId" << "\t" << "reduceId" << "\t" << "partition" << endl;
            for (int j = 0; j < tasks.size(); j++){
                cout << tasks[j].taskId << "\t" << tasks[j].jobId << "\t"; 
                switch(tasks[j].status){
                case 0:
                {    
                    cout << setw(11) << "IDLE\t";
                    break;
                }
                case 1:
                {    
                    cout << setw(11) << "INPR\t";
                    break;
                }
                case 2:
                {    
                    cout << setw(11) << "COMP\t";
                    break;
                }
                case 3:
                {    
                    cout << setw(11) << "CNCL\t";
                    break;
                }
                }
                switch(tasks[j].type){
                case 0:
                {    
                    cout << setw(7) << "MAP\t";
                    break;
                }
                case 1:
                {    
                    cout << setw(7) << "TRN\t";
                    break;
                }
                case 2:
                {    
                    cout << setw(7) << "RDC\t";
                    break;
                }
                case 3:
                {    
                    cout << setw(7) << "CLN\t";
                    break;
                }
                }
                time_t start = (time_t)tasks[j].startTime;
                time_t end = (time_t)tasks[j].endTime;
                cout << "\t" << tasks[j].worker << endl;
                cout << "   start: " << ctime(&start);
                cout << "   end: " << ctime(&end);
                cout << "   chunk: " << tasks[j].chunkId << endl;
                cout << "   from: " << tasks[j].mapId << " to: " << tasks[j].reduceId 
                     << " partition: " << tasks[j].partition << endl << endl;
            }
        }
        
    } catch (Ice::Exception &ex) {
        cerr << "ERROR: " << ex << endl;
        if (ic){
            ic->destroy();
        }
        return -1;
    }
    if (ic){
        ic->destroy();
    }
    return 0;
}
