/**
 * @file	test_fklib.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 *
 */

#include "fklib.h"
#include <boost/timer.hpp>
using namespace std;

int main() {
    cout <<"Test Timer ..." <<endl;
    fk::Timer t;
    cout <<t.elapsed() <<endl;
    sleep(1);
    cout <<t.elapsed() <<endl;

    cout <<"Test normalize_path() ..." <<endl;
    cout <<fk::normalize_path("/joke///") <<endl;
    cout <<fk::normalize_path("abc/joke///") <<endl;
    cout <<fk::normalize_path("abc/joke///hh") <<endl;
    cout <<fk::normalize_path("/joke///joke") <<endl;
    cout <<fk::normalize_path("////joke") <<endl;
    cout <<fk::normalize_path("/") <<endl;
    cout <<fk::normalize_path("") <<endl;
    cout <<fk::normalize_path("abc") <<endl;

    cout <<"Test parent_dir() ..." <<endl;
    cout <<fk::parent_dir("/abc") <<endl;
    cout <<fk::parent_dir("//abc/edf") <<endl;
    cout <<fk::parent_dir("/abc/edf/") <<endl;
    cout <<fk::parent_dir("abc") <<endl;
    cout <<fk::parent_dir("/abc///") <<endl;
    cout <<fk::parent_dir("/abc/def") <<endl;
    cout <<fk::parent_dir("/abc/def/g") <<endl;
}
