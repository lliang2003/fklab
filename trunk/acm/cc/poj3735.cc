/**
 * @file    3735.cc
 * @author  FAN Kai (fankai@net.pku.edu.cn), Peking University 
 * @date    May 22 04:27:25 PM CST 2009
 */


#include <iostream>
#include <algorithm>
#include <cassert>
#include <vector>
using namespace std;

class Matrix {
public:
    Matrix(int row, int col) {
        this->row = row;
        this->col = col;
        dat.resize(row*col);
    }

    void print() {
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                cout <<e(i,j) <<"\t";
            }
            cout <<endl;
        }
    }

    inline long long & e(int x, int y) {
        //cout <<"row="<<row <<" col=" <<col <<" x=" <<x <<" y=" <<y <<endl;
        //assert(x >= 0 && x < row);
        //assert(y >= 0 && y < col);
        return dat[x*col+y];
    }

    Matrix operator*(Matrix &r) {
        assert(col == r.row);
        Matrix m(row, r.col);
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < r.col; ++j) {
                for (int k = 0; k < col; ++k) {
                    if (e(i,k)>0 && r.e(k,j)>0)
                        m.e(i,j) += e(i,k)*r.e(k,j);
                }
            }
        } 
        return m;
    }

    Matrix power(int n) {
        assert(row == col);
        if (n == 0) {
            return Matrix::unit(row);
        } 
        Matrix t = power(n/2);
        Matrix tt = t*t;
        if (n % 2 == 1)
            return (*this)*tt;
        else 
            return tt;
    }

    static Matrix unit(int num) {
        Matrix m(num, num);
        for (int i = 0; i < num; ++i) 
            m.e(i,i) = 1;
        return m;
    }
private:
    int row, col;
    vector<long long> dat;
};



void test() {
    Matrix m = Matrix::unit(4);
    Matrix ma = Matrix::unit(4);
    m.e(0,1) = 4;
    m.e(3,2) = 44;
    ma.e(0,3) = 3;
    ma.e(2,1) = 9;
    Matrix mm = m*ma;
    m.print();
    ma.print();
    mm.print();
    mm.power(2).print();
    //mm.power(3).print();
    Matrix mc = Matrix::unit(3);
    mc.e(0,0) = 2;
    mc.power(3).print();
}

int main() {
    //test(); return 0;
    int n, m, k;
    while (true) {
        cin >>n >>m >>k;
        if (n == 0) break;
        Matrix ma(1, n+1);
        ma.e(0, n) = 1;
        Matrix mo(n+1, n+1);
        for (int i = 0; i <=n; ++i)
            mo.e(i, i) = 1;

        for (int i = 0; i < k; ++i) {
            char c;
            int a, b;
            cin  >>c >>a;
            //cout <<c <<a <<endl;
            if (c == 'g') {
                mo.e(n, a-1) ++;
            } else if (c == 'e') {
                for (int j = 0; j <= n; ++j)
                    mo.e(j, a-1) = 0;
            } else {
                cin >>b;
                for (int j = 0; j <= n; ++j)
                    swap(mo.e(j, a-1), mo.e(j, b-1));
            }
            //mo.print();
        }
        //cout <<"mo" <<endl;mo.print();
        mo = mo.power(m);
        //cout <<"mo^" <<m <<endl;mo.print();
        //cout <<"ma" <<endl; ma.print();
        Matrix mm = ma*mo;
        //cout <<"ma*mo" <<endl; mm.print();
        for (int i = 0; i < n; ++i)
            cout <<mm.e(0, i) <<" " ;
        cout <<endl;
    }

    return 0;
}


