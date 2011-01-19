/**
 * @file	test.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年04月02日 19时36分21秒 CST
 *
 */
#include <iostream>
#include <string>
#include <fstream>
using namespace std;

template<typename T>
int f(T &t) {
    t = T();
    return 0;
}

class Base {
    public:
        Base() {
            cout <<"base" <<endl;
        }
};

class CLS: public Base {
public:
    static const CLS & instance() {
        static CLS c;
        return c;
    }

    void func() const{
        cout <<"hello" <<endl;
    }
private:
    CLS() {
        cout <<"construct " <<endl;
        //Base();
    }

};

int main() {
   CLS::instance().func();

   cout <<typeid(const char *).name() <<endl;
   cout <<typeid(char *).name() <<endl;
   cout <<typeid(string).name() <<endl;
    return 0;

}

