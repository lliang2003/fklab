/**
 * @file	TestStress.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 *
 */

#include <client/debby.h>
#include <iostream>
#include <IceUtil/Thread.h>
#include <boost/lexical_cast.hpp>
using namespace std;

class MyCB: public debby::Callback {
public:
    void run() {
        //time_t t = time(0);
        //cout <<"MyCB invoked at " <<ctime(&t) <<endl;
        ++_t;
    }

    int _t;
};

class Test : public IceUtil::Thread {
public:
    Test(int id):_id(id){}

    void run() {
        debby::ClientConfig conf;
        conf.addr.push_back(make_pair("localhost", 7555));
        conf.logLevel = "Debug";

        debby::Client dc(conf);

        string dir = "/" + boost::lexical_cast<string>(_id);

        boost::shared_ptr<MyCB> pcb(new MyCB());
        pcb->_t = 0;
        dc.regcb(dir, debby::EDirChanged, pcb);
        vector<string> vs = dc.list(dir);
        for (int i = 0; i < vs.size(); ++i) {
            assert(dc.exists(vs[i]) == true);
            dc.remove(vs[i]);
        }
        cout <<"Test " <<_id <<" remove " <<vs.size() <<" files" <<endl;

        for (int i = 0; i < 10; ++i) {
            int r = rand();
            string file = dir + "/" + boost::lexical_cast<string>(r);
            if (dc.exists(file)) continue;
            dc.create(file, _id%2);
            assert(dc.read(file) ==  "");
            dc.write(file, "hello");
            assert(dc.read(file) == "hello");
        }

        cout <<"Test " <<_id <<" create " <<dc.list(dir).size() <<" files" <<endl;
        //sleep(1);
        cout <<"Test " <<_id <<" receive " <<pcb->_t <<" events." <<endl;

        dc.close();
    }

private:
    int _id;
};

int main() {
    time_t t = time(0);
    vector<IceUtil::ThreadPtr> vt;
    for (int i = 0; i < 10; ++i) {
        vt.push_back(new Test(i));
        //sleep(1);
        vt.back()->start();
    }

    for (int i = 0; i < vt.size(); ++i) {
        vt[i]->getThreadControl().join();
    }

    cout <<"Cost " <<time(0) - t <<" seconds." <<endl;
    return 0;
}







