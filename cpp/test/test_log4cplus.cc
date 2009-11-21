/**
 * @file	test_log4cplus.cc
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年07月04日 09时21分36秒 CST
 *
 */

#include <log4cplus/logger.h>
#include <log4cplus/consoleappender.h>
#include <log4cplus/fileappender.h>
#include <log4cplus/layout.h>
#include <log4cplus/ndc.h>
#include <log4cplus/helpers/loglog.h>
#include <log4cplus/loglevel.h>
#include <iostream>

using namespace std;
using namespace log4cplus;
using namespace log4cplus::helpers;

#define LOGGER_NAME "test" 
#define SUBLOGGER_NAME "test.sub" 
#define LOGGER_INSTANCE Logger::getInstance(LOGGER_NAME)
#define LOG_PREFIX ""
#define LOG_TRACE(x)    LOG4CPLUS_TRACE(LOGGER_INSTANCE, LOG_PREFIX <<x)
#define LOG_DEBUG(x)    LOG4CPLUS_DEBUG(LOGGER_INSTANCE, LOG_PREFIX <<x)
#define LOG_INFO(x)     LOG4CPLUS_INFO(LOGGER_INSTANCE, LOG_PREFIX <<x)
#define LOG_WARN(x)     LOG4CPLUS_WARN(LOGGER_INSTANCE, LOG_PREFIX <<x)
#define LOG_ERROR(x)    LOG4CPLUS_ERROR(LOGGER_INSTANCE, LOG_PREFIX <<x)
#define LOG_FATAL(x)    LOG4CPLUS_FATAL(LOGGER_INSTANCE, LOG_PREFIX <<x)

void log() {
    LOG_TRACE("TRACE");
    LOG_DEBUG("DEBUG");
    LOG_INFO("INFO");
    LOG_WARN("WARN");
    LOG_ERROR("ERROR");
    LOG_FATAL("FATAL");
}

void loglog() {
    LogLog::getLogLog()->debug("debug");
    LogLog::getLogLog()->warn("warn");
    LogLog::getLogLog()->error("error");
}

void test_log_level() {
    cout <<"Test Log Level" <<endl;
    Logger logger = Logger::getInstance(LOGGER_NAME);
    log();
    cout <<"Set log level: fatal" <<endl;
    logger.setLogLevel(FATAL_LOG_LEVEL);
    log();
    cout <<"Set log level: info" <<endl;
    logger.setLogLevel(INFO_LOG_LEVEL);
    log();
    cout <<"Set log level: trace" <<endl;
    logger.setLogLevel(TRACE_LOG_LEVEL);
    log();
    cout <<"Set log level: all" <<endl;
    logger.setLogLevel(ALL_LOG_LEVEL);
    log();
    cout <<"Set log level: off" <<endl;
    logger.setLogLevel(OFF_LOG_LEVEL);
}

void test_logger_class() {
    const string logfile = "test_log.log";
    Logger logger = Logger::getInstance(LOGGER_NAME);
    SharedAppenderPtr append_1(
            new RollingFileAppender(logfile, 4 * 12 * 1024, 5));
    append_1->setName("rollingfile");
    SharedAppenderPtr append_2(new ConsoleAppender());
    append_2->setName("console");
    logger.addAppender(append_1);

    cout <<"Test Logger class" <<endl;
    cout <<"exists logger[" <<LOGGER_NAME <<"]: " <<Logger::exists(LOGGER_NAME) <<endl;
    vector<Logger> vl = Logger::getCurrentLoggers();
    cout <<"has " <<vl.size() <<" loggers " <<endl;
    cout <<"logger[" <<LOGGER_NAME <<"] has " <<logger.getAllAppenders().size() 
        <<" appenders" <<endl;
    logger.addAppender(append_2);
    cout <<"logger[" <<LOGGER_NAME <<"] has " <<logger.getAllAppenders().size() 
        <<" appenders" <<endl;
}

void test_loglog() {
    cout <<"Test LogLog" <<endl;
    loglog();
    cout <<"set loglog quiet" <<endl;
    LogLog::getLogLog()->setQuietMode(true);
    loglog();
    LogLog::getLogLog()->setQuietMode(false);
    cout <<"set loglog debug on" <<endl;
    LogLog::getLogLog()->setInternalDebugging(true);
    loglog();
}

void test_layout() {
    cout <<"Test Layout" <<endl;
    const string pattern = "[%c]%D %p - %m (%l)\n";
    Logger logger = Logger::getInstance(LOGGER_NAME);
    SharedAppenderPtr append_2 = logger.getAppender("console");
    cout <<"set layout simple" <<endl;
    append_2->setLayout(auto_ptr<Layout>(new SimpleLayout()));
    log();
    cout <<"set layout pattern" <<pattern;
    append_2->setLayout(std::auto_ptr<Layout>(new PatternLayout(pattern)));
    log();
    cout <<"set layout ttcc" <<endl;
    append_2->setLayout(std::auto_ptr<Layout>(new TTCCLayout()));
    log();
}

void test_appender() {
    cout <<"Test Appender" <<endl;
    Logger logger = Logger::getInstance(LOGGER_NAME);
    cout <<"add file appender" <<endl;
    logger.addAppender(new FileAppender("test_log_fileappend.log"));
    log();
    logger.addAppender(new DailyRollingFileAppender("test_log_dr.log", MINUTELY, true, 5));
    log();
}

void test_sublog() {
    cout <<"Test sublog" <<endl;
    Logger logger = Logger::getInstance(LOGGER_NAME);
    Logger sublogger = Logger::getInstance(SUBLOGGER_NAME);
    SharedAppenderPtr a(new ConsoleAppender());
    a->setName("subconsole");
    a->setLayout(std::auto_ptr<Layout>(new TTCCLayout()));
    logger.setLogLevel(ALL_LOG_LEVEL);
    sublogger.setLogLevel(NOT_SET_LOG_LEVEL);
    sublogger.log(TRACE_LOG_LEVEL, "sub trace");
    sublogger.log(WARN_LOG_LEVEL, "sub warn");
    logger.log(TRACE_LOG_LEVEL, "trace");
    logger.log(WARN_LOG_LEVEL, "warn");

    sublogger.setLogLevel(WARN_LOG_LEVEL);
    sublogger.addAppender(a);
    sublogger.log(TRACE_LOG_LEVEL, "sub trace");
    sublogger.log(WARN_LOG_LEVEL, "sub warn");
    logger.log(TRACE_LOG_LEVEL, "trace");
    logger.log(WARN_LOG_LEVEL, "warn");
}

int main() {
    test_loglog();
    test_logger_class();
    test_log_level();
    test_appender();
    test_layout();
    test_sublog();

    return 0;
}
