/**
 * @file	test_ice.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 *
 */

#include <Ice/Ice.h>
#include <IceUtil/Mutex.h>
#include "ice/Transfer.h"
#include <map>
#include <boost/timer.hpp>
using namespace fktest;
using namespace std;

class TransferI : public Transfer {
public:
    void calc_speed(int n) {
        IceUtil::Mutex::Lock lock(mutex_);
        nbytes_ += n;
        if (t_.elapsed() < 1) return;
        double s = double(nbytes_) / t_.elapsed() / 1024;
       // cout <<"\rreceive " <<nbytes_ <<" in " <<t_.elapsed() <<" seconds";
        cout <<"\rspeed: " <<s <<" KB/s      " <<flush;
        nbytes_ = 0;
        t_.restart();
    }

    virtual int send(const vector<unsigned char> &bytes, int id, const Ice::Current &) {
        mcb[id] += bytes.size();
        //cout <<"Receive " <<bytes.size() <<" bytes from " <<id 
        //    <<" , " <<mcb[id] <<" bytes in total." <<endl;
        calc_speed(bytes.size());
        return bytes.size();
    }

    virtual int sendsn(int sn, int id, const Ice::Current &) {
        if (msn.find(id) == msn.end() || msn[id] == sn - 1) {
            //cout <<"Client " <<id << " send sn " <<sn <<endl;
            //if (sn % 100 == 0) cout <<"." <<flush;
            msn[id] = sn;
            return sn;
        }
        cout <<"Find error: id = " <<id <<" msn[id] = " <<msn[id] 
            <<" sn = " <<sn <<endl;
        msn[id] = sn;
        return -1;
    }

private:
    boost::timer t_;
    IceUtil::Mutex mutex_;
    map<int, int> msn;
    map<int, int> mcb;
    int nbytes_;
};

void test_message(TransferPrx &tr, int n) {
    cout <<"Testing message ..." <<endl;
    boost::timer t;
    for (int i = 0; i < n; ++i) 
        tr->sendsn(i, getpid());
    cout <<"fktest client[" <<getpid() <<"] send " <<n <<" messages." <<endl;
}

void test_speed(TransferPrx &tr, int n) {
    cout <<"Testing speed ... " <<endl;
    boost::timer t;
    ByteSeq bytes;
    int size = 1024 * 1024;
    bytes.resize(size);
    for (int i = 0; i < n; ++i) tr->send(bytes, getpid());
    cout <<"fktest client[" <<getpid() <<"] send " <<n * size 
        <<" bytes in " <<t.elapsed() <<" seconds." <<endl;
}

void client(const string &addr = "localhost") {
    Ice::CommunicatorPtr ic;
    try {
        Ice::InitializationData id;
        Ice::PropertiesPtr props = Ice::createProperties();
        props->setProperty("Ice.MessageSizeMax", "2048");
        id.properties = props;
        ic = Ice::initialize(id);
        Ice::ObjectPrx base = 
            ic->stringToProxy("fktest:tcp -h " + addr + " -p 12345");
        TransferPrx tr = TransferPrx::checkedCast(base);
        if (!tr) {
            cerr <<"Invalid proxy" <<endl;
        } else {
            srand(time(0));
            if (getpid() % 5 < 3) test_message(tr, 10000);
            else test_speed(tr, 1000);
        }
    } catch (const Ice::Exception &ex) {
        cerr <<ex <<endl;
    }
    if (ic) ic->destroy();
}

void server() {
    Ice::CommunicatorPtr ic;
    try {
        Ice::InitializationData id;
        Ice::PropertiesPtr props = Ice::createProperties();
        props->setProperty("Ice.MessageSizeMax", "2048");
        id.properties = props;
        ic = Ice::initialize(id);
        Ice::ObjectAdapterPtr adapter = ic->createObjectAdapterWithEndpoints(
                "fkadapter", "default -p 12345");
        Ice::ObjectPtr obj = new TransferI();
        adapter->add(obj, ic->stringToIdentity("fktest"));
        adapter->activate();
        ic->waitForShutdown();
        cout <<"fktest server started." <<endl;
    } catch (const Ice::Exception &e) {
        cerr <<e <<endl;
    }
    if (ic) ic->destroy();
}


int main(int argc, char **argv) {
    if (argc < 2 || (argv[1][0] != 's' && argv[1][0] != 'c')) {
        cout <<"Usage: " <<argv[0] <<" s[erver]|c[lient] [addr]" <<endl;
        return 0;
    }

    if (argv[1][0] == 's') server();
    else if (argc > 2) client(argv[2]);
    else client();

    return 0;
}

