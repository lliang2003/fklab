/**
 * @file	test_process.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年06月30日 19时12分00秒 CST
 *
 */

#include <signal.h>
#include <sys/wait.h>
#include <iostream>
using namespace std;

#define _START_FUNC printf("[%d] FUNC %s ...\n", getpid(), __func__)
#define _END_FUNC   printf("[%d] FUNC %s DONE.\n\n", getpid(),  __func__)
#define _P(x) printf("[%d] %s\n", getpid(), #x); x

void test_fork() {
    _START_FUNC;
    int num = 6;
    printf("[%d] num = %d\n", getpid(), num);
    int newpid = fork();
    if (newpid == 0) {
        printf("[%d] this is children, ppid = %d\n", getpid(), getppid());
        _P(++ num);
        printf("[%d] num = %d\n", getpid(), num);
        _P(while(true));
    } else {
        printf("[%d] this is parent, newpid = %d\n", getpid(), newpid);
        _P(kill(newpid, 9));
        int status;
        _P(waitpid(newpid, &status, 0));
        printf("[%d] WIFEXITED = %d\n", getpid(), WIFEXITED(status));
        printf("[%d] WIFSIGNALED = %d\n", getpid(), WIFSIGNALED(status));
        printf("[%d] WTERMSIG = %d\n", getpid(), WTERMSIG(status));
        printf("[%d] WIFSTOPPED = %d\n", getpid(), WIFSTOPPED(status));
        printf("[%d] WIFCONTINUED = %d\n", getpid(), WIFCONTINUED(status));
        printf("[%d] waitpid %d done\n", getpid(), newpid);
        printf("[%d] num = %d\n", getpid(), num);
    }
    _END_FUNC;
}

void test_vfork() {
    _START_FUNC;
    int num = 6;
    printf("[%d] num = %d\n", getpid(), num);
    int newpid = vfork();
    if (newpid == 0) {
        printf("[%d] this is child, ppid = %d\n", getpid(), getppid());
        _P(++ num);
        printf("[%d] num = %d\n", getpid(), num);
        _P(exit(3));
    } else {
        printf("[%d] this is parent, newpid = %d\n", getpid(), newpid);
        //_P(sleep(5));
        int status;
        _P(waitpid(newpid, &status, 0));
        printf("[%d] WIFEXITED = %d\n", getpid(), WIFEXITED(status));
        printf("[%d] WEXITSTATUS = %d\n", getpid(), WEXITSTATUS(status));
        printf("[%d] WIFSTOPPED = %d\n", getpid(), WIFSTOPPED(status));
        printf("[%d] WIFCONTINUED = %d\n", getpid(), WIFCONTINUED(status));
        printf("[%d] waitpid %d done\n", getpid(), newpid);
        printf("[%d] num = %d\n", getpid(), num);
    }
    _END_FUNC;
}

void test_exec() {
    _START_FUNC;
#define path "./test_process_aux"
    int newpid = fork();
    if (newpid == 0) {
        printf("[%d] this is children.\n", getpid());
        printf("[%d] execl %s\n", getpid(), path);
        _P(execl(path, path, "hello", NULL));
#undef path
    } else {
        printf("[%d] this is parent, newpid = %d\n", getpid(), newpid);
        _P(waitpid(newpid, NULL, 0));
        printf("[%d] waitpid %d done\n", getpid(), newpid);
    }
    _END_FUNC;
}

void test_nowait() {
    _START_FUNC;
    int newpid = fork();
    if (newpid == 0) {
        printf("[%d] this is children, ppid = %d\n", getpid(), getppid());
        _P(sleep(1));
        printf("[%d] this is children, ppid = %d\n", getpid(), getppid());
        _P(exit(0));
    } else {
        printf("[%d] this is parent, newpid = %d\n", getpid(), newpid);
    }
    _END_FUNC;
}

int main() {
    printf("test process ...\n");
    printf("pid  = %d\n", getpid());
    printf("ppid =  %d\n", getppid());
    printf("uid  = %d\n", getuid());
    printf("euid = %d\n", geteuid());
    printf("gid  = %d\n", getgid());
    printf("egid = %d\n", getegid());
    printf("\n");
    
    test_fork();
    test_vfork();
    test_exec();
    //test_nowait();
    printf("test process done.\n"); 
    return 0;
}
