/*
 *  TWReader.h -- ���������Ҫ������ȡ������ʽ�Ĵ洢�ļ�.
 *
 *  Created: Zhang Minghui, Netera Inc.
 *
 *  Created: Nov. 26 11:06am 2005. version 0.1.1
 *              # A framework was given.
 */ 

#ifndef TWREADER_H_ZMH_2005_11_26
#define TWREADER_H_ZMH_2005_11_26

#include <iostream>
#include <string>
#include <fstream>
#include <sstream>
#include <sys/errno.h>
#include <unistd.h>


using namespace std;

class TWReaderException : public std::exception
{
public :
	//! Constructor.
	TWReaderException() throw() {}

	//! Construcor.
	TWReaderException(const string &msg) throw() : mMsg(msg){}

	//! ����.
	const char *what() const throw() {return mMsg.c_str();}

	~TWReaderException() throw() {}

private :
	string mMsg;	//!< �쳣��Ϣ����.
};

class TWReader
{
public :
	//! ����һ�������洢�ļ��ļ�¼����. �����ļ���ʽ��鿴����ĵ�.
	typedef struct 
	{
		string version;		//!< ������ҳ�ļ���ÿ����¼�İ汾��.
		string url;		//!< ........................URL.
		string date;		//!< ........................ץȡ����.(����)
		string ip;		//!< ........................ip��ַ.
		int    unzip_length;	//!< .................��ҳ����ԭʼ����.
		int    length;		//!< .....................��ҳ����ѹ������.
		string body;		//!< ........................��ҳ����.
		void clear() {
			version.clear();
			url.clear();
			date.clear();
			ip.clear();
			unzip_length = 0;
			length = 0;
			body.clear();
		}	//!< �����г�Ա���.
	} T_PageRecord;
	
public :
	//! Constructor without parameter.
	TWReader();
	
	//! Constructor.
	//! @param   twPageFile   ������ҳ�ļ�.
	TWReader(const string &twPageFile) throw (TWReaderException);

	//! Destructor.
	virtual ~TWReader();

	/**
	 * @brief    �����µ�������ҳ�ļ�.
	 *
	 * @param    twPageFile     �ļ�����.
	 */
	void input(const string &twPageFile) throw (TWReaderException);

	//! ���������ļ��е���һ����ҳ��¼.
	//! @return   ����һ�����ݵ�ָ��.
	//! @retval   NULL : ��ʾ�ļ�����.
	//! @retval   else : ������ȷ���.
	const T_PageRecord *nextRecord() throw (TWReaderException);

	/**Return raw record buffer
	 *
	 * @param [in] recordBuf : user buffer to hold the record data
	 * @param [in] recordBufLen : user buffer length
	 * @return   : record length that read out
	 *           : 0 if reach eof  
	 *
	 */
	 int nextRecord(char *recordBuf, int recordBufLen) throw (TWReaderException);

protected :

private :
	
private :
	T_PageRecord	mPageRecord;	//!< Ϊ�����ظ��Ĺ���ýṹ���������ɸ���ĳ�Ա.
	ifstream	mTWFileStream;	//!< ������ҳ�ļ���.
};
#endif //TWREADER_H_ZMH_2005_11_26
