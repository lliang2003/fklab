// **********************************************************************
//
// Copyright (c) 2003-2006 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.1.1
// Generated from file `Transfer.ice'

#include <Transfer.h>
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

static const ::std::string __fktest__Transfer__send_name = "send";

static const ::std::string __fktest__Transfer__sendsn_name = "sendsn";

void
IceInternal::incRef(::fktest::Transfer* p)
{
    p->__incRef();
}

void
IceInternal::decRef(::fktest::Transfer* p)
{
    p->__decRef();
}

void
IceInternal::incRef(::IceProxy::fktest::Transfer* p)
{
    p->__incRef();
}

void
IceInternal::decRef(::IceProxy::fktest::Transfer* p)
{
    p->__decRef();
}

void
fktest::__write(::IceInternal::BasicStream* __os, const ::fktest::TransferPrx& v)
{
    __os->write(::Ice::ObjectPrx(v));
}

void
fktest::__read(::IceInternal::BasicStream* __is, ::fktest::TransferPrx& v)
{
    ::Ice::ObjectPrx proxy;
    __is->read(proxy);
    if(!proxy)
    {
	v = 0;
    }
    else
    {
	v = new ::IceProxy::fktest::Transfer;
	v->__copyFrom(proxy);
    }
}

void
fktest::__write(::IceInternal::BasicStream* __os, const ::fktest::TransferPtr& v)
{
    __os->write(::Ice::ObjectPtr(v));
}

::Ice::Int
IceProxy::fktest::Transfer::send(const ::fktest::ByteSeq& bytes, ::Ice::Int id, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    __checkTwowayOnly(__fktest__Transfer__send_name);
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::fktest::Transfer* __del = dynamic_cast< ::IceDelegate::fktest::Transfer*>(__delBase.get());
	    return __del->send(bytes, id, __ctx);
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

::Ice::Int
IceProxy::fktest::Transfer::sendsn(::Ice::Int sn, ::Ice::Int id, const ::Ice::Context& __ctx)
{
    int __cnt = 0;
    while(true)
    {
	try
	{
	    __checkTwowayOnly(__fktest__Transfer__sendsn_name);
	    ::IceInternal::Handle< ::IceDelegate::Ice::Object> __delBase = __getDelegate();
	    ::IceDelegate::fktest::Transfer* __del = dynamic_cast< ::IceDelegate::fktest::Transfer*>(__delBase.get());
	    return __del->sendsn(sn, id, __ctx);
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
IceProxy::fktest::Transfer::ice_staticId()
{
    return ::fktest::Transfer::ice_staticId();
}

::IceInternal::Handle< ::IceDelegateM::Ice::Object>
IceProxy::fktest::Transfer::__createDelegateM()
{
    return ::IceInternal::Handle< ::IceDelegateM::Ice::Object>(new ::IceDelegateM::fktest::Transfer);
}

::IceInternal::Handle< ::IceDelegateD::Ice::Object>
IceProxy::fktest::Transfer::__createDelegateD()
{
    return ::IceInternal::Handle< ::IceDelegateD::Ice::Object>(new ::IceDelegateD::fktest::Transfer);
}

bool
IceProxy::fktest::operator==(const ::IceProxy::fktest::Transfer& l, const ::IceProxy::fktest::Transfer& r)
{
    return static_cast<const ::IceProxy::Ice::Object&>(l) == static_cast<const ::IceProxy::Ice::Object&>(r);
}

bool
IceProxy::fktest::operator!=(const ::IceProxy::fktest::Transfer& l, const ::IceProxy::fktest::Transfer& r)
{
    return static_cast<const ::IceProxy::Ice::Object&>(l) != static_cast<const ::IceProxy::Ice::Object&>(r);
}

bool
IceProxy::fktest::operator<(const ::IceProxy::fktest::Transfer& l, const ::IceProxy::fktest::Transfer& r)
{
    return static_cast<const ::IceProxy::Ice::Object&>(l) < static_cast<const ::IceProxy::Ice::Object&>(r);
}

bool
IceProxy::fktest::operator<=(const ::IceProxy::fktest::Transfer& l, const ::IceProxy::fktest::Transfer& r)
{
    return l < r || l == r;
}

bool
IceProxy::fktest::operator>(const ::IceProxy::fktest::Transfer& l, const ::IceProxy::fktest::Transfer& r)
{
    return !(l < r) && !(l == r);
}

bool
IceProxy::fktest::operator>=(const ::IceProxy::fktest::Transfer& l, const ::IceProxy::fktest::Transfer& r)
{
    return !(l < r);
}

::Ice::Int
IceDelegateM::fktest::Transfer::send(const ::fktest::ByteSeq& bytes, ::Ice::Int id, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __fktest__Transfer__send_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	if(bytes.size() == 0)
	{
	    __os->writeSize(0);
	}
	else
	{
	    __os->write(&bytes[0], &bytes[0] + bytes.size());
	}
	__os->write(id);
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

::Ice::Int
IceDelegateM::fktest::Transfer::sendsn(::Ice::Int sn, ::Ice::Int id, const ::Ice::Context& __context)
{
    ::IceInternal::Outgoing __og(__connection.get(), __reference.get(), __fktest__Transfer__sendsn_name, ::Ice::Normal, __context, __compress);
    try
    {
	::IceInternal::BasicStream* __os = __og.os();
	__os->write(sn);
	__os->write(id);
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

::Ice::Int
IceDelegateD::fktest::Transfer::send(const ::fktest::ByteSeq& bytes, ::Ice::Int id, const ::Ice::Context& __context)
{
    ::Ice::Current __current;
    __initCurrent(__current, __fktest__Transfer__send_name, ::Ice::Normal, __context);
    while(true)
    {
	::IceInternal::Direct __direct(__current);
	::fktest::Transfer* __servant = dynamic_cast< ::fktest::Transfer*>(__direct.servant().get());
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
	    return __servant->send(bytes, id, __current);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    throw ::IceInternal::LocalExceptionWrapper(__ex, false);
	}
    }
}

::Ice::Int
IceDelegateD::fktest::Transfer::sendsn(::Ice::Int sn, ::Ice::Int id, const ::Ice::Context& __context)
{
    ::Ice::Current __current;
    __initCurrent(__current, __fktest__Transfer__sendsn_name, ::Ice::Normal, __context);
    while(true)
    {
	::IceInternal::Direct __direct(__current);
	::fktest::Transfer* __servant = dynamic_cast< ::fktest::Transfer*>(__direct.servant().get());
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
	    return __servant->sendsn(sn, id, __current);
	}
	catch(const ::Ice::LocalException& __ex)
	{
	    throw ::IceInternal::LocalExceptionWrapper(__ex, false);
	}
    }
}

::Ice::ObjectPtr
fktest::Transfer::ice_clone() const
{
    throw ::Ice::CloneNotImplementedException(__FILE__, __LINE__);
    return 0; // to avoid a warning with some compilers
}

static const ::std::string __fktest__Transfer_ids[2] =
{
    "::Ice::Object",
    "::fktest::Transfer"
};

bool
fktest::Transfer::ice_isA(const ::std::string& _s, const ::Ice::Current&) const
{
    return ::std::binary_search(__fktest__Transfer_ids, __fktest__Transfer_ids + 2, _s);
}

::std::vector< ::std::string>
fktest::Transfer::ice_ids(const ::Ice::Current&) const
{
    return ::std::vector< ::std::string>(&__fktest__Transfer_ids[0], &__fktest__Transfer_ids[2]);
}

const ::std::string&
fktest::Transfer::ice_id(const ::Ice::Current&) const
{
    return __fktest__Transfer_ids[1];
}

const ::std::string&
fktest::Transfer::ice_staticId()
{
    return __fktest__Transfer_ids[1];
}

::IceInternal::DispatchStatus
fktest::Transfer::___send(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::IceInternal::BasicStream* __os = __inS.os();
    ::fktest::ByteSeq bytes;
    ::Ice::Int id;
    ::std::pair<const ::Ice::Byte*, const ::Ice::Byte*> ___bytes;
    __is->read(___bytes);
    ::std::vector< ::Ice::Byte>(___bytes.first, ___bytes.second).swap(bytes);
    __is->read(id);
    ::Ice::Int __ret = send(bytes, id, __current);
    __os->write(__ret);
    return ::IceInternal::DispatchOK;
}

::IceInternal::DispatchStatus
fktest::Transfer::___sendsn(::IceInternal::Incoming&__inS, const ::Ice::Current& __current)
{
    __checkMode(::Ice::Normal, __current.mode);
    ::IceInternal::BasicStream* __is = __inS.is();
    ::IceInternal::BasicStream* __os = __inS.os();
    ::Ice::Int sn;
    ::Ice::Int id;
    __is->read(sn);
    __is->read(id);
    ::Ice::Int __ret = sendsn(sn, id, __current);
    __os->write(__ret);
    return ::IceInternal::DispatchOK;
}

static ::std::string __fktest__Transfer_all[] =
{
    "ice_id",
    "ice_ids",
    "ice_isA",
    "ice_ping",
    "send",
    "sendsn"
};

::IceInternal::DispatchStatus
fktest::Transfer::__dispatch(::IceInternal::Incoming& in, const ::Ice::Current& current)
{
    ::std::pair< ::std::string*, ::std::string*> r = ::std::equal_range(__fktest__Transfer_all, __fktest__Transfer_all + 6, current.operation);
    if(r.first == r.second)
    {
	return ::IceInternal::DispatchOperationNotExist;
    }

    switch(r.first - __fktest__Transfer_all)
    {
	case 0:
	{
	    return ___ice_id(in, current);
	}
	case 1:
	{
	    return ___ice_ids(in, current);
	}
	case 2:
	{
	    return ___ice_isA(in, current);
	}
	case 3:
	{
	    return ___ice_ping(in, current);
	}
	case 4:
	{
	    return ___send(in, current);
	}
	case 5:
	{
	    return ___sendsn(in, current);
	}
    }

    assert(false);
    return ::IceInternal::DispatchOperationNotExist;
}

void
fktest::Transfer::__write(::IceInternal::BasicStream* __os) const
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
fktest::Transfer::__read(::IceInternal::BasicStream* __is, bool __rid)
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
fktest::Transfer::__write(const ::Ice::OutputStreamPtr&) const
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "type fktest::Transfer was not generated with stream support";
    throw ex;
}

void
fktest::Transfer::__read(const ::Ice::InputStreamPtr&, bool)
{
    Ice::MarshalException ex(__FILE__, __LINE__);
    ex.reason = "type fktest::Transfer was not generated with stream support";
    throw ex;
}

void 
fktest::__patch__TransferPtr(void* __addr, ::Ice::ObjectPtr& v)
{
    ::fktest::TransferPtr* p = static_cast< ::fktest::TransferPtr*>(__addr);
    assert(p);
    *p = ::fktest::TransferPtr::dynamicCast(v);
    if(v && !*p)
    {
	::Ice::NoObjectFactoryException e(__FILE__, __LINE__);
	e.type = ::fktest::Transfer::ice_staticId();
	throw e;
    }
}

bool
fktest::operator==(const ::fktest::Transfer& l, const ::fktest::Transfer& r)
{
    return static_cast<const ::Ice::Object&>(l) == static_cast<const ::Ice::Object&>(r);
}

bool
fktest::operator!=(const ::fktest::Transfer& l, const ::fktest::Transfer& r)
{
    return static_cast<const ::Ice::Object&>(l) != static_cast<const ::Ice::Object&>(r);
}

bool
fktest::operator<(const ::fktest::Transfer& l, const ::fktest::Transfer& r)
{
    return static_cast<const ::Ice::Object&>(l) < static_cast<const ::Ice::Object&>(r);
}

bool
fktest::operator<=(const ::fktest::Transfer& l, const ::fktest::Transfer& r)
{
    return l < r || l == r;
}

bool
fktest::operator>(const ::fktest::Transfer& l, const ::fktest::Transfer& r)
{
    return !(l < r) && !(l == r);
}

bool
fktest::operator>=(const ::fktest::Transfer& l, const ::fktest::Transfer& r)
{
    return !(l < r);
}
