/**
 * @file	test_icethread.cpp
 * 	
 * @author	Fan Kai(fk), Peking University	
 *
 */

#include <IceUtil/Mutex.h>
#include <IceUtil/RecMutex.h>
#include <IceUtil/Thread.h>
#include <IceUtil/Exception.h>
#include <iostream>
using namespace std;

class MT : public IceUtil::Thread {
public:
    void run() {
        cout <<"start" <<endl;
        //new char[8*1024*1024];
        //throw exception();
        sleep(3);
        cout <<"finish" <<endl;
    }
    
};

int main() {
    try {
        IceUtil::ThreadPtr tp = new MT();
        tp->start();
        tp->getThreadControl().join();
    } catch (exception &ex) {
        cerr <<"Catch: " <<ex.what() <<endl;
    }
    IceUtil::ThreadPtr tp = new MT();
    tp->start();
    tp->getThreadControl().join();

    try {
        int i = 0;
        while (true) {
            IceUtil::ThreadPtr tp = new MT();
            tp->start(64*1024*1024);//.detach();
            cout <<i++ <<" " <<tp->getThreadControl().id() <<endl;
            sleep(1);
        }
    } catch (IceUtil::Exception &ex) {
        cout <<ex <<endl;
    }

    return 0;
}

