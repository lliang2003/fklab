// **********************************************************************
//
// Copyright (c) 2003-2006 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.1.1

package tfs.slice;

public class NoChunkAllocated extends GenericException
{
    public NoChunkAllocated()
    {
	super();
    }

    public NoChunkAllocated(String reason)
    {
	super(reason);
    }

    public String
    ice_name()
    {
	return "tfs::slice::NoChunkAllocated";
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
	__os.writeString("::tfs::slice::NoChunkAllocated");
	__os.startWriteSlice();
	__os.endWriteSlice();
	super.__write(__os);
    }

    public void
    __read(IceInternal.BasicStream __is, boolean __rid)
    {
	if(__rid)
	{
	    __is.readString();
	}
	__is.startReadSlice();
	__is.endReadSlice();
	super.__read(__is, true);
    }

    public void
    __write(Ice.OutputStream __outS)
    {
	Ice.MarshalException ex = new Ice.MarshalException();
	ex.reason = "exception tfs::slice::NoChunkAllocated was not generated with stream support";
	throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
	Ice.MarshalException ex = new Ice.MarshalException();
	ex.reason = "exception tfs::slice::NoChunkAllocated was not generated with stream support";
	throw ex;
    }
}
