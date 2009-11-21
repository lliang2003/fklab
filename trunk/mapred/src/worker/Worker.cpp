/**
 * @file	Worker.cpp
 *  Implementation of Worker class.	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	12/04/2007 12:00:52 AM CST
 *
 */

#include "Worker.hpp"
#include "TransServer.hpp"
#include "MapTask.hpp"
#include "ReduceTask.hpp"
#include "TransTask.hpp"
#include "mapred/TaskConfigure.hpp"
#include <tfs/tfs.h>

#include <util/Config.hpp>

#include <boost/filesystem.hpp>
#include <boost/lexical_cast.hpp>
#include <sstream>
#include <ctime>

using namespace std;
using namespace mapreduce;
namespace fs = boost::filesystem;

class MyIceLogger: virtual public Ice::Logger{
    ::Logger m_logger;          // log4cplus logger
public:
    MyIceLogger()
        :m_logger(::Logger::getInstance("mapred_worker"))
        {
        }

    void print(const std::string& message)
        {
            LOG4CPLUS_INFO(m_logger, "Ice print " << message);
        }
    void trace(const std::string& category, const std::string& message)
        {
            LOG4CPLUS_TRACE(m_logger, "Ice trace " << category << ":" << message);
        }
    void warning(const std::string& message)
        {
            LOG4CPLUS_WARN(m_logger, "Ice warning " << message);
        }
    void error(const std::string& message)
        {
            LOG4CPLUS_ERROR(m_logger, "Ice error " << message);
        }
};

// Define static member m_name
string Worker::m_name;

string Worker::name() { return m_name; }

Worker::Worker(const string &iniFile, const std::string& homeDir) :m_ic(NULL) {

    // Set home directory
    m_homeDir = homeDir;
    
    // Get configuration.
    Config conf;
    if (!conf.load(iniFile.c_str())) {
        cerr <<"Worker: Can not load config file" <<endl;
    }
    conf.setSection("master");
    string ip = conf.getStringValue("master_ip");
    string port = conf.getStringValue("master_port");
    conf.setSection("worker");
    m_storeDir = conf.getStringValue("store_dir");
    m_logDir = conf.getStringValue("log_dir");
    if (m_storeDir[m_storeDir.size() - 1] != '/') m_storeDir.append("/");
    if (m_logDir[m_logDir.size() - 1] != '/') m_logDir.append("/");
    // Init logger    
    initLogger("mapred_worker", m_logDir);
    if (m_storeDir[0] == '.') {
        char tmp[1000];
        m_storeDir.erase(0, 1);
        m_storeDir.insert(0, getcwd(tmp, sizeof(tmp)));
    }
    m_address.name = conf.getStringValue("local_ip");
    m_address.port = conf.getIntValue("listen_port");
    m_name = conf.getStringValue("name");
    if (m_name == "") m_name = m_address.name;
    string logLevel = conf.getStringValue("log_level");
    Logger logger = Logger::getInstance("mapred_worker");
    if (logLevel == "FATAL") logger.setLogLevel(FATAL_LOG_LEVEL);
    if (logLevel == "ERROR") logger.setLogLevel(ERROR_LOG_LEVEL);
    if (logLevel == "WARN") logger.setLogLevel(WARN_LOG_LEVEL);
    if (logLevel == "INFO") logger.setLogLevel(INFO_LOG_LEVEL);
    if (logLevel == "DEBUG") logger.setLogLevel(DEBUG_LOG_LEVEL);

    if (strlen(conf.getError()) > 0) {
        LOG_ERROR("Worker: Parse config file error.");
    }
    LOG_INFO("Worker: GetConf MasterAddress=" << ip << ":" << port);
    LOG_INFO("Worker: GetConf StorageDirectory=" + m_storeDir);
    LOG_INFO("Worker: GetConf ListenAddress=" + m_address.name 
            + ":" + boost::lexical_cast<string>(m_address.port));
    LOG_INFO("Worker: GetConf LogLevel=" <<logLevel);

    // Initialize ice.
    Ice::PropertiesPtr props = Ice::createProperties();
    // retry three times
    props->setProperty("Ice.RetryIntervals", "0 100 500");
    // set ice time out, 30 seconds
    props->setProperty("Ice.Override.ConnectTimeout", "30000");
    props->setProperty("Ice.Override.Timeout", "30000");
    // trace Ice 
    props->setProperty("Ice.Trace.GC", "1");
    props->setProperty("Ice.Trace.Network", "2");
    props->setProperty("Ice.Trace.Retry", "1");
    props->setProperty("Ice.Trace.Slicing", "1");
    props->setProperty("Ice.Warn.Connections", "1");
    props->setProperty("Ice.Warn.Datagrams", "1");
    props->setProperty("Ice.Warn.Dispatch", "2");

    Ice::InitializationData id;
    id.properties = props;
    id.logger = new MyIceLogger();

    m_ic = Ice::initialize(id);
    Ice::ObjectPrx base = m_ic->stringToProxy(
            "WorkerService:tcp -h " + ip + " -p " + port);
    m_service = slice::WorkerServicePrx::checkedCast(base);
    LOG_INFO("Worker: Done initizlize ICE service");
    LOG_INFO("Worker: Done start MapReduce worker");

}

Worker::~Worker() {
    if (m_ic) {
        try {
            m_ic->destroy();
            LOG_INFO("Worker: Ice communicator destroyed.")
        } catch (const std::string& msg) {
            cerr << msg << endl;
        }
    }
}

void Worker::initLogger(const string &name, const string& homeDir) 
{
    helpers::LogLog::getLogLog()->setInternalDebugging(false);
    SharedAppenderPtr append_1(new RollingFileAppender(homeDir + name + ".log", 4 * 1024 * 1024, 5) );
    append_1->setName(name);
    std::string pattern = "%D %p - %m\n";
    append_1->setLayout(std::auto_ptr<Layout>(new PatternLayout(pattern)));
    Logger::getInstance(name.c_str()).addAppender(append_1);
    //Logger::getRoot().addAppender(append_1);
}

void Worker::run() {
    LOG_INFO("Worker: Main process is " <<getpid())
    string taskType[] = {"DoNothing", "Map", "Transfer", "Reduce", "Clean", "Shutdown"};
    Heartbeat info;
    if (!fs::exists(m_storeDir))
        fs::create_directory(m_storeDir);

    // Begin transfer server.
    IceUtil::ThreadPtr tserver = new TransServer(m_address.port, m_storeDir);
    IceUtil::ThreadControl tserverc = tserver->start();
    LOG_INFO("Worker: Done start transfer server")

    IceUtil::ThreadPtr task;
    int taskNum = 0;
    int cnt = 0;


    while(true) {
        ++cnt;
        clock_t tstart = clock();

        /// Report heartbeat.
        info.mapJobCount = m_mapTasks.size();
        info.reduceJobCount = m_reduceTasks.size();
        info.workload = info.mapJobCount + info.reduceJobCount + m_transTasks.size();
        MasterCommands mcs;
        try {
            IceUtil::Mutex::Lock lock(m_mutex);
            for (set<int>::iterator it = m_abandonedTasks.begin();
                    it != m_abandonedTasks.end(); ++it) 
                m_service->abandonTask(m_address, *it);
            m_abandonedTasks.clear();
            for (set<int>::iterator it = m_completedTasks.begin();
                    it != m_completedTasks.end(); ++it) 
                m_service->completeTask(m_address, *it);
            m_completedTasks.clear();

            mcs = m_service->sendHeartbeat(m_address, info);
        } catch (Ice::TimeoutException &ex) {
            LOG_ERROR("Worker: GetException type=Ice::TimeoutException");
            stopAllJobs();
            IceUtil::ThreadControl::sleep(IceUtil::Time::seconds(30));
            m_abandonedTasks.clear();
            m_completedTasks.clear();
            continue;
        }

        if (cnt % 100 == 0) {
            LOG_DEBUG("Worker: SendHeartbeat count=" <<cnt);
        }
        if (mcs.size() > 3) {
            LOG_WARN("Worker: GetCommands count=" << mcs.size()); 
        }
        for (int mcIdx = 0; mcIdx < mcs.size(); mcIdx++){
            MasterCommand mc = mcs[mcIdx];

            string jobDir = m_storeDir + boost::lexical_cast<string>(mc.jobId);
            string taskDir = jobDir + "/" + boost::lexical_cast<string>(mc.taskId);

            if (mc.command != McDoNothing) {
                LOG_INFO("Worker: GetTask type=" << taskType[mc.command] << " job=" 
                        <<mc.jobId << " task=" <<mc.taskId); 
                if (mc.command != McDoClean && !fs::exists(jobDir)) {
                    fs::create_directory(jobDir);
                    LOG_DEBUG("Worker: CreateDir dir="<<jobDir); 
                }
                // if (mc.command != McDoClean && !fs::exists(taskDir)) { 
                //     fs::create_directory(taskDir);
                //     LOG_DEBUG("Worker: " <<taskDir <<" created."); 
                // }
            }

            switch (mc.command) {
                case (McDoNothing): 
                    break;

                case McDoMap:
                    {
                        {
                            IceUtil::Mutex::Lock lock(m_mutex);
                            m_mapTasks.insert(mc.taskId);
                            m_jobTasks[mc.jobId].insert(mc.taskId);
                        }
                        TaskConfigure tconf = getConfigure(mc);
                        task = new MapTask(tconf, mc, taskDir, this); 
                        IceUtil::ThreadControl tc = task->start();
                        tc.detach();
                        ++taskNum;
                        break;
                    }

                case McDoTrans:
                    {
                        {
                            IceUtil::Mutex::Lock lock(m_mutex);
                            m_transTasks.insert(mc.taskId);
                            m_jobTasks[mc.jobId].insert(mc.taskId);
                        }
                        task = new TransTask(mc, jobDir, this);
                        IceUtil::ThreadControl tc = task->start();
                        tc.detach();
                        ++taskNum;
                        break;
                    }

                case McDoReduce:
                    {
                        {
                            IceUtil::Mutex::Lock lock(m_mutex);
                            m_reduceTasks.insert(mc.taskId);
                            m_jobTasks[mc.jobId].insert(mc.taskId);
                        }
                        string reducerLib = mc.soFile + "Reducer.so";
                        string compLib = mc.soFile + "KeyComparison.so";
                        TaskConfigure tconf = getConfigure(mc);
                        task = new ReduceTask(tconf, mc, taskDir, this);
                        IceUtil::ThreadControl tc = task->start();
                        tc.detach();
                        ++taskNum;
                        break;
                    }

                case McDoClean:
                    {
                        IceUtil::Mutex::Lock lock(m_mutex);
                        // Stop tasks.
                        for (set<int>::iterator it = m_jobTasks[mc.jobId].begin();
                                it != m_jobTasks[mc.jobId].end(); ++it) {
                            m_uselessTasks.insert(*it);
                        }

                        // Record useless jobs.
                        m_uselessJobs.push_back(mc.jobId);

                        // Clear m_mappedFiles
                        map<pair<int, int>, vector<string> >::iterator it;
                        for (it = m_mappedFiles.begin(); it != m_mappedFiles.end(); ++it) {
                            if (it->first.first == mc.jobId)
                                m_mappedFiles.erase(it);
                        }

                        m_service->completeTask(m_address, mc.taskId);
                        LOG_INFO("Worker: CompleteTask job=" <<mc.jobId <<" task=" <<mc.taskId);
                    }
                    break;

                case McShutdown:
                    return;
            }
        } // end for each MasterCommand
        // Do clean
        {
            IceUtil::Mutex::Lock lock(m_mutex);
            for (int i = 0; i < m_uselessJobs.size(); ++i) {
                if (m_jobTasks[m_uselessJobs[i]].size() == 0) {
                    m_jobTasks.erase(m_uselessJobs[i]);
                    fs::path job_dir = m_storeDir 
                        + boost::lexical_cast<string>(m_uselessJobs[i]);
                    //fs::remove_all(job_dir);
                    m_uselessJobs[i] = m_uselessJobs[m_uselessJobs.size() - 1];
                    m_uselessJobs.pop_back();
                    LOG_INFO("Worker: RemoveDir dir=" <<job_dir);
                }
            }
        }
        //sleep(1);
        IceUtil::ThreadControl::sleep(IceUtil::Time::seconds(1));

        clock_t tend = clock();
        if ((tend - tstart) / CLOCKS_PER_SEC > 2) {
            LOG_WARN("Worker: LongLoop seconds=" <<(tend - tstart) / CLOCKS_PER_SEC);
        }
    }
}

void Worker::stopAllJobs() {
    for (std::map<int, std::set<int> >::iterator it = m_jobTasks.begin(); 
            it != m_jobTasks.end(); ++it) {
        for (std::set<int>::iterator jt = it->second.begin();
                jt != it->second.end(); ++jt) {
            m_uselessTasks.insert(*jt);
        }

        m_uselessJobs.push_back(it->first);
    }

    m_mappedFiles.clear();
    m_abandonedTasks.clear();
    m_completedTasks.clear();
}

TaskConfigure Worker::getConfigure(const MasterCommand &mc) {
    TaskConfigure tconf;
    string MapperClass = mc.mapperClass;
    string ReducerClass = mc.reducerClass;
    string MapperInKeyClass = mc.mapperInKeyClass;
    string MapperInValueClass = mc.mapperInValueClass;
    string MapperOutKeyClass = mc.mapperOutKeyClass;
    string MapperOutValueClass = mc.mapperOutValueClass;
    string ReducerOutKeyClass = mc.reducerOutKeyClass;
    string ReducerOutValueClass = mc.reducerOutValueClass;
    string MapperRecorClass = mc.mapperClass;
    string MapperRecordReaderClass = mc.mapperRecordReaderClass;
    string ReducerRecordWriterClass = mc.reducerRecordWriterClass;

    tconf.setDllName(mc.soFile);
    if (MapperClass != "") tconf.setMapperClass(MapperClass);
    if (ReducerClass != "") tconf.setReducerClass(ReducerClass);
    if (MapperInKeyClass != "") tconf.setMapperInKeyClass(MapperInKeyClass);
    if (MapperInValueClass != "") tconf.setMapperInValueClass(MapperInValueClass);
    if (MapperOutKeyClass != "") tconf.setMapperOutKeyClass(MapperOutKeyClass);
    if (MapperOutValueClass != "") tconf.setMapperOutValueClass(MapperOutValueClass);

    if (ReducerOutKeyClass != "") tconf.setReducerOutKeyClass(ReducerOutKeyClass);
    if (ReducerOutValueClass != "") tconf.setReducerOutValueClass(ReducerOutValueClass);
    if (MapperRecordReaderClass != "") tconf.setMapperRecordReaderClass(MapperRecordReaderClass);
    if (ReducerRecordWriterClass != "") tconf.setReducerRecordWriterClass(ReducerRecordWriterClass);

    return tconf;

}

void Worker::completeTask(int jobId, int taskId) {
    IceUtil::Mutex::Lock lock(m_mutex);
    m_uselessTasks.erase(taskId);
    m_jobTasks[jobId].erase(taskId);
    m_mapTasks.erase(taskId);
    m_reduceTasks.erase(taskId);
    m_transTasks.erase(taskId);
    m_completedTasks.insert(taskId);
    //m_service->completeTask(m_address, taskId);
    LOG_INFO("Worker: CompleteTask job=" <<jobId <<" task=" <<taskId);
}

void Worker::abandonTask(int jobId, int taskId) {
    IceUtil::Mutex::Lock lock(m_mutex);
    m_uselessTasks.erase(taskId);
    m_jobTasks[jobId].erase(taskId);
    m_mapTasks.erase(taskId);
    m_reduceTasks.erase(taskId);
    m_transTasks.erase(taskId);
    m_abandonedTasks.insert(taskId);
    //m_service->abandonTask(m_address, taskId);
    //LOG_INFO("Worker: AbandonTask job=" <<jobId <<" task=" <<taskId);
}

void Worker::addMappedFile(int jobId, int partition, const string &path) {
    IceUtil::Mutex::Lock lock(m_mutex);
    pair<int, int> p(jobId, partition);
    m_mappedFiles[p].push_back(path);
}

void Worker::getMappedFiles(int jobId, int partition, vector<string> &paths) {
    IceUtil::Mutex::Lock lock(m_mutex);
    pair<int, int> p(jobId, partition);
    paths = m_mappedFiles[p];
}


