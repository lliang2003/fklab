#include <iostream>
#include <boost/timer.hpp>
#include <vector>
#include <cassert>
#include <fstream>
using namespace std;

const int NR = 1000000; //1M
const int LR = 100;
const int LK = 10;

struct Record {
    char s[100];
};

bool operator<(const struct Record &a, const struct Record &b) {
    return memcmp(a.s, b.s, LK) < 0;
}

class CMP {
public:
    bool operator()(char *a, char *b) {
        return memcmp(a, b, LK) < 0;
    }
};

int main() {
    char *buf = new char[NR * LR];
    ifstream fin;
    ofstream fout;
    boost::timer t;
    vector<int> vi;
    vi.push_back(1);
    vi.push_back(5);
    vi.push_back(10);
    vi.push_back(100);
    vi.push_back(1000);

    for (int i = 0; i < 5; ++i) { 
        // Read data
        fin.open("a.dat", ios::binary);
        fin.read(buf, NR*LR);
        //random_shuffle(buf, buf + NR*LR);
        fin.close();

        // Use direct sort
        t.restart();
        int nr = NR / vi[i];
        for (int j = 0; j < vi[i]; ++j) {
            sort((Record *)buf + nr*j, (Record *)buf + nr*j + nr);
        }
        cout <<t.elapsed() <<endl;

        // Write data
        fout.open("b.dat", ios::binary|ios::trunc);
        fout.write(buf, NR*LR);
        fout.close();
        cout <<t.elapsed() <<endl <<endl;
    }

    return 0;

    // Read data again
    fin.open("a.dat", ios::binary);
    fin.read(buf, NR*LR);
    //random_shuffle(buf, buf + NR*LR);
    fin.close();

    // Sort pointer
    t.restart();
    vector<char *> vsp(NR);
    for (int i = 0; i < NR; ++i) {
        vsp[i] = buf + i * LR;
    }
    sort(vsp.begin(), vsp.end(), CMP());
    cout <<t.elapsed() <<endl;

    // Write data again
    fout.open("c.dat", ios::binary|ios::trunc);
    for (int i = 0; i < NR; ++i)
        fout.write(vsp[i], LR);
    fout.close();
    cout <<t.elapsed() <<endl;

    return 0;
}




