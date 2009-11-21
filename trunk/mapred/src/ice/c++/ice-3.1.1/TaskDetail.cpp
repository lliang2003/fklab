// **********************************************************************
//
// Copyright (c) 2003-2006 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.1.1
// Generated from file `TaskDetail.ice'

#include <TaskDetail.h>
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

bool
mapreduce::slice::TaskDetail::operator==(const TaskDetail& __rhs) const
{
    return !operator!=(__rhs);
}

bool
mapreduce::slice::TaskDetail::operator!=(const TaskDetail& __rhs) const
{
    if(this == &__rhs)
    {
	return false;
    }
    if(taskId != __rhs.taskId)
    {
	return true;
    }
    if(jobId != __rhs.jobId)
    {
	return true;
    }
    if(status != __rhs.status)
    {
	return true;
    }
    if(type != __rhs.type)
    {
	return true;
    }
    if(worker != __rhs.worker)
    {
	return true;
    }
    if(startTime != __rhs.startTime)
    {
	return true;
    }
    if(endTime != __rhs.endTime)
    {
	return true;
    }
    if(chunkId != __rhs.chunkId)
    {
	return true;
    }
    if(mapId != __rhs.mapId)
    {
	return true;
    }
    if(reduceId != __rhs.reduceId)
    {
	return true;
    }
    if(partition != __rhs.partition)
    {
	return true;
    }
    return false;
}

bool
mapreduce::slice::TaskDetail::operator<(const TaskDetail& __rhs) const
{
    if(this == &__rhs)
    {
	return false;
    }
    if(taskId < __rhs.taskId)
    {
	return true;
    }
    else if(__rhs.taskId < taskId)
    {
	return false;
    }
    if(jobId < __rhs.jobId)
    {
	return true;
    }
    else if(__rhs.jobId < jobId)
    {
	return false;
    }
    if(status < __rhs.status)
    {
	return true;
    }
    else if(__rhs.status < status)
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
    if(worker < __rhs.worker)
    {
	return true;
    }
    else if(__rhs.worker < worker)
    {
	return false;
    }
    if(startTime < __rhs.startTime)
    {
	return true;
    }
    else if(__rhs.startTime < startTime)
    {
	return false;
    }
    if(endTime < __rhs.endTime)
    {
	return true;
    }
    else if(__rhs.endTime < endTime)
    {
	return false;
    }
    if(chunkId < __rhs.chunkId)
    {
	return true;
    }
    else if(__rhs.chunkId < chunkId)
    {
	return false;
    }
    if(mapId < __rhs.mapId)
    {
	return true;
    }
    else if(__rhs.mapId < mapId)
    {
	return false;
    }
    if(reduceId < __rhs.reduceId)
    {
	return true;
    }
    else if(__rhs.reduceId < reduceId)
    {
	return false;
    }
    if(partition < __rhs.partition)
    {
	return true;
    }
    else if(__rhs.partition < partition)
    {
	return false;
    }
    return false;
}

void
mapreduce::slice::TaskDetail::__write(::IceInternal::BasicStream* __os) const
{
    __os->write(taskId);
    __os->write(jobId);
    __os->write(status);
    __os->write(type);
    worker.__write(__os);
    __os->write(startTime);
    __os->write(endTime);
    __os->write(chunkId);
    __os->write(mapId);
    __os->write(reduceId);
    __os->write(partition);
}

void
mapreduce::slice::TaskDetail::__read(::IceInternal::BasicStream* __is)
{
    __is->read(taskId);
    __is->read(jobId);
    __is->read(status);
    __is->read(type);
    worker.__read(__is);
    __is->read(startTime);
    __is->read(endTime);
    __is->read(chunkId);
    __is->read(mapId);
    __is->read(reduceId);
    __is->read(partition);
}

void
mapreduce::slice::__write(::IceInternal::BasicStream* __os, const ::mapreduce::slice::TaskDetail* begin, const ::mapreduce::slice::TaskDetail* end, ::mapreduce::slice::__U__TasksDetail)
{
    ::Ice::Int size = static_cast< ::Ice::Int>(end - begin);
    __os->writeSize(size);
    for(int i = 0; i < size; ++i)
    {
	begin[i].__write(__os);
    }
}

void
mapreduce::slice::__read(::IceInternal::BasicStream* __is, ::mapreduce::slice::TasksDetail& v, ::mapreduce::slice::__U__TasksDetail)
{
    ::Ice::Int sz;
    __is->readSize(sz);
    __is->startSeq(sz, 45);
    v.resize(sz);
    for(int i = 0; i < sz; ++i)
    {
	v[i].__read(__is);
	__is->checkSeq();
	__is->endElement();
    }
    __is->endSeq(sz);
}
