#include <db_cxx.h>
#include <iostream>

using namespace std;

int main(int argc, char **argv) {
    try {
        DbEnv env(0);
        Db db(&env, 0);
        env.open("data/bdb", DB_CREATE|DB_INIT_MPOOL|DB_INIT_TXN|DB_INIT_LOCK|DB_INIT_LOG, 0);
        db.set_flags(DB_DUP);
        db.open(NULL, "store.db", NULL, DB_BTREE, DB_CREATE|DB_AUTO_COMMIT, 0);

        // put values
        DbTxn *txn = NULL;
        DbTxn *txn2 = NULL;
        env.txn_begin(NULL, &txn, 0);
        env.txn_begin(NULL, &txn2, 0);
        int t = time(0);
        bool sflag = true;
        for (int i = 1; i < argc; ++i) {
            cout <<"store " <<argv[i] <<endl;
            ++t;
            Dbt key(&t, sizeof(int));
            Dbt data(argv[i], strlen(argv[i]) + 1);
            int r = db.put(txn, &key, &data, DB_NOOVERWRITE);
            if (r == DB_KEYEXIST) {
                cerr<<"key exists." <<endl;
                sflag = false;
                txn->abort();
                txn2->abort();
                break;
            }
            //r = db.put(txn, &key, &data, DB_NOOVERWRITE);
            if (r == DB_KEYEXIST) {
                cerr<<"2 key exists." <<endl;
                sflag = false;
                txn->abort();
                txn2->abort();
                break;
            }
        }

        if (sflag) {
            txn->commit(0);
            txn2->commit(0);
        }
        // get values
        Dbt key, data;
        int r;
        Dbc *pc;
        cerr <<"done" <<endl;
        db.cursor(NULL, &pc, 0);
        cerr <<"done" <<endl;
        while ((r = pc->get(&key, &data, DB_NEXT)) == 0) {
        cerr <<"done" <<endl;
            int k = *(int *)key.get_data();
            char *d = (char *)data.get_data();
            cout <<k <<" " <<d <<endl;
        }
        cerr <<"done" <<endl;
        pc->close();
        cerr <<"done" <<endl;


        db.close(0);
        env.close(0);

    } catch (DbException &e) {
        cerr <<"Catch :" <<e.what() <<endl;
        return -1;
    }
    return 0;
}
