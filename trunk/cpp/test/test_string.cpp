/**
 * @file	test_string.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年07月05日 19时52分50秒 CST
 *
 */

#include <iostream>
#include <string>
using namespace std;

int main() {
    string str;
    str.append("hello");
    str.append(" world");
    cout <<str.size() <<" " <<str <<endl;
    str[5] = '\0';
    cout <<str.size() <<" " <<str <<endl;

    const char *s = "abcd\0efg";
    str = s;
    cout <<str.size() <<" " <<str <<endl;

    return 0;
}
