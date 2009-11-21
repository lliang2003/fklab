/**
 * @file	TestBasic.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 *
 */

#include <client/debby.h>
#include <iostream>
#include <ctime>
using namespace boost;
using namespace std;

class MyCB: public debby::Callback {
public:
    void run() {
        time_t t = time(0);
        cout <<"MyCB invoked at " <<ctime(&t) <<endl;
    }
};

class TestBasic : public CxxTest::TestSuite {
public:
    void testFS() {
        debby::ClientConfig conf;
        conf.addr.push_back(make_pair("localhost", 7555));
        conf.logLevel = "DEBUG";

        debby::Client dc(conf);

        vector<string> vs = dc.list("/testbasic");
        for (int i = 0; i < vs.size(); ++i)
            dc.remove(vs[i]);
        TS_ASSERT_EQUALS(dc.list("/testbasic").size(), 0);
        TS_ASSERT_EQUALS(dc.exists("/testbasic/f1"), false);
        TS_ASSERT_EQUALS(dc.exists("/testbasic/f2"), false);
        TS_ASSERT_EQUALS(dc.exists("/testbasic/f3"), false);

        TS_ASSERT_THROWS(dc.exists("/testbasic/"), ::debby::InvalidName);
        TS_ASSERT_THROWS(dc.exists("testbasic"), ::debby::InvalidName);
        TS_ASSERT_THROWS(dc.exists("/testbasic/"), ::debby::InvalidName);
        TS_ASSERT_THROWS(dc.exists("/"), ::debby::InvalidName);
        TS_ASSERT_THROWS_NOTHING(dc.list("/"));
        TS_ASSERT_THROWS_NOTHING(dc.exists(""));
        TS_ASSERT_EQUALS(dc.list(""), dc.list("/"));

        dc.create("/testbasic/f1");
        dc.create("/testbasic/f2");
        TS_ASSERT_EQUALS(dc.exists("/testbasic/f1"), true);
        TS_ASSERT_EQUALS(dc.exists("/testbasic/f2"), true);

        vs = dc.list("/testbasic");
        TS_ASSERT_EQUALS(vs.size(), 2);
        TS_ASSERT_EQUALS(vs[0], "/testbasic/f1");
        TS_ASSERT_EQUALS(vs[1], "/testbasic/f2");

        TS_ASSERT_THROWS_NOTHING(dc.create("/testbasic/f1"));
        TS_ASSERT_EQUALS(dc.create("/testbasic/f1"), false);

        TS_ASSERT_THROWS(dc.read("/testbasic/f3"), debby::FileNotExist);
        TS_ASSERT_THROWS(dc.write("/testbasic/f3", ""), debby::FileNotExist);
        dc.create("/testbasic/f3");
        dc.write("/testbasic/f3", "hello");
        TS_ASSERT_EQUALS(dc.read("/testbasic/f3"), "hello");

        dc.create("/testbasic/ft", true);
        TS_ASSERT_EQUALS(dc.exists("/testbasic/ft"), true);

        dc.close();

        TS_ASSERT_THROWS(dc.remove("/testbasic/ft"), debby::SessionClosed);

        debby::Client dcc(conf);
        TS_ASSERT_EQUALS(dcc.exists("/testbasic/ft"), false);
        TS_ASSERT_EQUALS(dcc.exists("/testbasic/f3"), true);
        TS_ASSERT_EQUALS(dcc.list("/testbasic").size(), 3);
   }

    void testLock() {
        debby::ClientConfig conf;
        conf.addr.push_back(make_pair("localhost", 7555));
        conf.logLevel = "FATAL";

        debby::Client dc(conf);
        debby::Client dcc(conf);

        TS_ASSERT_EQUALS(dc.lock("/testbasic"), true);
        TS_ASSERT_EQUALS(dcc.lock("/testbasic"), false);
        
        dc.release("/testbasic");
        TS_ASSERT_EQUALS(dcc.lock("/testbasic"), true);
        TS_ASSERT_EQUALS(dc.lock("/testbasic"), false);

        dcc.close();
        TS_ASSERT_EQUALS(dc.lock("/testbasic"), true);
    }

    void xestCB() {
        debby::ClientConfig conf;
        conf.addr.push_back(make_pair("localhost", 7555));
        conf.logLevel = "FATAL";

        debby::Client dc(conf);

        dc.regcb("/testbasic/f1", debby::EChanged, shared_ptr<debby::Callback>(new MyCB()));
        dc.regcb("/testbasic/f3", debby::ECreated, shared_ptr<debby::Callback>(new MyCB()));
        dc.regcb("/test", debby::EChanged, shared_ptr<debby::Callback>(new MyCB()));
    }

};

