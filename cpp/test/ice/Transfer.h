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

#ifndef __Transfer_h__
#define __Transfer_h__

#include <Ice/LocalObjectF.h>
#include <Ice/ProxyF.h>
#include <Ice/ObjectF.h>
#include <Ice/Exception.h>
#include <Ice/LocalObject.h>
#include <Ice/Proxy.h>
#include <Ice/Object.h>
#include <Ice/Outgoing.h>
#include <Ice/Incoming.h>
#include <Ice/Direct.h>
#include <Ice/StreamF.h>
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

namespace fktest
{

class Transfer;
bool operator==(const Transfer&, const Transfer&);
bool operator!=(const Transfer&, const Transfer&);
bool operator<(const Transfer&, const Transfer&);
bool operator<=(const Transfer&, const Transfer&);
bool operator>(const Transfer&, const Transfer&);
bool operator>=(const Transfer&, const Transfer&);

}

}

namespace fktest
{

class Transfer;
bool operator==(const Transfer&, const Transfer&);
bool operator!=(const Transfer&, const Transfer&);
bool operator<(const Transfer&, const Transfer&);
bool operator<=(const Transfer&, const Transfer&);
bool operator>(const Transfer&, const Transfer&);
bool operator>=(const Transfer&, const Transfer&);

}

namespace IceInternal
{

void incRef(::fktest::Transfer*);
void decRef(::fktest::Transfer*);

void incRef(::IceProxy::fktest::Transfer*);
void decRef(::IceProxy::fktest::Transfer*);

}

namespace fktest
{

typedef ::IceInternal::Handle< ::fktest::Transfer> TransferPtr;
typedef ::IceInternal::ProxyHandle< ::IceProxy::fktest::Transfer> TransferPrx;

void __write(::IceInternal::BasicStream*, const TransferPrx&);
void __read(::IceInternal::BasicStream*, TransferPrx&);
void __write(::IceInternal::BasicStream*, const TransferPtr&);
void __patch__TransferPtr(void*, ::Ice::ObjectPtr&);

}

namespace fktest
{

typedef ::std::vector< ::Ice::Byte> ByteSeq;

class __U__ByteSeq { };

}

namespace IceProxy
{

namespace fktest
{

class Transfer : virtual public ::IceProxy::Ice::Object
{
public:

    ::Ice::Int send(const ::fktest::ByteSeq& bytes, ::Ice::Int id)
    {
	return send(bytes, id, __defaultContext());
    }
    ::Ice::Int send(const ::fktest::ByteSeq&, ::Ice::Int, const ::Ice::Context&);

    ::Ice::Int sendsn(::Ice::Int sn, ::Ice::Int id)
    {
	return sendsn(sn, id, __defaultContext());
    }
    ::Ice::Int sendsn(::Ice::Int, ::Ice::Int, const ::Ice::Context&);
    
    static const ::std::string& ice_staticId();

private: 

    virtual ::IceInternal::Handle< ::IceDelegateM::Ice::Object> __createDelegateM();
    virtual ::IceInternal::Handle< ::IceDelegateD::Ice::Object> __createDelegateD();
};

}

}

namespace IceDelegate
{

namespace fktest
{

class Transfer : virtual public ::IceDelegate::Ice::Object
{
public:

    virtual ::Ice::Int send(const ::fktest::ByteSeq&, ::Ice::Int, const ::Ice::Context&) = 0;

    virtual ::Ice::Int sendsn(::Ice::Int, ::Ice::Int, const ::Ice::Context&) = 0;
};

}

}

namespace IceDelegateM
{

namespace fktest
{

class Transfer : virtual public ::IceDelegate::fktest::Transfer,
		 virtual public ::IceDelegateM::Ice::Object
{
public:

    virtual ::Ice::Int send(const ::fktest::ByteSeq&, ::Ice::Int, const ::Ice::Context&);

    virtual ::Ice::Int sendsn(::Ice::Int, ::Ice::Int, const ::Ice::Context&);
};

}

}

namespace IceDelegateD
{

namespace fktest
{

class Transfer : virtual public ::IceDelegate::fktest::Transfer,
		 virtual public ::IceDelegateD::Ice::Object
{
public:

    virtual ::Ice::Int send(const ::fktest::ByteSeq&, ::Ice::Int, const ::Ice::Context&);

    virtual ::Ice::Int sendsn(::Ice::Int, ::Ice::Int, const ::Ice::Context&);
};

}

}

namespace fktest
{

class Transfer : virtual public ::Ice::Object
{
public:

    typedef TransferPrx ProxyType;
    typedef TransferPtr PointerType;
    
    virtual ::Ice::ObjectPtr ice_clone() const;

    virtual bool ice_isA(const ::std::string&, const ::Ice::Current& = ::Ice::Current()) const;
    virtual ::std::vector< ::std::string> ice_ids(const ::Ice::Current& = ::Ice::Current()) const;
    virtual const ::std::string& ice_id(const ::Ice::Current& = ::Ice::Current()) const;
    static const ::std::string& ice_staticId();

    virtual ::Ice::Int send(const ::fktest::ByteSeq&, ::Ice::Int, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___send(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual ::Ice::Int sendsn(::Ice::Int, ::Ice::Int, const ::Ice::Current& = ::Ice::Current()) = 0;
    ::IceInternal::DispatchStatus ___sendsn(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual ::IceInternal::DispatchStatus __dispatch(::IceInternal::Incoming&, const ::Ice::Current&);

    virtual void __write(::IceInternal::BasicStream*) const;
    virtual void __read(::IceInternal::BasicStream*, bool);
    virtual void __write(const ::Ice::OutputStreamPtr&) const;
    virtual void __read(const ::Ice::InputStreamPtr&, bool);
};

void __patch__TransferPtr(void*, ::Ice::ObjectPtr&);

}

#endif
