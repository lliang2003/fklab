/**
 * @file    TestDebbyManager.hpp
 *  
 * 
 * @author  Fan Kai(fk), Peking University
 * @date    2008年07月02日 20时03分51秒 CST
 *
 */

#ifndef  TESTDEBBYMANAGER_FILE_HEADER_INC
#define  TESTDEBBYMANAGER_FILE_HEADER_INC


#include <cxxtest/TestSuite.h>
#include <server/DebbyManager.hpp>
using namespace std;
using namespace debby;

class TestDebbyManager : public CxxTest::TestSuite {
    public:
        void testFile() {
            DebbyManager dm;
            const string testdir = "/testdebbymgr";
            const string testfile = testdir + "/file";
            const string testtmp = testdir + "/tmp";

            vector<string> vs = dm.list(testdir);
            for (int i = 0; i < vs.size(); ++i)
                dm.remove(vs[i]);
            TS_ASSERT_EQUALS(dm.list(testdir).size(), 0);

            dm.create(testfile);
            TS_ASSERT_EQUALS(dm.exists(testfile), true);
            TS_ASSERT_EQUALS(dm.read(testfile), "");
            dm.write(testfile, "hello");
            TS_ASSERT_EQUALS(dm.read(testfile), "hello");

            TS_ASSERT_EQUALS(dm.list(testdir).size(), 1);
            dm.remove(testfile);
            TS_ASSERT_EQUALS(dm.exists(testfile), false);
            TS_ASSERT_EQUALS(dm.list(testdir).size(), 0);

            TS_ASSERT_THROWS(dm.read(testdir + "/unexist"), slice::FileNotExist);
            TS_ASSERT_THROWS(dm.write(testdir + "/unexist", ""), slice::FileNotExist);
        }

        void testSession() {
            DebbyManager dm;
            const string testdir = "/testdatamgr";
            const string testfile = testdir + "/file";
            const string testtmp = testdir + "/tmp";
            const string testtmp2 = testdir + "/tmp2";

            vector<string> vs = dm.list(testdir);
            for (int i = 0; i < vs.size(); ++i)
                dm.remove(vs[i]);

            int handle = dm.addSession("newss");
            int handle2 = dm.addSession("newss2");
            TS_ASSERT_EQUALS(dm.existsSession(handle), true);
            TS_ASSERT_EQUALS(dm.existsSession(handle2), true);

            dm.create(testfile);
            dm.create(testtmp, handle, true);
            dm.create(testtmp2, handle2, true);
            TS_ASSERT_EQUALS(dm.exists(testfile), true);
            TS_ASSERT_EQUALS(dm.exists(testtmp), true);
            TS_ASSERT_EQUALS(dm.exists(testtmp2), true);
            TS_ASSERT_EQUALS(dm.read(testtmp), "");
            dm.write(testtmp, "hello");
            TS_ASSERT_EQUALS(dm.read(testtmp), "hello");

            // This remove expired session1
            dm.updateSession(handle, time(0) - 10);
            dm.removeExpiredSessions(5);
            TS_ASSERT_EQUALS(dm.existsSession(handle), false);
            TS_ASSERT_EQUALS(dm.existsSession(handle2), true);
            TS_ASSERT_EQUALS(dm.exists(testtmp), false);
            TS_ASSERT_EQUALS(dm.exists(testtmp2), true);

            dm.removeSession(handle2);
            TS_ASSERT_EQUALS(dm.existsSession(handle2), false);
            TS_ASSERT_EQUALS(dm.exists(testtmp2), false);

            TS_ASSERT_EQUALS(dm.exists(testfile), true);
        }
};

#endif   /* ----- #ifndef TESTDEBBYMANAGER_FILE_HEADER_INC  ----- */

