/**
 * @file    fkbdb.h
 * 
 * @author  Fan Kai(fk), Peking University
 * @date    09/25/2008 09:00:16 AM CST
 *
 */

#ifndef  FKBDB_FILE_HEADER_INC
#define  FKBDB_FILE_HEADER_INC

#include <db_cxx.h>
#include <iostream>
using namespace std;


namespace fk {

// This class wraps berkeley db for convenience.
class BDB {
public:
    BDB(const char *dbpath) :db(NULL, 0) {
        db.open(NULL, dbpath, NULL, DB_BTREE, DB_CREATE, 0);
    }

    ~BDB() {
        db.close(0);
    }

    void sync() {
        db.sync(0);
    }

    template<typename T, typename U>
    void put(T key, U data) {
        Dbt k = mkdbt(key);
        Dbt d = mkdbt(data);
        db.put(NULL, &k, &d, 0);
    }

    template<typename T>
    bool exists(T key) {
        Dbt k = mkdbt(key);
        return db.exists(NULL, &k, 0) == 0;
    }


    template<typename T, typename U>
    void get(T key, U &data) {
        data = get<T, U>(key);
    }

    template<typename U, typename T>
    U get(T key) {
        Dbt k = mkdbt(key);
        Dbt d;
        int r = db.get(NULL, &k, &d, 0);
        if (r == DB_NOTFOUND)
            return U();
        else
            return *(U *)(d.get_data());
    }

    template<typename T>
    string gets(T key) {
        Dbt k = mkdbt(key);
        cout <<k.get_size() <<endl;
        Dbt d;
        int r = db.get(NULL, &k, &d, 0);
        if (r == DB_NOTFOUND)
            return NULL;
        else
            return string((char *)d.get_data());
    }


private:
    Db db;
    Dbt key;
    Dbt data;

    template<typename T>
    Dbt mkdbt(T &dat) {
        Dbt dbt(&dat, sizeof(T));
        return dbt;
    }

    Dbt mkdbt(char *dat) {
        Dbt dbt(dat, strlen(dat) + 1);
        return dbt;
    }

    Dbt mkdbt(const char *dat) {
        return mkdbt(const_cast<char *>(dat));
    }

    Dbt mkdbt(string &dat) {
        return mkdbt(dat.c_str());
    }

};  // class BDB

}   // namespace fk

#endif   /* ----- #ifndef FKBDB_FILE_HEADER_INC  ----- */

