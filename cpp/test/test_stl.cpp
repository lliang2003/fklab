/**
 * @file	test_stl.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008��04��04�� 11ʱ18��21�� CST
 *
 */

#include <iostream>
#include <vector>
using namespace std;

class C {
public:
    typedef int type;
};

int main() {
    C cc;
    C::type a = 9;
    vector<int> v;
    for (vector<int>::iterator it = v.begin(); it != v.end(); ++it)
        cout <<*it <<endl;

    return 0;
}
