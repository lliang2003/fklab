/* 
 * @(#)Logger.hpp
 * AUTHOR: YangZhifeng
 * CREATE DATE: 13  3ÔÂ 2007
 * DESCRIPTION: 
 */
/** @file
 * system log for debug purpose
 * @author YangZhifeng
 * @date 13  3ÔÂ 2007
 */
#ifndef _LOGGER_HPP
#define _LOGGER_HPP 1

#include <log4cplus/logger.h>
#include <log4cplus/fileappender.h>
#include <log4cplus/consoleappender.h>
#include <log4cplus/layout.h>
#include <log4cplus/ndc.h>
#include <log4cplus/helpers/loglog.h>
#include <log4cplus/loglevel.h>
#include <iostream>

using namespace log4cplus;

#define LOGGER_INSTANCE Logger::getInstance(LOGGER_NAME)

#ifndef CXX_TEST
    #define LOG_TRACE(x)    LOG4CPLUS_TRACE(LOGGER_INSTANCE, LOG_PREFIX <<x)
    #define LOG_DEBUG(x)    LOG4CPLUS_DEBUG(LOGGER_INSTANCE, LOG_PREFIX <<x)
    #define LOG_INFO(x)     LOG4CPLUS_INFO(LOGGER_INSTANCE, LOG_PREFIX <<x)
    #define LOG_WARN(x)     LOG4CPLUS_WARN(LOGGER_INSTANCE, LOG_PREFIX <<x)
    #define LOG_ERROR(x)    LOG4CPLUS_ERROR(LOGGER_INSTANCE, LOG_PREFIX <<x)
    #define LOG_FATAL(x)    LOG4CPLUS_FATAL(LOGGER_INSTANCE, LOG_PREFIX <<x)
#else
    #define LOG_TRACE(x)
    #define LOG_DEBUG(x) 
    #define LOG_INFO(x) 
    #define LOG_WARN(x) 
    #define LOG_ERROR(x) 
    #define LOG_FATAL(x) 
#endif

#define LOG_PREFIX ""

#define _WRAP(X, Y) {X;Y}

#define _THROW(EX) {\
    LOG_WARN("Throw " <<#EX <<" from line " <<__LINE__); \
    throw EX; \
}

#define _CATCH(EX, WHAT, FUNC) catch (EX &ex) {\
    LOG_WARN("Catch " <<#EX <<"(" << WHAT <<").") \
    FUNC; \
}

#define _CATCH_THROW(EX, WHAT, FUNC) catch (EX &ex) {\
    LOG_WARN("Catch " <<#EX <<"(" <<WHAT <<")."); \
    FUNC; \
    LOG_WARN("Rethrow"); \
    throw ex; \
}

#endif /* _LOGGER_HPP */
