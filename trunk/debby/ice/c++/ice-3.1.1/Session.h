// **********************************************************************
//
// Copyright (c) 2003-2006 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.1.1
// Generated from file `Session.ice'

#ifndef __Session_h__
#define __Session_h__

#include <Ice/LocalObjectF.h>
#include <Ice/ProxyF.h>
#include <Ice/ObjectF.h>
#include <Ice/Exception.h>
#include <Ice/LocalObject.h>
#include <Ice/Proxy.h>
#include <Ice/Object.h>
#include <Ice/Outgoing.h>
#include <Ice/Incoming.h>
#include <Ice/IncomingAsync.h>
#include <Ice/Direct.h>
#include <Ice/UserExceptionFactory.h>
#include <Ice/FactoryTable.h>
#include <Ice/StreamF.h>
#include <Exceptions.h>
#include <Ice/UndefSysMacros.h>

#ifndef ICE_IGNORE_VERSION
#   if ICE_INT_VERSION / 100 != 301
#       error Ice version mismatch!
#   endif
#   if ICE_INT_VERSION % 100 < 1
#       error Ice patch level mismatch!
#   endif
#endif

namespace IceProxy
{

namespace debby
{

namespace slice
{

class Session;
bool operator==(const Session&, const Session&);
bool operator!=(const Session&, const Session&);
bool operator<(const Session&, const Session&);
bool operator<=(const Session&, const Session&);
bool operator>(const Session&, const Session&);
bool operator>=(const Session&, const Session&);

}

}

}

namespace debby
{

namespace slice
{

class Session;
bool operator==(const Session&, const Session&);
bool operator!=(const Session&, const Session&);
bool operator<(const Session&, const Session&);
bool operator<=(const Session&, const Session&);
bool operator>(const Session&, const Session&);
bool operator>=(const Session&, const Session&);

}

}

namespace IceInternal
{

void incRef(::debby::slice::Session*);
void decRef(::debby::slice::Session*);

void incRef(::IceProxy::debby::slice::Session*);
void decRef(::IceProxy::debby::slice::Session*);

}

namespace debby
{

namespace slice
{

typedef ::IceInternal::Handle< ::debby::slice::Session> SessionPtr;
typedef ::IceInternal::ProxyHandle< ::IceProxy::debby::slice::Session> SessionPrx;

void __write(::IceInternal::BasicStream*, const SessionPrx&);
void __read(::IceInternal::BasicStream*, SessionPrx&);
void __write(::IceInternal::BasicStream*, const SessionPtr&);
void __patch__SessionPtr(void*, ::Ice::ObjectPtr&);

}

}

namespace debby
{

namespace slice
{

enum EventType
{
    ECreated,
    ERemoved,
    EChanged,
    EDirChanged,
    EElected,
    ENothing
};

void __write(::IceInternal::BasicStream*, EventType);
void __read(::IceInternal::BasicStream*, EventType&);

struct Event
{
    ::debby::slice::EventType type;
    ::std::string path;

    bool operator==(const Event&) const;
    bool operator!=(const Event&) const;
    bool operator<(const Event&) const;
    bool operator<=(const Event& __rhs) const
    {
	return operator<(__rhs) || operator==(__rhs);
    }
    bool operator>(const Event& __rhs) const
    {
	return !operator<(__rhs) && !operator==(__rhs);
    }
    bool operator>=(const Event& __rhs) const
    {
	return !operator<(__rhs);
    }

    void __write(::IceInternal::BasicStream*) const;
    void __read(::IceInternal::BasicStream*);
};

typedef ::std::vector< ::std::string> Files;

class __U__Files { };

}

}

namespace debby
{

namespace slice
{

class AMD_Session_keepAlive : virtual public ::IceUtil::Shared
{
public:

    virtual void ice_response(const ::debby::slice::Event&) = 0;
    virtual void ice_exception(const ::Ice::Exception&) = 0;
    virtual void ice_exception(const ::std::exception&) = 0;
    virtual void ice_exception() = 0;
};

typedef ::IceUtil::Handle< ::debby::slice::AMD_Session_keepAlive> AMD_Session_keepAlivePtr;

}

}

namespace IceAsync
{

namespace debby
{

namespace slice
{

class AMD_Session_keepAlive : public ::debby::slice::AMD_Session_keepAlive, public ::IceInternal::IncomingAsync
{
public:

    AMD_Session_keepAlive(::IceInternal::Incoming&);

    virtual void ice_response(const ::debby::slice::Event&);
    virtual void ice_exception(const ::Ice::Exception&);
    virtual void ice_exception(const ::std::exception&);
    virtual void ice_exception();
};

}

}

}

namespace IceProxy
{

namespace debby
{

namespace slice
{

class Session : virtual public ::IceProxy::Ice::Object
{
public:

    ::Ice::Int connect(const ::std::string& name)
    {
	return connect(name, __defaultContext());
    }
    ::Ice::Int connect(const ::std::string&, const ::Ice::Context&);

    void close(::Ice::Int handle)
    {
	close(handle, __defaultContext());
    }
    void close(::Ice::Int, const ::Ice::Context&);

    ::debby::slice::Event keepAlive(::Ice::Int handle)
    {
	return keepAlive(handle, __defaultContext());
    }
    ::debby::slice::Event keepAlive(::Ice::Int, const ::Ice::Context&);

    void regEvent(::Ice::Int handle, const ::debby::slice::Event& e)
    {
	regEvent(handle, e, __defaultContext());
    }
    void regEvent(::Ice::Int, const ::debby::slice::Event&, const ::Ice::Context&);

    void unregEvent(::Ice::Int handle, const ::debby::slice::Event& e)
    {
	unregEvent(handle, e, __defaultContext());
    }
    void unregEvent(::Ice::Int, const ::debby::slice::Event&, const ::Ice::Context&);

    ::std::string read(::Ice::Int handle, const ::std::string& path)
    {
	return read(handle, path, __defaultContext());
    }
    ::std::string read(::Ice::Int, const ::std::string&, const ::Ice::Context&);

    void write(::Ice::Int handle, const ::std::string& path, const ::std::string& data)
    {
	write(handle, path, data, __defaultContext());
    }
    void write(::Ice::Int, const ::std::string&, const ::std::string&, const ::Ice::Context&);

    bool create(::Ice::Int handle, const ::std::string& path, bool temp)
    {
	return create(handle, path, temp, __defaultContext());
    }
    bool create(::Ice::Int, const ::std::string&, bool, const ::Ice::Context&);

    void remove(::Ice::Int handle, const ::std::string& path)
    {
	remove(handle, path, __defaultContext());
    }
    void remove(::Ice::Int, const ::std::string&, const ::Ice::Context&);

    bool exists(::Ice::Int handle, const ::std::string& path)
    {
	return exists(handle, path, __defaultContext());
    }
    bool exists(::Ice::Int, const ::std::string&, const ::Ice::Context&);

    bool istmp(::Ice::Int handle, const ::std::string& path)
    {
	return istmp(handle, path, __defaultContext());
    }
    bool istmp(::Ice::Int, const ::std::string&, const ::Ice::Context&);

    ::debby::slice::Files list(::Ice::Int handle, const ::std::string& path)
    {
	return list(handle, path, __defaultContext());
    }
    ::debby::slice::Files list(::Ice::Int, const ::std::string&, const ::Ice::Context&);
    
    static const ::std::string& ice_staticId();

private: 

    virtual ::IceInternal::Handle< ::IceDelegateM::Ice::Object> __createDelegateM();
    virtual ::IceInternal::Handle< ::IceDelegateD::Ice::Object> __createDelegateD();
};

}

}

}

namespace IceDelegate
{

namespace debby
{

namespace slice
{

class Session : virtual public ::IceDelegate::Ice::Object
{
public:

    virtual ::Ice::Int connect(const ::std::string&, const ::Ice::Context&) = 0;

    virtual void close(::Ice::Int, const ::Ice::Context&) = 0;

    virtual ::debby::slice::Event keepAlive(::Ice::Int, const ::Ice::Context&) = 0;

    virtual void regEvent(::Ice::Int, const ::debby::slice::Event&, const ::Ice::Context&) = 0;

    virtual void unregEvent(::Ice::Int, const ::debby::slice::Event&, const ::Ice::Context&) = 0;

    virtual ::std::string read(::Ice::Int, const ::std::string&, const ::Ice::Context&) = 0;

    virtual void write(::Ice::Int, const ::std::string&, const ::std::string&, const ::Ice::Context&) = 0;

    virtual bool create(::Ice::Int, const ::std::string&, bool, const ::Ice::Context&) = 0;

    virtual void remove(::Ice::Int, const ::std::string&, const ::Ice::Context&) = 0;

    virtual bool exists(::Ice::Int, const ::std::string&, const ::Ice::Context&) = 0;

    virtual bool istmp(::Ice::Int, const ::std::string&, const ::Ice::Context&) = 0;

    virtual ::debby::slice::Files list(::Ice::Int, const ::std::string&, const ::Ice::Context&) = 0;
};

}

}

}

namespace IceDelegateM
{

namespace debby
{

namespace slice
{

class Session : virtual public ::IceDelegate::debby::slice::Session,
		virtual public ::IceDelegateM::Ice::Object
{
public:

    virtual ::Ice::Int connect(const ::std::string&, const ::Ice::Context&);

    virtual void close(::Ice::Int, const ::Ice::Context&);

    virtual ::debby::slice::Event keepAlive(::Ice::Int, const ::Ice::Context&);

    virtual void regEvent(::Ice::Int, const ::debby::slice::Event&, const ::Ice::Context&);

    virtual void unregEvent(::Ice::Int, const ::debby::slice::Event&, const ::Ice::Context&);

    virtual ::std::string read(::Ice::Int, const ::std::string&, const ::Ice::Context&);

    virtual void write(::Ice::Int, const ::std::string&, const ::std::string&, const ::Ice::Context&);

    virtual bool create(::Ice::Int, const ::std::string&, bool, const ::Ice::Context&);

    virtual void remove(::Ice::Int, const ::std::string&, const ::Ice::Context&);

    virtual bool exists(::Ice::Int, const ::std::string&, const ::Ice::Context&);

    virtual bool istmp(::Ice::Int, const ::std::string&, const ::Ice::Context&);

    virtual ::debby::slice::Files list(::Ice::Int, const ::std::string&, const ::Ice::Context&);
};

}

}

}

namespace IceDelegateD
{

namespace debby
{

namespace slice
{

class Session : virtual public ::IceDelegate::debby::slice::Session,
		virtual public ::IceDelegateD::Ice::Object
{
public:

    virtual ::Ice::Int connect(const ::std::string&, const ::Ice::Context&);

    virtual void close(::Ice::Int, const ::Ice::Context&);

    virtual ::debby::slice::Event keepAlive(::Ice::Int, const ::Ice::Context&);

    virtual void regEvent(::Ice::Int, const ::debby::slice::Event&, const ::Ice::Context&);

    virtual void unregEvent(::Ice::Int, const ::debby::slice::Event&, const ::Ice::Context&);

    virtual ::std::string read(::Ice::Int, const ::std::string&, const ::Ice::Context&);

    virtual void write(::Ice::Int, const ::std::string&, const ::std::string&, const ::Ice::Context&);

    virtual bool create(::Ice::Int, const ::std::string&, bool, const ::Ice::Context&);

    virtual void remove(::Ice::Int, const ::std::string&, const ::Ice::Context&);

    virtual bool exists(::Ice::Int, const ::std::string&, const ::Ice::Context&);

    virtual bool istmp(::Ice::Int, const ::std::string&, const ::Ice::Context&);

    virtual ::debby::slice::Files list(::Ice::Int, const ::std::string&, const ::Ice::Context&);
};

}

}

}

namespace debby
{

namespace slice
{

class Session : virtual public ::Ice::Object
{
public:

    typedef SessionPrx ProxyType;
    typedef SessionPtr PointerType;
    
    virtual ::Ice::ObjectPtr ice_clone() const;

    virtual bool ice_isA(const ::std::string&, const ::Ice::Current& = ::Ice::Current()) const;
    virtual ::std::vector< ::std::string> ice_ids(const ::Ice::Current& = ::Ice::Current()) const;
    virtual const ::std::string& ice_id(const ::Ice::Current& = ::Ice::Current()) const;
    static const ::std::string& ice_staticId();

    virtual ::Ice::Int connect(const ::std::string&, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___connect(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual void close(::Ice::Int, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___close(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual void keepAlive_async(const ::debby::slice::AMD_Session_keepAlivePtr&, ::Ice::Int, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___keepAlive(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual void regEvent(::Ice::Int, const ::debby::slice::Event&, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___regEvent(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual void unregEvent(::Ice::Int, const ::debby::slice::Event&, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___unregEvent(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual ::std::string read(::Ice::Int, const ::std::string&, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___read(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual void write(::Ice::Int, const ::std::string&, const ::std::string&, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___write(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual bool create(::Ice::Int, const ::std::string&, bool, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___create(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual void remove(::Ice::Int, const ::std::string&, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___remove(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual bool exists(::Ice::Int, const ::std::string&, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___exists(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual bool istmp(::Ice::Int, const ::std::string&, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___istmp(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual ::debby::slice::Files list(::Ice::Int, const ::std::string&, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___list(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual ::IceInternal::DispatchStatus __dispatch(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual void __write(::IceInternal::BasicStream*) const;
    virtual void __read(::IceInternal::BasicStream*, bool);
    virtual void __write(const ::Ice::OutputStreamPtr&) const;
    virtual void __read(const ::Ice::InputStreamPtr&, bool);
};

void __patch__SessionPtr(void*, ::Ice::ObjectPtr&);

}

}

#endif
