/**
 * @file	test.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年03月31日 22时42分21秒 CST
 *
 */
#include <boost/scoped_ptr.hpp>
#include <boost/shared_ptr.hpp>
#include <boost/archive/text_oarchive.hpp>
#include <boost/archive/text_iarchive.hpp>
#include <boost/archive/binary_oarchive.hpp>
#include <boost/archive/xml_oarchive.hpp>
#include <boost/array.hpp>
#include <boost/tuple/tuple.hpp>
#include <boost/tuple/tuple_io.hpp>
#include <boost/regex.hpp>
#include <boost/algorithm/string.hpp>
#include <boost/filesystem/operations.hpp>
#include <fstream>
#include <iostream>
#include <vector>
using namespace std;
using namespace boost;
namespace fs=boost::filesystem; 

class NC : public noncopyable {
    public:
        NC() { cout <<"Construct " <<endl; }
        ~NC() {cout <<"Destruct " <<endl; }
};

struct C {
    friend class boost::serialization::access;
    private:
    template <class Archive>
        void serialize(Archive &ar, const unsigned int) {
            ar &i;
            ar &s;

        }
    public:
        C() {
            s = "fuck";
            cout <<"Construct " <<endl; }
        ~C() {cout <<"Destruct " <<endl; }
        int i;
        string s;
        char c[50];

        static void save(const C &cc, ofstream &fout) {
            archive::text_oarchive bo(fout);
            bo <<cc;
        }
};

void test_serialization() {
    shared_ptr<C> pp(new C());
    C cc;
    ofstream fout("a.txt");
    //archive::text_oarchive oa(fout);
    const C* ptr = &cc;
    for (int i = 0; i < 10; ++i)
        C::save(cc, fout);
}

void test_array() {
    array<int, 5> ai;
    for (array<int, 5>::iterator it = ai.begin();
            it != ai.end(); ++it)
        cout <<*it <<" ";
    cout <<endl;
    ai.assign(6);
    for (int i = 0; i < ai.size(); ++i) 
        cout <<ai[i] <<" ";
    cout <<endl;
    array<double, 0> ad;
    cout <<ad.size() <<endl;
}

void test_tuple() {
    tuple<int, double, string> t(1, 2, "fuck");
    cout <<t.get<0>() <<" " <<t.get<2>() <<endl;
    cout <<t <<endl;
    int i;
    string s;
    tie(i, tuples::ignore, s) = make_tuple(8, 9, "fuck");
    cout <<i <<" " <<s <<endl;

    vector<reference_wrapper<int> > rv;
    int a[3] = {1,2,3};
    for (int i = 0; i < 3; ++i)
        rv.push_back(ref(a[i]));
    for (int i = 0; i < 3; ++i)
        cout <<rv[i] <<" ";
    cout <<endl;
    a[1] = 4;
    for (int i = 0; i < 3; ++i)
        cout <<rv[i] <<" ";
    cout <<endl;
}

void test_regex() {
    string s = "(f(u)(ck))";
    string ss = "fuckk";
    regex r(s);
    smatch m;
    cout <<regex_match(ss, m, r) <<endl;
    cout <<m.size() <<" " <<r.mark_count() <<endl;
    for (int i = 0; i < m.size(); ++i)
        cout <<m[i] <<endl;
    regex_search(ss, m, r);
    cout <<m.size() <<" " <<r.mark_count() <<endl;
    for (int i = 0; i < m.size(); ++i)
        cout <<m[i] <<endl;
}

void test_string() {
    string str("fuck the world, FUCK");
    vector<string> sv;
    split(sv, str, is_any_of(" "));
    for (int i = 0; i < sv.size(); ++i)
        cout <<sv[i] <<endl;
    vector<iterator_range<string::iterator> > fv;
    find_all(sv, str, "fuck");
    for (int i = 0; i < sv.size(); ++i)
        cout <<sv[i] <<endl;
    ifind_all(sv, str, "fuck");
    for (int i = 0; i < sv.size(); ++i)
        cout <<sv[i] <<endl;
    to_upper(str);
    cout <<str <<endl;
}

void test_fs() {
    cout <<fs::current_path().string() <<endl;
    cout <<fs::initial_path().string() <<endl;
    fs::path p(".");
    cout <<p.root_name() <<endl;
    cout <<p.leaf() <<endl;
    cout <<p.is_complete() <<endl;
    cout <<p.root_directory() <<endl;
    cout <<getenv("HOME") <<endl;
}

int main() {
    //test_array();
    //test_tuple();
    //test_regex();
    //test_string();
    //shared_ptr<int> pi(new int);
    //shared_ptr<long long> pl(new long long);
    shared_ptr<C> pc(new C());
    //cout <<sizeof(pi) <<endl;
    //cout <<sizeof(pl) <<endl;
    cout <<sizeof(pc) <<endl;

    test_fs();

    return 0;
}

