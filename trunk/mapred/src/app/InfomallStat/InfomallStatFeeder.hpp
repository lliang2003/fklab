/**
 * @file InfomallStatFeeder.hpp
 * @description 
 *  RecordReader for Infomall statistics file
 * 1.<url, crawl_count> is output record, type is <char*, int>
 * 2.<host, crawl_count> is output record, type is <char*, int>
 * 3.<year_month, crawl_count> is output record, type is <year_month, int>
 * 4.<year_week, crawl_count> is output record, type is <year_week, int>
 **/

#ifndef INFOMALLSTATFEEDER_HPP
#define INFOMALLSTATFEEDER_HPP

#include <mapred/RecordReader.hpp>
#include <fstream>


namespace mapreduce{
    class RegionIterator;
    class FileStream;

    struct YearMonth{
        int year;
        int month;
    };
    
    struct YearWeek{
        int year;
        int week;
    };

    //==================================================
    // 1. url feeder <url, count>
    class InfomallStatUrlFeeder : public RecordReader {
        public:

            InfomallStatUrlFeeder();
            
            virtual ~InfomallStatUrlFeeder();

            /**
            * Initialize the reader, this is necessary for dynamically created objects,
            * which only runs default constructor.
            */
            virtual void initialize(int bufferSize = 0, bool autoClearBuffer = false);

            /**
            * Open a stream. 
            * @throws FSError : under fstream error, like open mode mismatch
            */
            virtual void open(FileStream* stream);

            /**
            * flush the stream buffers. 
            * @return : -1 means the operation was failed;
            *            1 means it was success.
            */
            virtual void flush();

            /**
            * Close the stream. 
            * @throws FSError : under fstream error, like open mode mismatch
            */
            virtual void close() ;

            /**
            * Create Key Class object. 
            * @return : 
            *           
            */
            virtual void* createKeys(int number = 0){
                if (number <= 0){
                    return new char*[1];
                }
                else{
                    return new char*[number];
                }
            }

            virtual void* createValues(int number) 
            {
                return new int; 
            }

            /**
            * Get Key Class size. 
            * @return : 
            *           
            */
            virtual int getKeySize(){return sizeof(char*);}

            /**
            * Create Value Class object. 
            * @return : 
            *           
            */
            virtual void* createValues(){
                return new int;
            }

            /**
            * Get Value Class size. 
            * @return : 
            *           
            */
            virtual int getValueSize(){return sizeof(int);}

            /**
            * Access the next record.
            * User must provide key and value object, send their address in.
            * @param key [out], key read in
            * @param value [out], value read in
            * @return : true if success
            *           false if fail, for example, reading reach the end.
            */
            virtual bool next(void*& key, void*& value);

            /**
            * Access the next record.
            * This call create new key object by createKey call.
            * This method is design for in memory sorting.
            * Read in <key pointer, recordLen> pairs as index and sort on them.
            * @param key [out], key read in
            * @param iter [out], record regions
            * @return : true if success
            *           false if fail, for example, reading reach the end.
            */
            virtual bool next(void*& key, RegionIterator& iter){return false;}
            
            /**
             * release the pointer or pointers allocated when create keys
             * */
            virtual bool releaseKeys(void*& keys);
            
            /**
             * release the pointer or pointers allocated when create values
             * */
            virtual bool releaseValues(void*& values);


        private:
            std::ifstream m_fin;
    };

    //=================================================
    //
    // 2.host feeder <host, count>
    class InfomallStatHostFeeder : public RecordReader {
        public:

            InfomallStatHostFeeder();
            
            virtual ~InfomallStatHostFeeder();

            /**
            * Initialize the reader, this is necessary for dynamically created objects,
            * which only runs default constructor.
            */
            virtual void initialize(int bufferSize = 0, bool autoClearBuffer = false);

            /**
            * Open a stream. 
            * @throws FSError : under fstream error, like open mode mismatch
            */
            virtual void open(FileStream* stream);

            /**
            * flush the stream buffers. 
            * @return : -1 means the operation was failed;
            *            1 means it was success.
            */
            virtual void flush();

            /**
            * Close the stream. 
            * @throws FSError : under fstream error, like open mode mismatch
            */
            virtual void close() ;

            /**
            * Create Key Class object. 
            * @return : 
            *           
            */
            virtual void* createKeys(int number = 0){
                if (number <= 0){
                    return new char*[1];
                }
                else{
                    return new char*[number];
                }
            }

            virtual void* createValues(int number) 
            {
                return new int; 
            }

            /**
            * Get Key Class size. 
            * @return : 
            *           
            */
            virtual int getKeySize(){return sizeof(char*);}

            /**
            * Create Value Class object. 
            * @return : 
            *           
            */
            virtual void* createValues(){
                return new int;
            }

            /**
            * Get Value Class size. 
            * @return : 
            *           
            */
            virtual int getValueSize(){return sizeof(int);}

            /**
            * Access the next record.
            * User must provide key and value object, send their address in.
            * @param key [out], key read in
            * @param value [out], value read in
            * @return : true if success
            *           false if fail, for example, reading reach the end.
            */
            virtual bool next(void*& key, void*& value);

            /**
            * Access the next record.
            * This call create new key object by createKey call.
            * This method is design for in memory sorting.
            * Read in <key pointer, recordLen> pairs as index and sort on them.
            * @param key [out], key read in
            * @param iter [out], record regions
            * @return : true if success
            *           false if fail, for example, reading reach the end.
            */
            virtual bool next(void*& key, RegionIterator& iter){return false;}
            
            /**
             * release the pointer or pointers allocated when create keys
             * */
            virtual bool releaseKeys(void*& keys);
            
            /**
             * release the pointer or pointers allocated when create values
             * */
            virtual bool releaseValues(void*& values);


        private:
            std::ifstream m_fin;
    };


    //=================================================
    //
    // 3.timespan feeder <list<timespan>, null>
    // timespan count in weeks
    class InfomallStatTimeSpanFeeder : public RecordReader {
        public:

            InfomallStatTimeSpanFeeder();
            
            virtual ~InfomallStatTimeSpanFeeder();

            /**
            * Initialize the reader, this is necessary for dynamically created objects,
            * which only runs default constructor.
            */
            virtual void initialize(int bufferSize = 0, bool autoClearBuffer = false);

            /**
            * Open a stream. 
            * @throws FSError : under fstream error, like open mode mismatch
            */
            virtual void open(FileStream* stream);

            /**
            * flush the stream buffers. 
            * @return : -1 means the operation was failed;
            *            1 means it was success.
            */
            virtual void flush();

            /**
            * Close the stream. 
            * @throws FSError : under fstream error, like open mode mismatch
            */
            virtual void close() ;

            /**
            * Create Key Class object. 
            * @return : 
            *           
            */
            virtual void* createKeys(int number = 0){
                if(number <= 0)
                    return new int[2];
                else
                    return new int[number+1];
            }

            virtual void* createValues(int number) 
            {
                return NULL; 
            }

            /**
            * Get Key Class size. 
            * @return : 
            *           
            */
            virtual int getKeySize(){return sizeof(int*);}

            /**
            * Create Value Class object. 
            * @return : 
            *           
            */
            virtual void* createValues(){
                return NULL;
            }

            /**
            * Get Value Class size. 
            * @return : 
            *           
            */
            virtual int getValueSize(){return 0;}

            /**
            * Access the next record.
            * User must provide key and value object, send their address in.
            * @param key [out], key read in
            * @param value [out], value read in
            * @return : true if success
            *           false if fail, for example, reading reach the end.
            */
            virtual bool next(void*& key, void*& value);

            /**
            * Access the next record.
            * This call create new key object by createKey call.
            * This method is design for in memory sorting.
            * Read in <key pointer, recordLen> pairs as index and sort on them.
            * @param key [out], key read in
            * @param iter [out], record regions
            * @return : true if success
            *           false if fail, for example, reading reach the end.
            */
            virtual bool next(void*& key, RegionIterator& iter){return false;}
            
            /**
             * release the pointer or pointers allocated when create keys
             * */
            virtual bool releaseKeys(void*& keys);
            
            /**
             * release the pointer or pointers allocated when create values
             * */
            virtual bool releaseValues(void*& values);


        private:
            std::ifstream m_fin;
    };

#ifdef rishan

    //=================================================
    //
    // 4.week feeder <year_week, count>
    class InfomallStatWeekFeeder : public RecordReader {
        public:

            InfomallStatWeekFeeder();
            
            virtual ~InfomallStatWeekFeeder();

            /**
            * Initialize the reader, this is necessary for dynamically created objects,
            * which only runs default constructor.
            */
            virtual void initialize(int bufferSize = 0, bool autoClearBuffer = false);

            /**
            * Open a stream. 
            * @throws FSError : under fstream error, like open mode mismatch
            */
            virtual void open(FileStream* stream);

            /**
            * flush the stream buffers. 
            * @return : -1 means the operation was failed;
            *            1 means it was success.
            */
            virtual void flush();

            /**
            * Close the stream. 
            * @throws FSError : under fstream error, like open mode mismatch
            */
            virtual void close() ;

            /**
            * Create Key Class object. 
            * @return : 
            *           
            */
            virtual void* createKeys(int number = 0){
                return YearWeek;
            }

            virtual void* createValues(int number) 
            {
                return new int; 
            }

            /**
            * Get Key Class size. 
            * @return : 
            *           
            */
            virtual int getKeySize(){return sizeof(YearWeek);}

            /**
            * Create Value Class object. 
            * @return : 
            *           
            */
            virtual void* createValues(){
                return new int;
            }

            /**
            * Get Value Class size. 
            * @return : 
            *           
            */
            virtual int getValueSize(){return sizeof(int);}

            /**
            * Access the next record.
            * User must provide key and value object, send their address in.
            * @param key [out], key read in
            * @param value [out], value read in
            * @return : true if success
            *           false if fail, for example, reading reach the end.
            */
            virtual bool next(void*& key, void*& value);

            /**
            * Access the next record.
            * This call create new key object by createKey call.
            * This method is design for in memory sorting.
            * Read in <key pointer, recordLen> pairs as index and sort on them.
            * @param key [out], key read in
            * @param iter [out], record regions
            * @return : true if success
            *           false if fail, for example, reading reach the end.
            */
            virtual bool next(void*& key, RegionIterator& iter){return false;}
            
            /**
             * release the pointer or pointers allocated when create keys
             * */
            virtual bool releaseKeys(void*& keys);
            
            /**
             * release the pointer or pointers allocated when create values
             * */
            virtual bool releaseValues(void*& values);


        private:
            std::ifstream m_fin;
    };

#endif

}
#endif
