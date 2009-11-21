/**
 * @file MonitorServiceImpl.cpp
 * .
 * 
 * @author Zhifeng YANG
 * @date 2007-12-05
 */

#include "master/MonitorServiceImpl.hpp"
#include <algorithm>
using namespace mapreduce;
using namespace std;

// internal helper class
struct TaskDetailLessThan: public std::binary_function<TaskDetail, TaskDetail, bool>
{
    bool operator() (const TaskDetail &task1, const TaskDetail &task2)
        {
            if (task1.type != task2.type){
                return task1.type < task2.type;
            }
            // equal type, then
            if (task1.status != task2.status){
                return task1.status < task2.status;
            }
            // equal type and status ,then
            return task1.taskId < task2.taskId;
        }        
};


int32_t MonitorServiceImpl::getStartTime(const ::Ice::Current&)
{
    return m_db->m_statistics.getStartTime();
}

JobsDetail MonitorServiceImpl::getJobs(const ::Ice::Current&)
{
    JobsDetail details;
    Jobs jobs = m_db->m_jobs.getAllJobs();
    
    /*
    JobDetail detailHack;
    detailHack.id = 123123123;
    details.push_back(detailHack);
    */

    for (int i = 0; i < jobs.size(); i++){
        JobDetail detail;
        detail.id = jobs[i].first;
        detail.createTime = jobs[i].second.createTime;
        detail.finishTime = jobs[i].second.finishTime;
        detail.detached = jobs[i].second.bDetached;
        detail.jobSpec = jobs[i].second.jobSpec;
        detail.status = jobs[i].second.status;
        details.push_back(detail);
    }
    return details;
}

TasksDetail MonitorServiceImpl::getTasks(int32_t jobId, const ::Ice::Current&)
{
    TasksDetail details;
    Tasks tasks = m_db->m_tasks.getAllTasks(jobId);
    /*
    Tasks abandoned = m_db->m_abandonedTasks;
    
    Task task;
    TaskInfo taskinfo;
    taskinfo.type = TT_MAP;
    taskinfo.status = TS_COMPLETED;
    taskinfo.jobId = 123124123;
    taskinfo.abandonCount = 1;
    taskinfo.startTime = 123123123;
    taskinfo.endTime = 123123123;
    taskinfo.worker.name = "222.29.154.21";
    taskinfo.worker.port = 21021;
    taskinfo.chunkIdx = 212323;
    taskinfo.mapTaskId = 0;
    taskinfo.reduceTaskId = 0;
    taskinfo.partition = 0;
    task.first = 123123;
    task.second = taskinfo;

    tasks.push_back(task);

    for(Tasks::iterator iter = abandoned.begin(); iter != abandoned.end(); iter ++){
        tasks.push_back(*iter);
    }
    */

    for (int i = 0; i < tasks.size(); i++){
        TaskDetail detail;
        detail.taskId = tasks[i].first;
        detail.jobId = tasks[i].second.jobId;
        detail.type = tasks[i].second.type;
        detail.status = tasks[i].second.status;
        detail.worker = tasks[i].second.worker;

        // following are optional
        detail.startTime = tasks[i].second.startTime;
        detail.endTime = tasks[i].second.endTime;
        detail.chunkId = tasks[i].second.chunkIdx;
        detail.mapId = tasks[i].second.mapTaskId;
        detail.reduceId = tasks[i].second.reduceTaskId;
        detail.partition = tasks[i].second.partition;

        details.push_back(detail);
    }
    // sort
    std::sort(details.begin(), details.end(), TaskDetailLessThan());
    return details;
}

TasksDetail MonitorServiceImpl::getCurrentRunningTasks(const ::Ice::Current&)
{
    TasksDetail details;
    Tasks tasks = m_db->m_tasks.getAllTasks();

    for (int i = 0; i < tasks.size(); i++){
        TaskDetail detail;
        if(tasks[i].second.status != TS_INPROGRESS)
            continue;
        detail.taskId = tasks[i].first;
        detail.jobId = tasks[i].second.jobId;
        detail.type = tasks[i].second.type;
        detail.status = tasks[i].second.status;
        detail.worker = tasks[i].second.worker;

        // following are optional
        detail.startTime = tasks[i].second.startTime;
        detail.endTime = tasks[i].second.endTime;
        detail.chunkId = tasks[i].second.chunkIdx;
        detail.mapId = tasks[i].second.mapTaskId;
        detail.reduceId = tasks[i].second.reduceTaskId;
        detail.partition = tasks[i].second.partition;

        details.push_back(detail);
    }
    // sort
    std::sort(details.begin(), details.end(), TaskDetailLessThan());
    return details;
}


TasksStatistics MonitorServiceImpl::getTasksStat(int32_t jobId, const ::Ice::Current&)
{
    TasksStatistics stat;
    Tasks tasks = m_db->m_tasks.getAllTasks(jobId);
    int count[4][4];
    memset(count, 0, 16*sizeof(int));
    
    for (int i = 0; i < tasks.size(); i++){
        count[tasks[i].second.type][tasks[i].second.status]++;
    }
    stat.push_back(tasks.size());
    for (int i = 0; i < 4; i++){
        for (int j = 0; j < 4; j++){
            stat.push_back(count[i][j]);
        }
    }
    
    return stat;
}

int32_t MonitorServiceImpl::getCompletedJobs(const ::Ice::Current&)
{
    return m_db->m_statistics.getCompletedJobs();
}
int32_t MonitorServiceImpl::getCompletedTasks(const ::Ice::Current&)
{
    return m_db->m_statistics.getCompletedMapTasks()+
	m_db->m_statistics.getCompletedReduceTasks()+
	m_db->m_statistics.getCompletedTransTasks();
}

WorkersDetail MonitorServiceImpl::getWorkers(const ::Ice::Current&)
{
    WorkersDetail details;
    Workers workers = m_db->m_workers.getWorkers();
    std::sort(workers.begin(), workers.end(), WorkerLessThan());
    
    for (int i = 0; i < workers.size(); i++){
        WorkerDetail detail;
        detail.addr = workers[i].first;
        detail.workload = workers[i].second.m_heartbeat.workload;
        detail.mapDoneCount = workers[i].second.m_doneMapTasks;
        detail.reduceDoneCount = workers[i].second.m_doneReduceTasks;
        detail.transDoneCount = workers[i].second.m_doneTransTasks;
        vector<int32_t> activeTasksNum = m_db->m_tasks.getInproTasksNum(workers[i].first);
        detail.mapTaskCount = activeTasksNum[1];
        detail.reduceTaskCount = activeTasksNum[2];
        detail.transTaskCount = activeTasksNum[3];
        details.push_back(detail);
    }
    return details;
}

