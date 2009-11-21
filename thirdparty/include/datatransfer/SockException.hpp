

#ifndef _SOCK_EXCEPTION_HPP
#define _SOCK_EXCEPTION_HPP

#include <exception>
#include <string>

class TCPException:public std::exception
{
public:
    TCPException(const char * text) throw ():m_error(text){}
    ~TCPException() throw(){}
    virtual const char* what() const throw()
    {
        std::string str="TCP Socket Exception."+m_error;
        return str.c_str();
    }
private:
    std::string m_error;
};


#endif
