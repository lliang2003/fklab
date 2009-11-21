//***********************************************************
//	LFS support to access bigfile(>2GB)
//	Author:	pb,2002-4-18
//	Notice:	macro _GNU_SOURCE must be defined first by compiler
//		for _LARGEFILE_SOURCE and _LARGEFILE64_SOURCE are defined according
//		to it.
//***********************************************************

#ifndef _LFS_LARGE_FILE_
#define _LFS_LARGE_FILE_

#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

class LargeFile{
public:
	LargeFile():handle(0),stream(NULL){}
	~LargeFile(){
		if(handle>0) ::close(handle);
		if(stream) fclose(stream);
	}

	int open(const char* fname,int oflag=O_RDWR){
		if(handle>0) return handle;
		else{
			handle=open64(fname,oflag,0666);
			return handle;
		}
	}
	
	FILE* open(const char* fname,char* mode /* r+ */){
		if(stream) return stream;
		else{
			stream=fopen64(fname,mode);
			return stream;
		}
	}

	void close(){
		if(handle){
			::close(handle);
			handle=0;
		}
		if(stream){
			fclose(stream);
			stream=NULL;
		}
	}

	
	// low level I/O 
	int read(char* buf,int nsize){return ::read(handle,buf,nsize);}
	int write(const char* buf,int nsize){return ::write(handle,buf,nsize);}
	int64_t seek(int64_t offset,int whence){return ::lseek64(handle,offset,whence);}
	int64_t tell(){return ::lseek64(handle,0,SEEK_CUR);}

	// I/O with stream support
	char* fgets(char* buf,int nsize){return ::fgets(buf,nsize,stream);}
	int fread(char* buf,int sizet,int nsize){return ::fread(buf,sizet,nsize,stream);}
	int fwrite(char* buf,int sizet,int nsize){return ::fwrite(buf,sizet,nsize,stream);}
	int64_t fseek(int64_t offset,int whence){return ::fseeko64(stream,offset,whence);}
	int64_t ftell(){return ::ftello64(stream);}

	int gethandle(){return handle;}	
	FILE* getstream(){return stream;}
protected:
	//int 	mode; //0 -low level,1 - stream
	int 	handle;
	FILE* 	stream;
};

#endif
