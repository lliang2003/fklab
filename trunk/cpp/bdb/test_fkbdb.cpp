/**
 * @file	test_fkbdb.cpp
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	09/25/2008 09:15:43 AM CST
 *
 */
#include <fkbdb.h>
#include <iostream>
using namespace std;

int main() {
    fk::BDB db("fkbdb.db");
    cout <<db.exists(1) <<endl;
    db.put(1, 2);
    db.put(2, 4.5);
    db.put<double, char>(3.3, 'j');
    db.put(4, "hello");
    db.put("joke", "fine");
    db.put(4, "world");

    string s("pkucs");
    db.put(s, s);

    cout <<db.exists(2) <<endl;
    cout <<db.exists(3) <<endl;
    cout <<db.exists(s) <<endl;
    cout <<db.get<int>(1) <<endl;
    cout <<db.get<double>(2) <<endl;
    cout <<db.get<char>(3.3) <<endl;
    cout <<db.gets(4) <<endl;
    cout <<db.gets("joke") <<endl;
    cout <<db.gets(s) <<endl;

    return 0;
}


