/**
 * @file	test_template.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年07月06日 22时24分18秒 CST
 *
 */

#include <iostream>
using namespace std;

template <bool> struct fassert;
template <> struct fassert<true> {};

struct MC { }; 

template <class T> struct TC {
        static void func() { cout <<__func__  <<" template" <<endl; }
};

template <> struct TC<int> {
        static void func() { cout <<__func__  <<" int " <<endl; }
};

struct NC {
        template <class T> static void func() { cout <<__func__  <<" template" <<endl; }
        //template <> static void func<int>() { cout <<__func__  <<" int" <<endl; }
};

template <class T> struct OpNewCreator {
    static T* create() { 
        cout <<"new type " <<typeid(T).name() <<endl;
        return new T; 
    }
};

template <class T, class Policy = OpNewCreator<T> > struct Managerx : public Policy { };
template <class Policy> struct Manager : public Policy { };

template <template <class> class Policy = OpNewCreator> 
struct Mgr { 
    template <class T> static T *create() { return Policy<T>::create(); }
    static int *createi() { return Policy<int>::create(); }
};

template <template <class> class Policy, class T> struct Mgrx : public Policy<T> { };
template <template <class> class Policy> struct Mgrx<Policy, int> : public Policy<int> { };

template <int NUM> 
void pn() {cout <<"num = " <<NUM <<endl; }

void test_template_func() {
    cout <<__func__ <<" ..." <<endl;

    pn<5>();
    pn<3>();
    //pn();

    cout <<__func__ <<" done" <<endl;
}

void test_template_class() {
    cout <<__func__ <<" ..." <<endl;

    fassert<true> f;
    fassert<1> f1;
    //fassert<(1<0)> f2;

    TC<bool>::func();
    TC<int>::func();
    NC::func<bool>();
    NC::func<int>();

    cout <<__func__ <<" done" <<endl;
}

void test_template() {
    cout <<__func__ <<" ..." <<endl;

    test_template_func();
    test_template_class();

    delete Managerx<int>::create();
    delete Manager<OpNewCreator<int> >::create();
    delete Manager<OpNewCreator<MC> >::create();
    delete Mgr<>::createi();
    delete Mgr<OpNewCreator>::createi();
    delete Mgr<OpNewCreator>::create<char>();
    delete Mgrx<OpNewCreator, int>::create();
    delete Mgrx<OpNewCreator, bool>::create();

    cout <<__func__ <<" done." <<endl;
}
