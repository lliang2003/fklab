/**
 * @file    TestDataManager.hpp
 *  
 * 
 * @author  Fan Kai(fk), Peking University
 * @date    2008年06月29日 20时45分30秒 CST
 *
 */

#ifndef  TESTDATAMANAGER_FILE_HEADER_INC
#define  TESTDATAMANAGER_FILE_HEADER_INC

#include <cxxtest/TestSuite.h>
#include <server/DataManager.hpp>
using namespace debby;
using namespace std;

class TestDataManager : public CxxTest::TestSuite {
    public:
        void testFile() {
            DataManager dm;
            const string testdir = "/testdatamgr";
            const string testfile = testdir + "/file";
            const string testtmp = testdir + "/tmp";

            vector<string> vs = dm.list(testdir);
            for (int i = 0; i < vs.size(); ++i)
                dm.remove(vs[i]);
            TS_ASSERT_EQUALS(dm.list(testdir).size(), 0);
            if (dm.existsSession(12345))
                dm.removeSession(12345);

            cout <<"ok" <<endl;
            dm.create(testfile);
            TS_ASSERT_EQUALS(dm.exists(testfile), true);
            TS_ASSERT_EQUALS(dm.read(testfile), "");
            dm.write(testfile, "hello");
            TS_ASSERT_EQUALS(dm.read(testfile), "hello");

            dm.addSession("", 12345);
            dm.create(testtmp, 12345);
            TS_ASSERT_EQUALS(dm.exists(testtmp), true);
            TS_ASSERT_EQUALS(dm.read(testtmp), "");
            dm.write(testtmp, "hello");
            TS_ASSERT_EQUALS(dm.read(testtmp), "hello");

            TS_ASSERT_EQUALS(dm.list(testdir).size(), 2);
            dm.remove(testfile);
            TS_ASSERT_EQUALS(dm.exists(testfile), false);
            dm.remove(testtmp);
            TS_ASSERT_EQUALS(dm.exists(testtmp), false);
            TS_ASSERT_EQUALS(dm.list(testdir).size(), 0);
        }

        void testSession() {
            DataManager dm;
            const string testdir = "/testdatamgr";
            const string testfile = testdir + "/file";
            const string testtmp = testdir + "/tmp";

            vector<string> vs = dm.list(testdir);
            for (int i = 0; i < vs.size(); ++i)
                dm.remove(vs[i]);

            if (dm.existsSession(12345))
                dm.removeSession(12345);
            dm.addSession("newss", 12345);
            TS_ASSERT_EQUALS(dm.existsSession(12345), true);

            cout <<"ok" <<endl;
            dm.create(testfile);
            dm.create(testtmp, 12345);

            TS_ASSERT_EQUALS(dm.hasEvent(12345), false);
            slice::Event e1, e2, e3;
            e1.type = slice::EChanged;
            e1.path = testfile;
            e2.type = slice::ECreated;
            e2.path = testtmp;
            dm.addEvent(12345, e1);
            dm.addEvent(12345, e2);
            TS_ASSERT_EQUALS(dm.hasEvent(12345), true);
            e3 = dm.getEvent(12345);
            TS_ASSERT_EQUALS(e1, e3);
            TS_ASSERT_EQUALS(dm.hasEvent(12345), true);
            e3 = dm.getEvent(12345);
            TS_ASSERT_EQUALS(e2, e3);
            TS_ASSERT_EQUALS(dm.hasEvent(12345), false);

            cout <<"ok" <<endl;
            TS_ASSERT_EQUALS(dm.existsSession(12345), true);
            dm.removeSession(12345);
            TS_ASSERT_EQUALS(dm.exists(testfile), true);
            TS_ASSERT_EQUALS(dm.exists(testtmp), false);
            TS_ASSERT_EQUALS(dm.hasEvent(12345), false);
       }

        void testDb() {
            const string testdir = "/testdatamgr";
            const string testfile1 = testdir + "/file1";
            const string testfile2 = testdir + "/file2";
            const string testfile3 = testdir + "/file3";
            const string testtmp1 = testdir + "/tmp1";
            const string testtmp2 = testdir + "/tmp2";
            const string testtmp3 = testdir + "/tmp3";
            {
                DataManager dm;
                if (dm.existsSession(12345))
                    dm.removeSession(12345);
                if (dm.existsSession(54321))
                    dm.removeSession(54321);

                vector<string> vs = dm.list(testdir);
                for (int i = 0; i < vs.size(); ++i)
                    dm.remove(vs[i]);
                TS_ASSERT_EQUALS(dm.list(testdir).size(), 0);

                dm.create(testfile1);
                dm.create(testfile2);
                dm.create(testfile3);
                dm.write(testfile1, "1");
                dm.write(testfile2, "2");
                dm.write(testfile3, "3");

                dm.addSession("newss", 12345);
                dm.addSession("newss2", 54321);
                dm.create(testtmp1, 12345);
                dm.create(testtmp2, 12345);
                dm.create(testtmp3, 54321);
                dm.write(testtmp1, "tmp1");
                dm.write(testtmp2, "tmp2");
                dm.write(testtmp3, "tmp3");
                
                slice::Event e1, e2, e3;
                e1.type = slice::EChanged;
                e1.path = testfile1;
                e2.type = slice::ERemoved;
                e2.path = testtmp2;
                e3.type = slice::ERemoved;
                e3.path = testfile3;
                dm.addEvent(12345, e1);
                dm.addEvent(12345, e2);
                dm.addEvent(54321, e3);
                cout <<"ok" <<endl;
            }
            {
                DataManager dm;
                TS_ASSERT_EQUALS(dm.exists(testfile1), true);
                TS_ASSERT_EQUALS(dm.exists(testfile2), true);
                TS_ASSERT_EQUALS(dm.exists(testfile3), true);
                TS_ASSERT_EQUALS(dm.exists(testtmp1), true);
                TS_ASSERT_EQUALS(dm.exists(testtmp2), true);
                TS_ASSERT_EQUALS(dm.exists(testtmp3), true);
                TS_ASSERT_EQUALS(dm.read(testfile1), "1");
                TS_ASSERT_EQUALS(dm.read(testfile2), "2");
                TS_ASSERT_EQUALS(dm.read(testfile3), "3");
                TS_ASSERT_EQUALS(dm.read(testtmp1), "tmp1");
                TS_ASSERT_EQUALS(dm.read(testtmp2), "tmp2");
                TS_ASSERT_EQUALS(dm.read(testtmp3), "tmp3");

                TS_ASSERT_EQUALS(dm.existsSession(12345), true);
                TS_ASSERT_EQUALS(dm.existsSession(54321), true);
                TS_ASSERT_EQUALS(dm.hasEvent(12345), true);
                slice::Event e = dm.getEvent(12345);
                TS_ASSERT_EQUALS(e.path, testfile1);
                TS_ASSERT_EQUALS(e.type, slice::EChanged);
                TS_ASSERT_EQUALS(dm.hasEvent(12345), true);
                e = dm.getEvent(12345);
                TS_ASSERT_EQUALS(e.path, testtmp2);
                TS_ASSERT_EQUALS(e.type, slice::ERemoved);
                TS_ASSERT_EQUALS(dm.hasEvent(54321), true);
                e = dm.getEvent(54321);
                TS_ASSERT_EQUALS(e.path, testfile3);
                TS_ASSERT_EQUALS(e.type, slice::ERemoved);
                TS_ASSERT_EQUALS(dm.hasEvent(12345), false);
                TS_ASSERT_EQUALS(dm.hasEvent(54321), false);

                dm.remove(testfile2);
                dm.removeSession(12345);
                TS_ASSERT_EQUALS(dm.exists(testfile1), true);
                TS_ASSERT_EQUALS(dm.exists(testfile2), false);
                TS_ASSERT_EQUALS(dm.exists(testfile3), true);
                TS_ASSERT_EQUALS(dm.exists(testtmp1), false);
                TS_ASSERT_EQUALS(dm.exists(testtmp2), false);
                TS_ASSERT_EQUALS(dm.exists(testtmp3), true);

                cout <<"ok" <<endl;
            }
            {
                DataManager dm;
                TS_ASSERT_EQUALS(dm.exists(testfile1), true);
                TS_ASSERT_EQUALS(dm.exists(testfile2), false);
                TS_ASSERT_EQUALS(dm.exists(testfile3), true);
                TS_ASSERT_EQUALS(dm.exists(testtmp1), false);
                TS_ASSERT_EQUALS(dm.exists(testtmp2), false);
                TS_ASSERT_EQUALS(dm.exists(testtmp3), true);

                TS_ASSERT_EQUALS(dm.existsSession(12345), false);
                TS_ASSERT_EQUALS(dm.existsSession(54321), true);
                dm.removeSession(54321);
                TS_ASSERT_EQUALS(dm.existsSession(54321), false);
                TS_ASSERT_EQUALS(dm.exists(testtmp3), false);
            }
        }

};  

#endif   /* ----- #ifndef TESTDATAMANAGER_FILE_HEADER_INC  ----- */

