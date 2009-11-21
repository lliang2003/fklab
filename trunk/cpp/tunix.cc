/**
 * @file    tunix.cc
 * @author  FAN Kai (fankai@net.pku.edu.cn), Peking University 
 * @date    Dec 30 10:20:52 PM CST 2008
 */

#include <string.h>
#include <stdio.h>
#include <errno.h>

int main() {
    for (int i = 0; i < 132; ++i) {
        printf("%d %s\n", i, strerror(i));
        errno = i;
        perror("error");
    }
    return 0;
}


