// **********************************************************************
//
// Copyright (c) 2003-2006 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.1.1
// Generated from file `Exceptions.ice'

#include <Exceptions.h>
#include <Ice/BasicStream.h>
#include <Ice/Object.h>
#include <Ice/LocalException.h>
#include <IceUtil/Iterator.h>

#ifndef ICE_IGNORE_VERSION
#   if ICE_INT_VERSION / 100 != 301
#       error Ice version mismatch!
#   endif
#   if ICE_INT_VERSION % 100 < 1
#       error Ice patch level mismatch!
#   endif
#endif

debby::slice::SessionException::SessionException(const ::std::string& __ice_reason) :
#if (defined(_MSC_VER) && (_MSC_VER < 1300)) // VC++ 6 compiler bug
    UserException(),
#else
    ::Ice::UserException(),
#endif
    reason(__ice_reason)
{
}

static const char* __debby__slice__SessionException_name = "debby::slice::SessionException";

const ::std::string
debby::slice::SessionException::ice_name() const
{
    return __debby__slice__SessionException_name;
}

::Ice::Exception*
debby::slice::SessionException::ice_clone() const
{
    return new SessionException(*this);
}

void
debby::slice::SessionException::ice_throw() const
{
    throw *this;
}

void
debby::slice::SessionException::__write(::IceInternal::BasicStream* __os) const
{
    __os->write(::std::string("::debby::slice::SessionException"), false);
    __os->startWriteSlice();
    __os->write(reason);
    __os->endWriteSlice();
}

void
debby::slice::SessionException::__read(::IceInternal::BasicStream* __is, bool __rid)
{
    if(__rid)
    {
	::std::string myId;
	__is->read(myId, false);
    }
    __is->startReadSlice();
    __is->read(reason);
    __is->endReadSlice();
}

void
debby::slice::SessionException::__write(const ::Ice::OutputStreamPtr&) const
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "exception debby::slice::SessionException was not generated with stream support";
    throw ex;
}

void
debby::slice::SessionException::__read(const ::Ice::InputStreamPtr&, bool)
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "exception debby::slice::SessionException was not generated with stream support";
    throw ex;
}

struct __F__debby__slice__SessionException : public ::IceInternal::UserExceptionFactory
{
    virtual void
    createAndThrow()
    {
	throw ::debby::slice::SessionException();
    }
};

static ::IceInternal::UserExceptionFactoryPtr __F__debby__slice__SessionException__Ptr = new __F__debby__slice__SessionException;

const ::IceInternal::UserExceptionFactoryPtr&
debby::slice::SessionException::ice_factory()
{
    return __F__debby__slice__SessionException__Ptr;
}

class __F__debby__slice__SessionException__Init
{
public:

    __F__debby__slice__SessionException__Init()
    {
	::IceInternal::factoryTable->addExceptionFactory("::debby::slice::SessionException", ::debby::slice::SessionException::ice_factory());
    }

    ~__F__debby__slice__SessionException__Init()
    {
	::IceInternal::factoryTable->removeExceptionFactory("::debby::slice::SessionException");
    }
};

static __F__debby__slice__SessionException__Init __F__debby__slice__SessionException__i;

#ifdef __APPLE__
extern "C" { void __F__debby__slice__SessionException__initializer() {} }
#endif

debby::slice::FileNotExist::FileNotExist(const ::std::string& __ice_reason) :
#if (defined(_MSC_VER) && (_MSC_VER < 1300)) // VC++ 6 compiler bug
    SessionException(__ice_reason)
#else
    ::debby::slice::SessionException(__ice_reason)
#endif
{
}

static const char* __debby__slice__FileNotExist_name = "debby::slice::FileNotExist";

const ::std::string
debby::slice::FileNotExist::ice_name() const
{
    return __debby__slice__FileNotExist_name;
}

::Ice::Exception*
debby::slice::FileNotExist::ice_clone() const
{
    return new FileNotExist(*this);
}

void
debby::slice::FileNotExist::ice_throw() const
{
    throw *this;
}

void
debby::slice::FileNotExist::__write(::IceInternal::BasicStream* __os) const
{
    __os->write(::std::string("::debby::slice::FileNotExist"), false);
    __os->startWriteSlice();
    __os->endWriteSlice();
#if (defined(_MSC_VER) && (_MSC_VER < 1300)) // VC++ 6 compiler bug
    SessionException::__write(__os);
#else
    ::debby::slice::SessionException::__write(__os);
#endif
}

void
debby::slice::FileNotExist::__read(::IceInternal::BasicStream* __is, bool __rid)
{
    if(__rid)
    {
	::std::string myId;
	__is->read(myId, false);
    }
    __is->startReadSlice();
    __is->endReadSlice();
#if (defined(_MSC_VER) && (_MSC_VER < 1300)) // VC++ 6 compiler bug
    SessionException::__read(__is, true);
#else
    ::debby::slice::SessionException::__read(__is, true);
#endif
}

void
debby::slice::FileNotExist::__write(const ::Ice::OutputStreamPtr&) const
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "exception debby::slice::FileNotExist was not generated with stream support";
    throw ex;
}

void
debby::slice::FileNotExist::__read(const ::Ice::InputStreamPtr&, bool)
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "exception debby::slice::FileNotExist was not generated with stream support";
    throw ex;
}

struct __F__debby__slice__FileNotExist : public ::IceInternal::UserExceptionFactory
{
    virtual void
    createAndThrow()
    {
	throw ::debby::slice::FileNotExist();
    }
};

static ::IceInternal::UserExceptionFactoryPtr __F__debby__slice__FileNotExist__Ptr = new __F__debby__slice__FileNotExist;

const ::IceInternal::UserExceptionFactoryPtr&
debby::slice::FileNotExist::ice_factory()
{
    return __F__debby__slice__FileNotExist__Ptr;
}

class __F__debby__slice__FileNotExist__Init
{
public:

    __F__debby__slice__FileNotExist__Init()
    {
	::IceInternal::factoryTable->addExceptionFactory("::debby::slice::FileNotExist", ::debby::slice::FileNotExist::ice_factory());
    }

    ~__F__debby__slice__FileNotExist__Init()
    {
	::IceInternal::factoryTable->removeExceptionFactory("::debby::slice::FileNotExist");
    }
};

static __F__debby__slice__FileNotExist__Init __F__debby__slice__FileNotExist__i;

#ifdef __APPLE__
extern "C" { void __F__debby__slice__FileNotExist__initializer() {} }
#endif

debby::slice::UnknownSession::UnknownSession(const ::std::string& __ice_reason) :
#if (defined(_MSC_VER) && (_MSC_VER < 1300)) // VC++ 6 compiler bug
    SessionException(__ice_reason)
#else
    ::debby::slice::SessionException(__ice_reason)
#endif
{
}

static const char* __debby__slice__UnknownSession_name = "debby::slice::UnknownSession";

const ::std::string
debby::slice::UnknownSession::ice_name() const
{
    return __debby__slice__UnknownSession_name;
}

::Ice::Exception*
debby::slice::UnknownSession::ice_clone() const
{
    return new UnknownSession(*this);
}

void
debby::slice::UnknownSession::ice_throw() const
{
    throw *this;
}

void
debby::slice::UnknownSession::__write(::IceInternal::BasicStream* __os) const
{
    __os->write(::std::string("::debby::slice::UnknownSession"), false);
    __os->startWriteSlice();
    __os->endWriteSlice();
#if (defined(_MSC_VER) && (_MSC_VER < 1300)) // VC++ 6 compiler bug
    SessionException::__write(__os);
#else
    ::debby::slice::SessionException::__write(__os);
#endif
}

void
debby::slice::UnknownSession::__read(::IceInternal::BasicStream* __is, bool __rid)
{
    if(__rid)
    {
	::std::string myId;
	__is->read(myId, false);
    }
    __is->startReadSlice();
    __is->endReadSlice();
#if (defined(_MSC_VER) && (_MSC_VER < 1300)) // VC++ 6 compiler bug
    SessionException::__read(__is, true);
#else
    ::debby::slice::SessionException::__read(__is, true);
#endif
}

void
debby::slice::UnknownSession::__write(const ::Ice::OutputStreamPtr&) const
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "exception debby::slice::UnknownSession was not generated with stream support";
    throw ex;
}

void
debby::slice::UnknownSession::__read(const ::Ice::InputStreamPtr&, bool)
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "exception debby::slice::UnknownSession was not generated with stream support";
    throw ex;
}

struct __F__debby__slice__UnknownSession : public ::IceInternal::UserExceptionFactory
{
    virtual void
    createAndThrow()
    {
	throw ::debby::slice::UnknownSession();
    }
};

static ::IceInternal::UserExceptionFactoryPtr __F__debby__slice__UnknownSession__Ptr = new __F__debby__slice__UnknownSession;

const ::IceInternal::UserExceptionFactoryPtr&
debby::slice::UnknownSession::ice_factory()
{
    return __F__debby__slice__UnknownSession__Ptr;
}

class __F__debby__slice__UnknownSession__Init
{
public:

    __F__debby__slice__UnknownSession__Init()
    {
	::IceInternal::factoryTable->addExceptionFactory("::debby::slice::UnknownSession", ::debby::slice::UnknownSession::ice_factory());
    }

    ~__F__debby__slice__UnknownSession__Init()
    {
	::IceInternal::factoryTable->removeExceptionFactory("::debby::slice::UnknownSession");
    }
};

static __F__debby__slice__UnknownSession__Init __F__debby__slice__UnknownSession__i;

#ifdef __APPLE__
extern "C" { void __F__debby__slice__UnknownSession__initializer() {} }
#endif

debby::slice::DuplicateKeepAlive::DuplicateKeepAlive(const ::std::string& __ice_reason) :
#if (defined(_MSC_VER) && (_MSC_VER < 1300)) // VC++ 6 compiler bug
    SessionException(__ice_reason)
#else
    ::debby::slice::SessionException(__ice_reason)
#endif
{
}

static const char* __debby__slice__DuplicateKeepAlive_name = "debby::slice::DuplicateKeepAlive";

const ::std::string
debby::slice::DuplicateKeepAlive::ice_name() const
{
    return __debby__slice__DuplicateKeepAlive_name;
}

::Ice::Exception*
debby::slice::DuplicateKeepAlive::ice_clone() const
{
    return new DuplicateKeepAlive(*this);
}

void
debby::slice::DuplicateKeepAlive::ice_throw() const
{
    throw *this;
}

void
debby::slice::DuplicateKeepAlive::__write(::IceInternal::BasicStream* __os) const
{
    __os->write(::std::string("::debby::slice::DuplicateKeepAlive"), false);
    __os->startWriteSlice();
    __os->endWriteSlice();
#if (defined(_MSC_VER) && (_MSC_VER < 1300)) // VC++ 6 compiler bug
    SessionException::__write(__os);
#else
    ::debby::slice::SessionException::__write(__os);
#endif
}

void
debby::slice::DuplicateKeepAlive::__read(::IceInternal::BasicStream* __is, bool __rid)
{
    if(__rid)
    {
	::std::string myId;
	__is->read(myId, false);
    }
    __is->startReadSlice();
    __is->endReadSlice();
#if (defined(_MSC_VER) && (_MSC_VER < 1300)) // VC++ 6 compiler bug
    SessionException::__read(__is, true);
#else
    ::debby::slice::SessionException::__read(__is, true);
#endif
}

void
debby::slice::DuplicateKeepAlive::__write(const ::Ice::OutputStreamPtr&) const
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "exception debby::slice::DuplicateKeepAlive was not generated with stream support";
    throw ex;
}

void
debby::slice::DuplicateKeepAlive::__read(const ::Ice::InputStreamPtr&, bool)
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "exception debby::slice::DuplicateKeepAlive was not generated with stream support";
    throw ex;
}

struct __F__debby__slice__DuplicateKeepAlive : public ::IceInternal::UserExceptionFactory
{
    virtual void
    createAndThrow()
    {
	throw ::debby::slice::DuplicateKeepAlive();
    }
};

static ::IceInternal::UserExceptionFactoryPtr __F__debby__slice__DuplicateKeepAlive__Ptr = new __F__debby__slice__DuplicateKeepAlive;

const ::IceInternal::UserExceptionFactoryPtr&
debby::slice::DuplicateKeepAlive::ice_factory()
{
    return __F__debby__slice__DuplicateKeepAlive__Ptr;
}

class __F__debby__slice__DuplicateKeepAlive__Init
{
public:

    __F__debby__slice__DuplicateKeepAlive__Init()
    {
	::IceInternal::factoryTable->addExceptionFactory("::debby::slice::DuplicateKeepAlive", ::debby::slice::DuplicateKeepAlive::ice_factory());
    }

    ~__F__debby__slice__DuplicateKeepAlive__Init()
    {
	::IceInternal::factoryTable->removeExceptionFactory("::debby::slice::DuplicateKeepAlive");
    }
};

static __F__debby__slice__DuplicateKeepAlive__Init __F__debby__slice__DuplicateKeepAlive__i;

#ifdef __APPLE__
extern "C" { void __F__debby__slice__DuplicateKeepAlive__initializer() {} }
#endif
