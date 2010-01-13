/**
 * @file    test_mpi.cc
 * @author  FAN Kai (fankai@net.pku.edu.cn), Peking University 
 * @date    Jan 09 19:03:23 CST 2010
 */


#include <iostream>
#include <mpi/mpi.h>
using namespace std;

int main(int argc, char **argv) {
    int size = 4;
    int rank;
    int r = MPI_Init(&argc, &argv);
    if (r != MPI_SUCCESS) {
        printf("error.\n");
        MPI_Abort(MPI_COMM_WORLD, r);
    }
    printf("hello\n");
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    printf("size=%d rank=%d\n", size, rank);
    MPI_Finalize();
    return 0;
}


