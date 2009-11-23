// **********************************************************************
//
// Copyright (c) 2003-2006 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.1.1

package mapreduce.slice;

public final class TaskDetail implements java.lang.Cloneable
{
    public int taskId;

    public int jobId;

    public int status;

    public int type;

    public Address worker;

    public int startTime;

    public int endTime;

    public int chunkId;

    public int mapId;

    public int reduceId;

    public int partition;

    public TaskDetail()
    {
    }

    public TaskDetail(int taskId, int jobId, int status, int type, Address worker, int startTime, int endTime, int chunkId, int mapId, int reduceId, int partition)
    {
	this.taskId = taskId;
	this.jobId = jobId;
	this.status = status;
	this.type = type;
	this.worker = worker;
	this.startTime = startTime;
	this.endTime = endTime;
	this.chunkId = chunkId;
	this.mapId = mapId;
	this.reduceId = reduceId;
	this.partition = partition;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
	if(this == rhs)
	{
	    return true;
	}
	TaskDetail _r = null;
	try
	{
	    _r = (TaskDetail)rhs;
	}
	catch(ClassCastException ex)
	{
	}

	if(_r != null)
	{
	    if(taskId != _r.taskId)
	    {
		return false;
	    }
	    if(jobId != _r.jobId)
	    {
		return false;
	    }
	    if(status != _r.status)
	    {
		return false;
	    }
	    if(type != _r.type)
	    {
		return false;
	    }
	    if(worker != _r.worker && worker != null && !worker.equals(_r.worker))
	    {
		return false;
	    }
	    if(startTime != _r.startTime)
	    {
		return false;
	    }
	    if(endTime != _r.endTime)
	    {
		return false;
	    }
	    if(chunkId != _r.chunkId)
	    {
		return false;
	    }
	    if(mapId != _r.mapId)
	    {
		return false;
	    }
	    if(reduceId != _r.reduceId)
	    {
		return false;
	    }
	    if(partition != _r.partition)
	    {
		return false;
	    }

	    return true;
	}

	return false;
    }

    public int
    hashCode()
    {
	int __h = 0;
	__h = 5 * __h + taskId;
	__h = 5 * __h + jobId;
	__h = 5 * __h + status;
	__h = 5 * __h + type;
	__h = 5 * __h + worker.hashCode();
	__h = 5 * __h + startTime;
	__h = 5 * __h + endTime;
	__h = 5 * __h + chunkId;
	__h = 5 * __h + mapId;
	__h = 5 * __h + reduceId;
	__h = 5 * __h + partition;
	return __h;
    }

    public java.lang.Object
    clone()
    {
	java.lang.Object o = null;
	try
	{
	    o = super.clone();
	}
	catch(CloneNotSupportedException ex)
	{
	    assert false; // impossible
	}
	return o;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
	__os.writeInt(taskId);
	__os.writeInt(jobId);
	__os.writeInt(status);
	__os.writeInt(type);
	worker.__write(__os);
	__os.writeInt(startTime);
	__os.writeInt(endTime);
	__os.writeInt(chunkId);
	__os.writeInt(mapId);
	__os.writeInt(reduceId);
	__os.writeInt(partition);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
	taskId = __is.readInt();
	jobId = __is.readInt();
	status = __is.readInt();
	type = __is.readInt();
	worker = new Address();
	worker.__read(__is);
	startTime = __is.readInt();
	endTime = __is.readInt();
	chunkId = __is.readInt();
	mapId = __is.readInt();
	reduceId = __is.readInt();
	partition = __is.readInt();
    }
}
