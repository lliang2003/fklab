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

#include <Session.h>
#include <Ice/LocalException.h>
#include <Ice/ObjectFactory.h>
#include <Ice/BasicStream.h>
#include <Ice/Object.h>
#include <IceUtil/Iterator.h>

#ifndef ICE_IGNORE_VERSION
#   if ICE_INT_VERSION / 100 != 301
#       error Ice version mismatch!
#   endif
#   if ICE_INT_VERSION % 100 < 1
#       error Ice patch level mismatch!
#   endif
#endif

static const ::std::string __debby__slice__Session__connect_name = "connect";

static const ::std::string __debby__slice__Session__close_name = "close";

static const ::std::string __debby__slice__Session__keepAlive_name = "keepAlive";

static const ::std::string __debby__slice__Session__regEvent_name = "regEvent";

static const ::std::string __debby__slice__Session__unregEvent_name = "unregEvent";

static const ::std::string __debby__slice__Session__read_name = "read";

static const ::std::string __debby__slice__Session__write_name = "write";

static const ::std::string __debby__slice__Session__create_name = "create";

static const ::std::string __debby__slice__Session__remove_name = "remove";

static const ::std::string __debby__slice__Session__exists_name = "exists";

static const ::std::string __debby__slice__Session__istmp_name = "istmp";

static const ::std::string __debby__slice__Session__list_name = "list";

void
IceInternal::incRef(::debby::slice::Session* p)
{
    p->__incRef();
}

void
IceInternal::decRef(::debby::slice::Session* p)
{
    p->__decRef();
}

void
IceInternal::incRef(::IceProxy::debby::slice::Session* p)
{
    p->__incRef();
}

void
IceInternal::decRef(::IceProxy::debby::slice::Session* p)
{
    p->__decRef();
}

void
debby::slice::__write(::IceInternal::BasicStream* __os, const ::debby::slice::SessionPrx& v)
{
    __os->write(::Ice::ObjectPrx(v));
}

void
debby::slice::__read(::IceInternal::BasicStream* __is, ::debby::slice::SessionPrx& v)
{
    ::Ice::ObjectPrx proxy;
    __is->read(proxy);
    if(!proxy)
    {
	v = 0;
    }
    else
    {
	v = new ::IceProxy::debby::slice::Session;
	v->__copyFrom(proxy);
    }
}

void
debby::slice::__write(::IceInternal::BasicStream* __os, const ::debby::slice::SessionPtr& v)
{
    __os->write(::Ice::ObjectPtr(v));
}

void
debby::slice::__write(::IceInternal::BasicStream* __os, ::debby::slice::EventType v)
{
    __os->write(static_cast< ::Ice::Byte>(v));
}

void
debby::slice::__read(::IceInternal::BasicStream* __is, ::debby::slice::EventType& v)
{
    ::Ice::Byte val;
    __is->read(val);
    v = static_cast< ::debby::slice::EventType>(val);
}

bool
debby::slice::Event::operator==(const Event& __rhs) const
{
    return !operator!=(__rhs);
}

bool
debby::slice::Event::operator!=(const Event& __rhs) const
{
    if(this == &__rhs)
    {
	return false;
    }
    if(type != __rhs.type)
    {
	return true;
    }
    if(path != __rhs.path)
    {
	return true;
    }
    return false;
}

bool
debby::slice::Event::operator<(const Event& __rhs) const
{
    if(this == &__rhs)
    {
	return false;
    }
    if(type < __rhs.type)
    {
	return true;
    }
    else if(__rhs.type < type)
    {
	return false;
    }
    if(path < __rhs.path)
    {
	return true;
    }
    else if(__rhs.path < path)
    {
	return false;
    }
    return false;
}

void
debby::slice::Event::__write(::IceInternal::BasicStream* __os) const
{
    ::debby::slice::__write(__os, type);
    __os->write(path);
}

void
debby::slice::Event::__read(::IceInternal::BasicStream* __is)
{
    ::debby::slice::__read(__is, type);
    __is->read(path);
}

IceAsync::debby::slice::AMD_Session_keepAlive::AMD_Session_keepAlive(::IceInternal::Incoming& in) :
    ::IceInternal::IncomingAsync(in)
{
}

void
IceAsync::debby::slice::AMD_Session_keepAlive::ice_response(const ::debby::slice::Event& __ret)
{
    try
    {
	::IceInternal::BasicStream* __os = this->__os();
	__ret.__write(__os);
    }
    catch(const ::Ice::Exception& __ex)
    {
	__exception(__ex);
	return;
    }
    __response(true);
}

void
IceAsync::debby::slice::AMD_Session_keepAlive::ice_exception(const ::Ice::Exception& ex)
{
    try
    {
	ex.ice_throw();
    }
    catch(const ::debby::slice::DuplicateKeepAlive& __ex)
    {
	__os()->write(__ex);
	__response(false);
    }
    catch(const ::debby::slice::UnknownSession& __ex)
    {
	__os()->write(__ex);
	__response(false);
    }
    catch(const ::Ice::Exception& __ex)
    {
	__exception(__ex);
    }
}

void
IceAsync::debby::slice::AMD_Session_keepAlive::ice_exception(const ::std::exception& ex)
{
    __exception(ex);
}

void
IceAsync::debby::slice::AMD_Session_keepAlive::ice_exception()
{
    __exception();
}

::Ice::Int
IceProxy::debby::slice::Session::connect(const ::std::string& name, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    __checkTwowayOnly(__debby__slice__Session__connect_name);
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::debby::slice::Session* __del = dynamic_cast< ::IceDelegate::debby::slice::Session*>(__delBase.get());
	    return __del->connect(name, __ctx);
	}
	catch(const ::IceInternal::LocalExceptionWrapper& __ex)
	{
	    __handleExceptionWrapper(__ex);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    __handleException(__ex, __cnt);
	}
    }
}

void
IceProxy::debby::slice::Session::close(::Ice::Int handle, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::debby::slice::Session* __del = dynamic_cast< ::IceDelegate::debby::slice::Session*>(__delBase.get());
	    __del->close(handle, __ctx);
	    return;
	}
	catch(const ::IceInternal::LocalExceptionWrapper& __ex)
	{
	    __handleExceptionWrapper(__ex);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    __handleException(__ex, __cnt);
	}
    }
}

::debby::slice::Event
IceProxy::debby::slice::Session::keepAlive(::Ice::Int handle, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    __checkTwowayOnly(__debby__slice__Session__keepAlive_name);
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::debby::slice::Session* __del = dynamic_cast< ::IceDelegate::debby::slice::Session*>(__delBase.get());
	    return __del->keepAlive(handle, __ctx);
	}
	catch(const ::IceInternal::LocalExceptionWrapper& __ex)
	{
	    __handleExceptionWrapper(__ex);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    __handleException(__ex, __cnt);
	}
    }
}

void
IceProxy::debby::slice::Session::regEvent(::Ice::Int handle, const ::debby::slice::Event& e, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    __checkTwowayOnly(__debby__slice__Session__regEvent_name);
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::debby::slice::Session* __del = dynamic_cast< ::IceDelegate::debby::slice::Session*>(__delBase.get());
	    __del->regEvent(handle, e, __ctx);
	    return;
	}
	catch(const ::IceInternal::LocalExceptionWrapper& __ex)
	{
	    __handleExceptionWrapper(__ex);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    __handleException(__ex, __cnt);
	}
    }
}

void
IceProxy::debby::slice::Session::unregEvent(::Ice::Int handle, const ::debby::slice::Event& e, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    __checkTwowayOnly(__debby__slice__Session__unregEvent_name);
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::debby::slice::Session* __del = dynamic_cast< ::IceDelegate::debby::slice::Session*>(__delBase.get());
	    __del->unregEvent(handle, e, __ctx);
	    return;
	}
	catch(const ::IceInternal::LocalExceptionWrapper& __ex)
	{
	    __handleExceptionWrapper(__ex);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    __handleException(__ex, __cnt);
	}
    }
}

::std::string
IceProxy::debby::slice::Session::read(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    __checkTwowayOnly(__debby__slice__Session__read_name);
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::debby::slice::Session* __del = dynamic_cast< ::IceDelegate::debby::slice::Session*>(__delBase.get());
	    return __del->read(handle, path, __ctx);
	}
	catch(const ::IceInternal::LocalExceptionWrapper& __ex)
	{
	    __handleExceptionWrapper(__ex);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    __handleException(__ex, __cnt);
	}
    }
}

void
IceProxy::debby::slice::Session::write(::Ice::Int handle, const ::std::string& path, const ::std::string& data, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    __checkTwowayOnly(__debby__slice__Session__write_name);
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::debby::slice::Session* __del = dynamic_cast< ::IceDelegate::debby::slice::Session*>(__delBase.get());
	    __del->write(handle, path, data, __ctx);
	    return;
	}
	catch(const ::IceInternal::LocalExceptionWrapper& __ex)
	{
	    __handleExceptionWrapper(__ex);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    __handleException(__ex, __cnt);
	}
    }
}

bool
IceProxy::debby::slice::Session::create(::Ice::Int handle, const ::std::string& path, bool temp, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    __checkTwowayOnly(__debby__slice__Session__create_name);
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::debby::slice::Session* __del = dynamic_cast< ::IceDelegate::debby::slice::Session*>(__delBase.get());
	    return __del->create(handle, path, temp, __ctx);
	}
	catch(const ::IceInternal::LocalExceptionWrapper& __ex)
	{
	    __handleExceptionWrapper(__ex);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    __handleException(__ex, __cnt);
	}
    }
}

void
IceProxy::debby::slice::Session::remove(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    __checkTwowayOnly(__debby__slice__Session__remove_name);
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::debby::slice::Session* __del = dynamic_cast< ::IceDelegate::debby::slice::Session*>(__delBase.get());
	    __del->remove(handle, path, __ctx);
	    return;
	}
	catch(const ::IceInternal::LocalExceptionWrapper& __ex)
	{
	    __handleExceptionWrapper(__ex);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    __handleException(__ex, __cnt);
	}
    }
}

bool
IceProxy::debby::slice::Session::exists(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    __checkTwowayOnly(__debby__slice__Session__exists_name);
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::debby::slice::Session* __del = dynamic_cast< ::IceDelegate::debby::slice::Session*>(__delBase.get());
	    return __del->exists(handle, path, __ctx);
	}
	catch(const ::IceInternal::LocalExceptionWrapper& __ex)
	{
	    __handleExceptionWrapper(__ex);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    __handleException(__ex, __cnt);
	}
    }
}

bool
IceProxy::debby::slice::Session::istmp(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    __checkTwowayOnly(__debby__slice__Session__istmp_name);
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::debby::slice::Session* __del = dynamic_cast< ::IceDelegate::debby::slice::Session*>(__delBase.get());
	    return __del->istmp(handle, path, __ctx);
	}
	catch(const ::IceInternal::LocalExceptionWrapper& __ex)
	{
	    __handleExceptionWrapper(__ex);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    __handleException(__ex, __cnt);
	}
    }
}

::debby::slice::Files
IceProxy::debby::slice::Session::list(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    __checkTwowayOnly(__debby__slice__Session__list_name);
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::debby::slice::Session* __del = dynamic_cast< ::IceDelegate::debby::slice::Session*>(__delBase.get());
	    return __del->list(handle, path, __ctx);
	}
	catch(const ::IceInternal::LocalExceptionWrapper& __ex)
	{
	    __handleExceptionWrapper(__ex);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    __handleException(__ex, __cnt);
	}
    }
}

const ::std::string&
IceProxy::debby::slice::Session::ice_staticId()
{
    return ::debby::slice::Session::ice_staticId();
}

::IceInternal::Handle< ::IceDelegateM::Ice::Object>
IceProxy::debby::slice::Session::__createDelegateM()
{
    return ::IceInternal::Handle< ::IceDelegateM::Ice::Object>(new ::IceDelegateM::debby::slice::Session);
}

::IceInternal::Handle< ::IceDelegateD::Ice::Object>
IceProxy::debby::slice::Session::__createDelegateD()
{
    return ::IceInternal::Handle< ::IceDelegateD::Ice::Object>(new ::IceDelegateD::debby::slice::Session);
}

bool
IceProxy::debby::slice::operator==(const ::IceProxy::debby::slice::Session& l, const ::IceProxy::debby::slice::Session& r)
{
    return static_cast<const ::IceProxy::Ice::Object&>(l) == static_cast<const ::IceProxy::Ice::Object&>(r);
}

bool
IceProxy::debby::slice::operator!=(const ::IceProxy::debby::slice::Session& l, const ::IceProxy::debby::slice::Session& r)
{
    return static_cast<const ::IceProxy::Ice::Object&>(l) != static_cast<const ::IceProxy::Ice::Object&>(r);
}

bool
IceProxy::debby::slice::operator<(const ::IceProxy::debby::slice::Session& l, const ::IceProxy::debby::slice::Session& r)
{
    return static_cast<const ::IceProxy::Ice::Object&>(l) < static_cast<const ::IceProxy::Ice::Object&>(r);
}

bool
IceProxy::debby::slice::operator<=(const ::IceProxy::debby::slice::Session& l, const ::IceProxy::debby::slice::Session& r)
{
    return l < r || l == r;
}

bool
IceProxy::debby::slice::operator>(const ::IceProxy::debby::slice::Session& l, const ::IceProxy::debby::slice::Session& r)
{
    return !(l < r) && !(l == r);
}

bool
IceProxy::debby::slice::operator>=(const ::IceProxy::debby::slice::Session& l, const ::IceProxy::debby::slice::Session& r)
{
    return !(l < r);
}

::Ice::Int
IceDelegateM::debby::slice::Session::connect(const ::std::string& name, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __debby__slice__Session__connect_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	__os->write(name);
    }
    catch(const ::Ice::LocalException& __ex)
    {
	__og.abort(__ex);
    }
    bool __ok = __og.invoke();
    try
    {
	::IceInternal::BasicStream* __is = __og.is();
	if(!__ok)
	{
	    try
	    {
		__is->throwException();
	    }
	    catch(const ::Ice::UserException& __ex)
	    {
		throw ::Ice::UnknownUserException(__FILE__, __LINE__, __ex.ice_name());
	    }
	}
	::Ice::Int __ret;
	__is->read(__ret);
	return __ret;
    }
    catch(const ::Ice::LocalException& __ex)
    {
	throw ::IceInternal::LocalExceptionWrapper(__ex, false);
    }
}

void
IceDelegateM::debby::slice::Session::close(::Ice::Int handle, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __debby__slice__Session__close_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	__os->write(handle);
    }
    catch(const ::Ice::LocalException& __ex)
    {
	__og.abort(__ex);
    }
    bool __ok = __og.invoke();
    try
    {
	::IceInternal::BasicStream* __is = __og.is();
	if(!__ok)
	{
	    try
	    {
		__is->throwException();
	    }
	    catch(const ::Ice::UserException& __ex)
	    {
		throw ::Ice::UnknownUserException(__FILE__, __LINE__, __ex.ice_name());
	    }
	}
    }
    catch(const ::Ice::LocalException& __ex)
    {
	throw ::IceInternal::LocalExceptionWrapper(__ex, false);
    }
}

::debby::slice::Event
IceDelegateM::debby::slice::Session::keepAlive(::Ice::Int handle, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __debby__slice__Session__keepAlive_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	__os->write(handle);
    }
    catch(const ::Ice::LocalException& __ex)
    {
	__og.abort(__ex);
    }
    bool __ok = __og.invoke();
    try
    {
	::IceInternal::BasicStream* __is = __og.is();
	if(!__ok)
	{
	    try
	    {
		__is->throwException();
	    }
	    catch(const ::debby::slice::DuplicateKeepAlive&)
	    {
		throw;
	    }
	    catch(const ::debby::slice::UnknownSession&)
	    {
		throw;
	    }
	    catch(const ::Ice::UserException& __ex)
	    {
		throw ::Ice::UnknownUserException(__FILE__, __LINE__, __ex.ice_name());
	    }
	}
	::debby::slice::Event __ret;
	__ret.__read(__is);
	return __ret;
    }
    catch(const ::Ice::LocalException& __ex)
    {
	throw ::IceInternal::LocalExceptionWrapper(__ex, false);
    }
}

void
IceDelegateM::debby::slice::Session::regEvent(::Ice::Int handle, const ::debby::slice::Event& e, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __debby__slice__Session__regEvent_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	__os->write(handle);
	e.__write(__os);
    }
    catch(const ::Ice::LocalException& __ex)
    {
	__og.abort(__ex);
    }
    bool __ok = __og.invoke();
    try
    {
	::IceInternal::BasicStream* __is = __og.is();
	if(!__ok)
	{
	    try
	    {
		__is->throwException();
	    }
	    catch(const ::debby::slice::UnknownSession&)
	    {
		throw;
	    }
	    catch(const ::Ice::UserException& __ex)
	    {
		throw ::Ice::UnknownUserException(__FILE__, __LINE__, __ex.ice_name());
	    }
	}
    }
    catch(const ::Ice::LocalException& __ex)
    {
	throw ::IceInternal::LocalExceptionWrapper(__ex, false);
    }
}

void
IceDelegateM::debby::slice::Session::unregEvent(::Ice::Int handle, const ::debby::slice::Event& e, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __debby__slice__Session__unregEvent_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	__os->write(handle);
	e.__write(__os);
    }
    catch(const ::Ice::LocalException& __ex)
    {
	__og.abort(__ex);
    }
    bool __ok = __og.invoke();
    try
    {
	::IceInternal::BasicStream* __is = __og.is();
	if(!__ok)
	{
	    try
	    {
		__is->throwException();
	    }
	    catch(const ::debby::slice::UnknownSession&)
	    {
		throw;
	    }
	    catch(const ::Ice::UserException& __ex)
	    {
		throw ::Ice::UnknownUserException(__FILE__, __LINE__, __ex.ice_name());
	    }
	}
    }
    catch(const ::Ice::LocalException& __ex)
    {
	throw ::IceInternal::LocalExceptionWrapper(__ex, false);
    }
}

::std::string
IceDelegateM::debby::slice::Session::read(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __debby__slice__Session__read_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	__os->write(handle);
	__os->write(path);
    }
    catch(const ::Ice::LocalException& __ex)
    {
	__og.abort(__ex);
    }
    bool __ok = __og.invoke();
    try
    {
	::IceInternal::BasicStream* __is = __og.is();
	if(!__ok)
	{
	    try
	    {
		__is->throwException();
	    }
	    catch(const ::debby::slice::FileNotExist&)
	    {
		throw;
	    }
	    catch(const ::debby::slice::UnknownSession&)
	    {
		throw;
	    }
	    catch(const ::Ice::UserException& __ex)
	    {
		throw ::Ice::UnknownUserException(__FILE__, __LINE__, __ex.ice_name());
	    }
	}
	::std::string __ret;
	__is->read(__ret);
	return __ret;
    }
    catch(const ::Ice::LocalException& __ex)
    {
	throw ::IceInternal::LocalExceptionWrapper(__ex, false);
    }
}

void
IceDelegateM::debby::slice::Session::write(::Ice::Int handle, const ::std::string& path, const ::std::string& data, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __debby__slice__Session__write_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	__os->write(handle);
	__os->write(path);
	__os->write(data);
    }
    catch(const ::Ice::LocalException& __ex)
    {
	__og.abort(__ex);
    }
    bool __ok = __og.invoke();
    try
    {
	::IceInternal::BasicStream* __is = __og.is();
	if(!__ok)
	{
	    try
	    {
		__is->throwException();
	    }
	    catch(const ::debby::slice::FileNotExist&)
	    {
		throw;
	    }
	    catch(const ::debby::slice::UnknownSession&)
	    {
		throw;
	    }
	    catch(const ::Ice::UserException& __ex)
	    {
		throw ::Ice::UnknownUserException(__FILE__, __LINE__, __ex.ice_name());
	    }
	}
    }
    catch(const ::Ice::LocalException& __ex)
    {
	throw ::IceInternal::LocalExceptionWrapper(__ex, false);
    }
}

bool
IceDelegateM::debby::slice::Session::create(::Ice::Int handle, const ::std::string& path, bool temp, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __debby__slice__Session__create_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	__os->write(handle);
	__os->write(path);
	__os->write(temp);
    }
    catch(const ::Ice::LocalException& __ex)
    {
	__og.abort(__ex);
    }
    bool __ok = __og.invoke();
    try
    {
	::IceInternal::BasicStream* __is = __og.is();
	if(!__ok)
	{
	    try
	    {
		__is->throwException();
	    }
	    catch(const ::debby::slice::UnknownSession&)
	    {
		throw;
	    }
	    catch(const ::Ice::UserException& __ex)
	    {
		throw ::Ice::UnknownUserException(__FILE__, __LINE__, __ex.ice_name());
	    }
	}
	bool __ret;
	__is->read(__ret);
	return __ret;
    }
    catch(const ::Ice::LocalException& __ex)
    {
	throw ::IceInternal::LocalExceptionWrapper(__ex, false);
    }
}

void
IceDelegateM::debby::slice::Session::remove(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __debby__slice__Session__remove_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	__os->write(handle);
	__os->write(path);
    }
    catch(const ::Ice::LocalException& __ex)
    {
	__og.abort(__ex);
    }
    bool __ok = __og.invoke();
    try
    {
	::IceInternal::BasicStream* __is = __og.is();
	if(!__ok)
	{
	    try
	    {
		__is->throwException();
	    }
	    catch(const ::debby::slice::UnknownSession&)
	    {
		throw;
	    }
	    catch(const ::Ice::UserException& __ex)
	    {
		throw ::Ice::UnknownUserException(__FILE__, __LINE__, __ex.ice_name());
	    }
	}
    }
    catch(const ::Ice::LocalException& __ex)
    {
	throw ::IceInternal::LocalExceptionWrapper(__ex, false);
    }
}

bool
IceDelegateM::debby::slice::Session::exists(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __debby__slice__Session__exists_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	__os->write(handle);
	__os->write(path);
    }
    catch(const ::Ice::LocalException& __ex)
    {
	__og.abort(__ex);
    }
    bool __ok = __og.invoke();
    try
    {
	::IceInternal::BasicStream* __is = __og.is();
	if(!__ok)
	{
	    try
	    {
		__is->throwException();
	    }
	    catch(const ::debby::slice::UnknownSession&)
	    {
		throw;
	    }
	    catch(const ::Ice::UserException& __ex)
	    {
		throw ::Ice::UnknownUserException(__FILE__, __LINE__, __ex.ice_name());
	    }
	}
	bool __ret;
	__is->read(__ret);
	return __ret;
    }
    catch(const ::Ice::LocalException& __ex)
    {
	throw ::IceInternal::LocalExceptionWrapper(__ex, false);
    }
}

bool
IceDelegateM::debby::slice::Session::istmp(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __debby__slice__Session__istmp_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	__os->write(handle);
	__os->write(path);
    }
    catch(const ::Ice::LocalException& __ex)
    {
	__og.abort(__ex);
    }
    bool __ok = __og.invoke();
    try
    {
	::IceInternal::BasicStream* __is = __og.is();
	if(!__ok)
	{
	    try
	    {
		__is->throwException();
	    }
	    catch(const ::debby::slice::UnknownSession&)
	    {
		throw;
	    }
	    catch(const ::Ice::UserException& __ex)
	    {
		throw ::Ice::UnknownUserException(__FILE__, __LINE__, __ex.ice_name());
	    }
	}
	bool __ret;
	__is->read(__ret);
	return __ret;
    }
    catch(const ::Ice::LocalException& __ex)
    {
	throw ::IceInternal::LocalExceptionWrapper(__ex, false);
    }
}

::debby::slice::Files
IceDelegateM::debby::slice::Session::list(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __debby__slice__Session__list_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	__os->write(handle);
	__os->write(path);
    }
    catch(const ::Ice::LocalException& __ex)
    {
	__og.abort(__ex);
    }
    bool __ok = __og.invoke();
    try
    {
	::IceInternal::BasicStream* __is = __og.is();
	if(!__ok)
	{
	    try
	    {
		__is->throwException();
	    }
	    catch(const ::debby::slice::UnknownSession&)
	    {
		throw;
	    }
	    catch(const ::Ice::UserException& __ex)
	    {
		throw ::Ice::UnknownUserException(__FILE__, __LINE__, __ex.ice_name());
	    }
	}
	::debby::slice::Files __ret;
	__is->read(__ret);
	return __ret;
    }
    catch(const ::Ice::LocalException& __ex)
    {
	throw ::IceInternal::LocalExceptionWrapper(__ex, false);
    }
}

::Ice::Int
IceDelegateD::debby::slice::Session::connect(const ::std::string& name, const ::Ice::Context& __context)
{
    ::Ice::Current __current;
    __initCurrent(__current, __debby__slice__Session__connect_name, ::Ice::Normal, __context);
    while(true)
    {
	::IceInternal::Direct __direct(__current);
	::debby::slice::Session* __servant = dynamic_cast< ::debby::slice::Session*>(__direct.servant().get());
	if(!__servant)
	{
	    ::Ice::OperationNotExistException __opEx(__FILE__, __LINE__);
	    __opEx.id = __current.id;
	    __opEx.facet = __current.facet;
	    __opEx.operation = __current.operation;
	    throw __opEx;
	}
	try
	{
	    return __servant->connect(name, __current);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    throw ::IceInternal::LocalExceptionWrapper(__ex, false);
	}
    }
}

void
IceDelegateD::debby::slice::Session::close(::Ice::Int handle, const ::Ice::Context& __context)
{
    ::Ice::Current __current;
    __initCurrent(__current, __debby__slice__Session__close_name, ::Ice::Normal, __context);
    while(true)
    {
	::IceInternal::Direct __direct(__current);
	::debby::slice::Session* __servant = dynamic_cast< ::debby::slice::Session*>(__direct.servant().get());
	if(!__servant)
	{
	    ::Ice::OperationNotExistException __opEx(__FILE__, __LINE__);
	    __opEx.id = __current.id;
	    __opEx.facet = __current.facet;
	    __opEx.operation = __current.operation;
	    throw __opEx;
	}
	try
	{
	    __servant->close(handle, __current);
	    return;
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    throw ::IceInternal::LocalExceptionWrapper(__ex, false);
	}
    }
}

::debby::slice::Event
IceDelegateD::debby::slice::Session::keepAlive(::Ice::Int, const ::Ice::Context&)
{
    throw ::Ice::CollocationOptimizationException(__FILE__, __LINE__);
    return ::debby::slice::Event(); // to avoid a warning with some compilers;
}

void
IceDelegateD::debby::slice::Session::regEvent(::Ice::Int handle, const ::debby::slice::Event& e, const ::Ice::Context& __context)
{
    ::Ice::Current __current;
    __initCurrent(__current, __debby__slice__Session__regEvent_name, ::Ice::Normal, __context);
    while(true)
    {
	::IceInternal::Direct __direct(__current);
	::debby::slice::Session* __servant = dynamic_cast< ::debby::slice::Session*>(__direct.servant().get());
	if(!__servant)
	{
	    ::Ice::OperationNotExistException __opEx(__FILE__, __LINE__);
	    __opEx.id = __current.id;
	    __opEx.facet = __current.facet;
	    __opEx.operation = __current.operation;
	    throw __opEx;
	}
	try
	{
	    __servant->regEvent(handle, e, __current);
	    return;
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    throw ::IceInternal::LocalExceptionWrapper(__ex, false);
	}
    }
}

void
IceDelegateD::debby::slice::Session::unregEvent(::Ice::Int handle, const ::debby::slice::Event& e, const ::Ice::Context& __context)
{
    ::Ice::Current __current;
    __initCurrent(__current, __debby__slice__Session__unregEvent_name, ::Ice::Normal, __context);
    while(true)
    {
	::IceInternal::Direct __direct(__current);
	::debby::slice::Session* __servant = dynamic_cast< ::debby::slice::Session*>(__direct.servant().get());
	if(!__servant)
	{
	    ::Ice::OperationNotExistException __opEx(__FILE__, __LINE__);
	    __opEx.id = __current.id;
	    __opEx.facet = __current.facet;
	    __opEx.operation = __current.operation;
	    throw __opEx;
	}
	try
	{
	    __servant->unregEvent(handle, e, __current);
	    return;
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    throw ::IceInternal::LocalExceptionWrapper(__ex, false);
	}
    }
}

::std::string
IceDelegateD::debby::slice::Session::read(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __context)
{
    ::Ice::Current __current;
    __initCurrent(__current, __debby__slice__Session__read_name, ::Ice::Normal, __context);
    while(true)
    {
	::IceInternal::Direct __direct(__current);
	::debby::slice::Session* __servant = dynamic_cast< ::debby::slice::Session*>(__direct.servant().get());
	if(!__servant)
	{
	    ::Ice::OperationNotExistException __opEx(__FILE__, __LINE__);
	    __opEx.id = __current.id;
	    __opEx.facet = __current.facet;
	    __opEx.operation = __current.operation;
	    throw __opEx;
	}
	try
	{
	    return __servant->read(handle, path, __current);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    throw ::IceInternal::LocalExceptionWrapper(__ex, false);
	}
    }
}

void
IceDelegateD::debby::slice::Session::write(::Ice::Int handle, const ::std::string& path, const ::std::string& data, const ::Ice::Context& __context)
{
    ::Ice::Current __current;
    __initCurrent(__current, __debby__slice__Session__write_name, ::Ice::Normal, __context);
    while(true)
    {
	::IceInternal::Direct __direct(__current);
	::debby::slice::Session* __servant = dynamic_cast< ::debby::slice::Session*>(__direct.servant().get());
	if(!__servant)
	{
	    ::Ice::OperationNotExistException __opEx(__FILE__, __LINE__);
	    __opEx.id = __current.id;
	    __opEx.facet = __current.facet;
	    __opEx.operation = __current.operation;
	    throw __opEx;
	}
	try
	{
	    __servant->write(handle, path, data, __current);
	    return;
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    throw ::IceInternal::LocalExceptionWrapper(__ex, false);
	}
    }
}

bool
IceDelegateD::debby::slice::Session::create(::Ice::Int handle, const ::std::string& path, bool temp, const ::Ice::Context& __context)
{
    ::Ice::Current __current;
    __initCurrent(__current, __debby__slice__Session__create_name, ::Ice::Normal, __context);
    while(true)
    {
	::IceInternal::Direct __direct(__current);
	::debby::slice::Session* __servant = dynamic_cast< ::debby::slice::Session*>(__direct.servant().get());
	if(!__servant)
	{
	    ::Ice::OperationNotExistException __opEx(__FILE__, __LINE__);
	    __opEx.id = __current.id;
	    __opEx.facet = __current.facet;
	    __opEx.operation = __current.operation;
	    throw __opEx;
	}
	try
	{
	    return __servant->create(handle, path, temp, __current);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    throw ::IceInternal::LocalExceptionWrapper(__ex, false);
	}
    }
}

void
IceDelegateD::debby::slice::Session::remove(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __context)
{
    ::Ice::Current __current;
    __initCurrent(__current, __debby__slice__Session__remove_name, ::Ice::Normal, __context);
    while(true)
    {
	::IceInternal::Direct __direct(__current);
	::debby::slice::Session* __servant = dynamic_cast< ::debby::slice::Session*>(__direct.servant().get());
	if(!__servant)
	{
	    ::Ice::OperationNotExistException __opEx(__FILE__, __LINE__);
	    __opEx.id = __current.id;
	    __opEx.facet = __current.facet;
	    __opEx.operation = __current.operation;
	    throw __opEx;
	}
	try
	{
	    __servant->remove(handle, path, __current);
	    return;
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    throw ::IceInternal::LocalExceptionWrapper(__ex, false);
	}
    }
}

bool
IceDelegateD::debby::slice::Session::exists(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __context)
{
    ::Ice::Current __current;
    __initCurrent(__current, __debby__slice__Session__exists_name, ::Ice::Normal, __context);
    while(true)
    {
	::IceInternal::Direct __direct(__current);
	::debby::slice::Session* __servant = dynamic_cast< ::debby::slice::Session*>(__direct.servant().get());
	if(!__servant)
	{
	    ::Ice::OperationNotExistException __opEx(__FILE__, __LINE__);
	    __opEx.id = __current.id;
	    __opEx.facet = __current.facet;
	    __opEx.operation = __current.operation;
	    throw __opEx;
	}
	try
	{
	    return __servant->exists(handle, path, __current);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    throw ::IceInternal::LocalExceptionWrapper(__ex, false);
	}
    }
}

bool
IceDelegateD::debby::slice::Session::istmp(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __context)
{
    ::Ice::Current __current;
    __initCurrent(__current, __debby__slice__Session__istmp_name, ::Ice::Normal, __context);
    while(true)
    {
	::IceInternal::Direct __direct(__current);
	::debby::slice::Session* __servant = dynamic_cast< ::debby::slice::Session*>(__direct.servant().get());
	if(!__servant)
	{
	    ::Ice::OperationNotExistException __opEx(__FILE__, __LINE__);
	    __opEx.id = __current.id;
	    __opEx.facet = __current.facet;
	    __opEx.operation = __current.operation;
	    throw __opEx;
	}
	try
	{
	    return __servant->istmp(handle, path, __current);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    throw ::IceInternal::LocalExceptionWrapper(__ex, false);
	}
    }
}

::debby::slice::Files
IceDelegateD::debby::slice::Session::list(::Ice::Int handle, const ::std::string& path, const ::Ice::Context& __context)
{
    ::Ice::Current __current;
    __initCurrent(__current, __debby__slice__Session__list_name, ::Ice::Normal, __context);
    while(true)
    {
	::IceInternal::Direct __direct(__current);
	::debby::slice::Session* __servant = dynamic_cast< ::debby::slice::Session*>(__direct.servant().get());
	if(!__servant)
	{
	    ::Ice::OperationNotExistException __opEx(__FILE__, __LINE__);
	    __opEx.id = __current.id;
	    __opEx.facet = __current.facet;
	    __opEx.operation = __current.operation;
	    throw __opEx;
	}
	try
	{
	    return __servant->list(handle, path, __current);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    throw ::IceInternal::LocalExceptionWrapper(__ex, false);
	}
    }
}

::Ice::ObjectPtr
debby::slice::Session::ice_clone() const
{
    throw ::Ice::CloneNotImplementedException(__FILE__, __LINE__);
    return 0; // to avoid a warning with some compilers
}

static const ::std::string __debby__slice__Session_ids[2] =
{
    "::Ice::Object",
    "::debby::slice::Session"
};

bool
debby::slice::Session::ice_isA(const ::std::string& _s, const ::Ice::Current&) const
{
    return ::std::binary_search(__debby__slice__Session_ids, __debby__slice__Session_ids + 2, _s);
}

::std::vector< ::std::string>
debby::slice::Session::ice_ids(const ::Ice::Current&) const
{
    return ::std::vector< ::std::string>(&__debby__slice__Session_ids[0], &__debby__slice__Session_ids[2]);
}

const ::std::string&
debby::slice::Session::ice_id(const ::Ice::Current&) const
{
    return __debby__slice__Session_ids[1];
}

const ::std::string&
debby::slice::Session::ice_staticId()
{
    return __debby__slice__Session_ids[1];
}

::IceInternal::DispatchStatus
debby::slice::Session::___connect(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::IceInternal::BasicStream* __os = __inS.os();
    ::std::string name;
    __is->read(name);
    ::Ice::Int __ret = connect(name, __current);
    __os->write(__ret);
    return ::IceInternal::DispatchOK;
}

::IceInternal::DispatchStatus
debby::slice::Session::___close(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::Ice::Int handle;
    __is->read(handle);
    close(handle, __current);
    return ::IceInternal::DispatchOK;
}

::IceInternal::DispatchStatus
debby::slice::Session::___keepAlive(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::Ice::Int handle;
    __is->read(handle);
    ::debby::slice::AMD_Session_keepAlivePtr __cb = new IceAsync::debby::slice::AMD_Session_keepAlive(__inS);
    try
    {
	keepAlive_async(__cb, handle, __current);
    }
    catch(const ::Ice::Exception& __ex)
    {
	__cb->ice_exception(__ex);
    }
    catch(const ::std::exception& __ex)
    {
	__cb->ice_exception(__ex);
    }
    catch(...)
    {
	__cb->ice_exception();
    }
    return ::IceInternal::DispatchAsync;
}

::IceInternal::DispatchStatus
debby::slice::Session::___regEvent(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::IceInternal::BasicStream* __os = __inS.os();
    ::Ice::Int handle;
    ::debby::slice::Event e;
    __is->read(handle);
    e.__read(__is);
    try
    {
	regEvent(handle, e, __current);
    }
    catch(const ::debby::slice::UnknownSession& __ex)
    {
	__os->write(__ex);
	return ::IceInternal::DispatchUserException;
    }
    return ::IceInternal::DispatchOK;
}

::IceInternal::DispatchStatus
debby::slice::Session::___unregEvent(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::IceInternal::BasicStream* __os = __inS.os();
    ::Ice::Int handle;
    ::debby::slice::Event e;
    __is->read(handle);
    e.__read(__is);
    try
    {
	unregEvent(handle, e, __current);
    }
    catch(const ::debby::slice::UnknownSession& __ex)
    {
	__os->write(__ex);
	return ::IceInternal::DispatchUserException;
    }
    return ::IceInternal::DispatchOK;
}

::IceInternal::DispatchStatus
debby::slice::Session::___read(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::IceInternal::BasicStream* __os = __inS.os();
    ::Ice::Int handle;
    ::std::string path;
    __is->read(handle);
    __is->read(path);
    try
    {
	::std::string __ret = read(handle, path, __current);
	__os->write(__ret);
    }
    catch(const ::debby::slice::FileNotExist& __ex)
    {
	__os->write(__ex);
	return ::IceInternal::DispatchUserException;
    }
    catch(const ::debby::slice::UnknownSession& __ex)
    {
	__os->write(__ex);
	return ::IceInternal::DispatchUserException;
    }
    return ::IceInternal::DispatchOK;
}

::IceInternal::DispatchStatus
debby::slice::Session::___write(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::IceInternal::BasicStream* __os = __inS.os();
    ::Ice::Int handle;
    ::std::string path;
    ::std::string data;
    __is->read(handle);
    __is->read(path);
    __is->read(data);
    try
    {
	write(handle, path, data, __current);
    }
    catch(const ::debby::slice::FileNotExist& __ex)
    {
	__os->write(__ex);
	return ::IceInternal::DispatchUserException;
    }
    catch(const ::debby::slice::UnknownSession& __ex)
    {
	__os->write(__ex);
	return ::IceInternal::DispatchUserException;
    }
    return ::IceInternal::DispatchOK;
}

::IceInternal::DispatchStatus
debby::slice::Session::___create(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::IceInternal::BasicStream* __os = __inS.os();
    ::Ice::Int handle;
    ::std::string path;
    bool temp;
    __is->read(handle);
    __is->read(path);
    __is->read(temp);
    try
    {
	bool __ret = create(handle, path, temp, __current);
	__os->write(__ret);
    }
    catch(const ::debby::slice::UnknownSession& __ex)
    {
	__os->write(__ex);
	return ::IceInternal::DispatchUserException;
    }
    return ::IceInternal::DispatchOK;
}

::IceInternal::DispatchStatus
debby::slice::Session::___remove(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::IceInternal::BasicStream* __os = __inS.os();
    ::Ice::Int handle;
    ::std::string path;
    __is->read(handle);
    __is->read(path);
    try
    {
	remove(handle, path, __current);
    }
    catch(const ::debby::slice::UnknownSession& __ex)
    {
	__os->write(__ex);
	return ::IceInternal::DispatchUserException;
    }
    return ::IceInternal::DispatchOK;
}

::IceInternal::DispatchStatus
debby::slice::Session::___exists(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::IceInternal::BasicStream* __os = __inS.os();
    ::Ice::Int handle;
    ::std::string path;
    __is->read(handle);
    __is->read(path);
    try
    {
	bool __ret = exists(handle, path, __current);
	__os->write(__ret);
    }
    catch(const ::debby::slice::UnknownSession& __ex)
    {
	__os->write(__ex);
	return ::IceInternal::DispatchUserException;
    }
    return ::IceInternal::DispatchOK;
}

::IceInternal::DispatchStatus
debby::slice::Session::___istmp(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::IceInternal::BasicStream* __os = __inS.os();
    ::Ice::Int handle;
    ::std::string path;
    __is->read(handle);
    __is->read(path);
    try
    {
	bool __ret = istmp(handle, path, __current);
	__os->write(__ret);
    }
    catch(const ::debby::slice::UnknownSession& __ex)
    {
	__os->write(__ex);
	return ::IceInternal::DispatchUserException;
    }
    return ::IceInternal::DispatchOK;
}

::IceInternal::DispatchStatus
debby::slice::Session::___list(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::IceInternal::BasicStream* __os = __inS.os();
    ::Ice::Int handle;
    ::std::string path;
    __is->read(handle);
    __is->read(path);
    try
    {
	::debby::slice::Files __ret = list(handle, path, __current);
	if(__ret.size() == 0)
	{
	    __os->writeSize(0);
	}
	else
	{
	    __os->write(&__ret[0], &__ret[0] + __ret.size());
	}
    }
    catch(const ::debby::slice::UnknownSession& __ex)
    {
	__os->write(__ex);
	return ::IceInternal::DispatchUserException;
    }
    return ::IceInternal::DispatchOK;
}

static ::std::string __debby__slice__Session_all[] =
{
    "close",
    "connect",
    "create",
    "exists",
    "ice_id",
    "ice_ids",
    "ice_isA",
    "ice_ping",
    "istmp",
    "keepAlive",
    "list",
    "read",
    "regEvent",
    "remove",
    "unregEvent",
    "write"
};

::IceInternal::DispatchStatus
debby::slice::Session::__dispatch(::IceInternal::Incoming& in, const ::Ice::Current& current)
{
    ::std::pair< ::std::string*, ::std::string*> r = ::std::equal_range(__debby__slice__Session_all, __debby__slice__Session_all + 16, current.operation);
    if(r.first == r.second)
    {
	return ::IceInternal::DispatchOperationNotExist;
    }

    switch(r.first - __debby__slice__Session_all)
    {
	case 0:
	{
	    return ___close(in, current);
	}
	case 1:
	{
	    return ___connect(in, current);
	}
	case 2:
	{
	    return ___create(in, current);
	}
	case 3:
	{
	    return ___exists(in, current);
	}
	case 4:
	{
	    return ___ice_id(in, current);
	}
	case 5:
	{
	    return ___ice_ids(in, current);
	}
	case 6:
	{
	    return ___ice_isA(in, current);
	}
	case 7:
	{
	    return ___ice_ping(in, current);
	}
	case 8:
	{
	    return ___istmp(in, current);
	}
	case 9:
	{
	    return ___keepAlive(in, current);
	}
	case 10:
	{
	    return ___list(in, current);
	}
	case 11:
	{
	    return ___read(in, current);
	}
	case 12:
	{
	    return ___regEvent(in, current);
	}
	case 13:
	{
	    return ___remove(in, current);
	}
	case 14:
	{
	    return ___unregEvent(in, current);
	}
	case 15:
	{
	    return ___write(in, current);
	}
    }

    assert(false);
    return ::IceInternal::DispatchOperationNotExist;
}

void
debby::slice::Session::__write(::IceInternal::BasicStream* __os) const
{
    __os->writeTypeId(ice_staticId());
    __os->startWriteSlice();
    __os->endWriteSlice();
#if defined(_MSC_VER) && (_MSC_VER < 1300) // VC++ 6 compiler bug
    Object::__write(__os);
#else
    ::Ice::Object::__write(__os);
#endif
}

void
debby::slice::Session::__read(::IceInternal::BasicStream* __is, bool __rid)
{
    if(__rid)
    {
	::std::string myId;
	__is->readTypeId(myId);
    }
    __is->startReadSlice();
    __is->endReadSlice();
#if defined(_MSC_VER) && (_MSC_VER < 1300) // VC++ 6 compiler bug
    Object::__read(__is, true);
#else
    ::Ice::Object::__read(__is, true);
#endif
}

void
debby::slice::Session::__write(const ::Ice::OutputStreamPtr&) const
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "type debby::slice::Session was not generated with stream support";
    throw ex;
}

void
debby::slice::Session::__read(const ::Ice::InputStreamPtr&, bool)
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "type debby::slice::Session was not generated with stream support";
    throw ex;
}

void 
debby::slice::__patch__SessionPtr(void* __addr, ::Ice::ObjectPtr& v)
{
    ::debby::slice::SessionPtr* p = static_cast< ::debby::slice::SessionPtr*>(__addr);
    assert(p);
    *p = ::debby::slice::SessionPtr::dynamicCast(v);
    if(v && !*p)
    {
	::Ice::NoObjectFactoryException e(__FILE__, __LINE__);
	e.type = ::debby::slice::Session::ice_staticId();
	throw e;
    }
}

bool
debby::slice::operator==(const ::debby::slice::Session& l, const ::debby::slice::Session& r)
{
    return static_cast<const ::Ice::Object&>(l) == static_cast<const ::Ice::Object&>(r);
}

bool
debby::slice::operator!=(const ::debby::slice::Session& l, const ::debby::slice::Session& r)
{
    return static_cast<const ::Ice::Object&>(l) != static_cast<const ::Ice::Object&>(r);
}

bool
debby::slice::operator<(const ::debby::slice::Session& l, const ::debby::slice::Session& r)
{
    return static_cast<const ::Ice::Object&>(l) < static_cast<const ::Ice::Object&>(r);
}

bool
debby::slice::operator<=(const ::debby::slice::Session& l, const ::debby::slice::Session& r)
{
    return l < r || l == r;
}

bool
debby::slice::operator>(const ::debby::slice::Session& l, const ::debby::slice::Session& r)
{
    return !(l < r) && !(l == r);
}

bool
debby::slice::operator>=(const ::debby::slice::Session& l, const ::debby::slice::Session& r)
{
    return !(l < r);
}
