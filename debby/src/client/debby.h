/**
 * @file    debby.h
 *  This is the ONLY header file users need to include to use Debby service.
 * 
 * @author  Fan Kai(fk), Peking University
 *
 */

#ifndef  DEBBY_FILE_HEADER_INC
#define  DEBBY_FILE_HEADER_INC

#include <vector>
#include <string>
#include <functional>
#include <boost/shared_ptr.hpp>

namespace debby {

class ClientImpl;

/// Base class for all exceptions that would be thrown out by debby client.
struct Exception : public std::exception {
public:
    Exception() throw() { reason = NULL; }
    ~Exception() throw() { }
    Exception(const char *str) { reason = str; }
    const char *what() const throw() { return reason; }
private:
    const char *reason;
};

/// This exception is raised when client can not connect the debby server.
struct ConnectionException : public Exception {
    ConnectionException(const char *str) : Exception(str) { }
};

/// This exception is raised when user try to use the debby service from 
/// a closed debby session. 
/// Each debby Client instance is related to a debby session, user could 
/// explicitly close this session using Client::close() function, or this 
/// session would be automatically closed when the instance is destructed.
struct SessionClosed : public Exception {
    SessionClosed() : Exception("Debby Session already closed.") { }
};

/// This exception is raised if user specified a invalid path. 
/// All path used in debby should be unix-style absolute path starts with '/' and 
/// NOT end with a '/'.
struct InvalidName : public Exception {
    InvalidName() : Exception("File name is invalid.") { }
};

/// This exception is raised if user try to operate on a file that does not exist.
struct FileNotExist : public Exception {
    FileNotExist() : Exception("File does not exist.") { } 
};

/// This class defines a callback. 
/// User should define a subclass of Callback and implement the run() function.
class Callback {
public:
    virtual void run() = 0;
    virtual ~Callback() {}
};  

/// This class defines the configuration for a debby client.
struct ClientConfig {
    ClientConfig():printLog(false) {}
    /// Whether print log on screen, the default is false.
    bool printLog;
    /// Addresses of debby servers, one or more could be specified.
    std::vector<std::pair<std::string, int> > addr;
    /// Log level, possible values are FATAL, ERROR, WARN, INFO, DEBUG, TRACE(default). 
    std::string logLevel;
};

/// Event types users could register on a certain path.
enum EventType {
    /// Indicate creation of a file.
    ECreated,       
    /// Indicate removal of a file.
    ERemoved,     
    /// Indicate change of the file content.
    EChanged,       
    /// Indicate creation or removal of a file with certain path.
    ///     For example, if /demo/files/f1 is created or removed, 
    ///     this event will be raised on "/" and "/demo" and "/demo/files". 
    ///     So register this event on high level path like "/" would possibly
    ///     raise large quantity of this events.
    ///
    EDirChanged,    
    /// Indicate release of a lock.
    ELockReleased   
};

/// This class defines a debby client, users use this class to access the debby service.
class Client {
public:
    /// A session with debby server is created on construction.
    Client(const ClientConfig &conf);
    /// If the session is still alive, it will be closed on destruction.
    ~Client();

    /// Create a file.
    /// @param path All path used in debby should be unix-style absolute path starts 
    ///             with '/' and NOT end with a '/'.
    /// @param tmp  Specify whether this file should be temporary.  
    ///             A temporary file is deleted automatically when the session is closed.
    bool create(const std::string &path, bool tmp = false);
    /// Remove a file.
    void remove(const std::string &path);
    /// Test whether a certain file exists.
    bool exists(const std::string &path);
    /// Test whether a file is a temporary file.
    bool istmp(const std::string &path);
    /// List all files whose filename starts with the path.
    ///     For example, list "/demo" will return all filenames start with "/demo",
    ///     including files like "/demo/file1" and "/demo/dir1/file2".
    std::vector<std::string> list(const std::string &path);
    /// Read the WHOLE content of a file to a string.
    std::string read(const std::string &path);
    /// Write data to a file, the original data is overwritten.
    /// The size of data is limited to 128K.
    void write(const std::string &path, const std::string &data);

    /// Create a lock with a certain name. 
    bool lock(const std::string &name);
    /// Release a lock.
    void release(const std::string &name);

    /// Register a event on a certain path.
    /// When the specified event occurs, the Callback will be raised.
    /// @param pcb  A user defined Callback instance.
    void regcb(const std::string &path, EventType t, boost::shared_ptr<Callback> pcb);
    /// Clear all Callbacks registed on a specified path and event type.
    void clearcb(const std::string &path, EventType t);

    /// Close current session.
    void close();

private:
    ClientImpl *_client;
   
};  // class Client

}   // namespace debby

#endif   /* ----- #ifndef DEBBY_FILE_HEADER_INC  ----- */

