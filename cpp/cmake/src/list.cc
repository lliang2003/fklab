#include <iostream>
#include <fstream>
using namespace std;

int main(int argc, char **argv) {
    cout <<"List arguments:" <<endl;
    for (int i = 0; i < argc; ++i)
        cout <<i <<": " <<argv[i] <<endl;
    cout <<"List over." <<endl;
    ofstream fout("list.txt");
    fout <<"hello" <<endl;
    fout.close();
    cout <<"Create file over." <<endl;
    return 0;
}
