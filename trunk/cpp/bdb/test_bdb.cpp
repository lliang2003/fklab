/**
 * @file	test_bdb.cpp
 * 	
 * @author	Fan Kai(fk), Peking University	
 *
 */
#include <db_cxx.h>
#include <string>
#include <boost/scoped_ptr.hpp>
using namespace std;

int main() {
    DbEnv env(0);
    Db db(&env, 0);

    try {
        env.open("data/bdb", DB_CREATE|DB_INIT_MPOOL|DB_INIT_TXN|DB_INIT_LOCK|DB_INIT_LOG, 0);
        db.set_flags(DB_DUP);
        db.open(NULL, "test_bdb.db", NULL, DB_BTREE, DB_CREATE, 0);

        srand(time(0));
        for (int i = 0; i < 15; ++i) {
            int k = i%10;
            int d = rand();

            Dbt key(&k, sizeof(int));
            Dbt data(&d, sizeof(int));
            int r = db.put(NULL, &key, &data, DB_NOOVERWRITE);
            //int r = db.put(NULL, &key, &data, DB_AFTER);
            if (r == DB_KEYEXIST) {
                //db.del(NULL, &key, 0);
                cerr<<"key exists." <<endl;
            }
            //db.get(NULL, &key, &d, 0);
        }
        db.sync(0);
        cout <<"ok" <<endl;

        {
            Dbc *pc;
            db.cursor(NULL, &pc, 0);
            int r, k = 5, d = 2;
            Dbt key(&k, sizeof(int));
            Dbt data(&d, sizeof(int));
            pc->put(&key, &data, DB_KEYLAST);
            d = 9;
            pc->put(&key, &data, DB_KEYFIRST);
            k = 55, d = 99;
            pc->put(&key, &data, DB_KEYFIRST);
            //pc->put(&key1, &data1, DB_KEYFIRST);

            while ((r = pc->get(&key, &data, DB_PREV)) == 0) {
                int k = *(int *)key.get_data();
                int d = *(int *)data.get_data();
                cout <<k <<" " <<d <<endl;
            }
            cout <<endl;
            while ((r = pc->get(&key, &data, DB_NEXT)) == 0) {
                int k = *(int *)key.get_data();
                int d = *(int *)data.get_data();
                cout <<k <<" " <<d <<endl;
            }
            cout <<endl;

            k = 5;
            key.set_data(&k);
            pc->get(&key, &data, DB_SET);
            k = *(int *)key.get_data();
            d = *(int *)data.get_data();
            cout <<k <<" " <<d <<endl;
            while ((r = pc->get(&key, &data, DB_NEXT_DUP)) == 0) {
                int k = *(int *)key.get_data();
                int d = *(int *)data.get_data();
                cout <<k <<" " <<d <<endl;
            }
            pc->close();
        }
        cout <<"ok" <<endl;

        db.close(0);
        env.close(0);

    } catch (DbException &e) {
        cerr <<"Catch :" <<e.what() <<endl;
        return -1;
    } catch (std::exception &e) {
        cerr <<"Catch :" <<e.what() <<endl;
        return -1;
    } catch (...) {
        cerr <<"Catch unknown exception" <<endl;
        return -1;
    }
    return 0;
}

