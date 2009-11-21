/**
 * @file	test_net.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008��04��12�� 22ʱ36��45�� CST
 *
 */

#include <cstdlib>
#include <unistd.h>
#include <string>
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <iostream>
using namespace std;

int main() {
    struct hostent *h = gethostbyname("127.0.0.1");
    if (!h || h->h_length != sizeof(struct in_addr)) {
        cout <<"error" <<endl;
        return -1;
    }
    cout <<h->h_length <<endl;
    cout <<h->h_name <<endl;
    cout <<"1" <<h->h_addr_list[0] <<endl;
    cout <<"2" <<h->h_addr_list[1] <<endl;
    cout <<"3" <<h->h_addr_list[2] <<endl;
    cout <<"4" <<h->h_addr_list[3] <<endl;
    return 0;
}

