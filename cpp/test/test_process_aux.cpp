/**
 * @file	test_process_aux.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 *
 */

#include <iostream>
using namespace std;

int main(int argc, char **argv) {
    printf("%s starts ...\n", argv[0]);
    printf("argc = %d\n", argc);
    for (int i = 0; i < argc; ++i)
        printf("argv[%d]:%s\n", i, argv[i]);
    printf("%s ends.\n", argv[0]);
    return 0;
}
