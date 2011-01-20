/**
 * @file    c.cc
 * @author  FAN Kai (fankai@net.pku.edu.cn), Peking University 
 * @date    Feb 21 13:10:19 CST 2010
 */


#include <iostream>
#include <string>
#include <vector>
using namespace std;

struct S {
    int s;
};

class Base {
public:
    Base() {
        cout <<"construct Base" <<endl;
    }
    Base(const Base& other) {
        cout <<"copy constructor" <<endl;
    }
};

class C {
public:
    C():ii(0){
    }
    C(int &i):ii(i){
    }
    C(const C &o):ii(3){
        cout <<"coo" <<endl;
    }
    C(C &o):ii(3){
        cout <<"co" <<endl;
    }
    const int ii;
    void f() {}
    C& operator=(C &other) {
        cout <<"assign" <<endl;
    }
    C* operator&() {
        return NULL;
    }
    int n;
};

class D:public C {
public:
    D():C() {
        C::f();
    }
};

int main() {
    S ss;
    int n = 4;
    C cc(n); 
    C bb(cc);
    C aa=bb;
    bb=cc;
    cout <<cc.n<<endl;
    cout <<ss.s<<endl;
    cout <<n <<endl;
    cout <<&bb <<endl;
    const C* pc = &cc;
    string str;
    cout <<sizeof(str) <<endl;

    vector<Base> vb(5);
    cout <<vb.size() <<endl;
    vb.reserve(10);
    vb.push_back(Base());
    cout <<vb.capacity() <<endl;
    Base b = vb[0];
    vector<Base> vc(vb);
    vb.swap(vc);
    cout <<vb.capacity() <<endl;

}


