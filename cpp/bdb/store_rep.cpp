#include <db_cxx.h>
#include <iostream>
#include <fstream>
#include <string>
#include <vector>
using namespace std;

bool is_master = false;
bool done = false;

static void event_callback(DbEnv* dbenv, u_int32_t which, void *info) {
    cerr <<"get event" <<endl;
	//APP_DATA *app = (APP_DATA*)dbenv->get_app_private();

	info = NULL;		/* Currently unused. */

	switch (which) {
	case DB_EVENT_REP_MASTER:
		is_master = true;
        cerr <<"is master" <<endl;
		break;

	case DB_EVENT_REP_CLIENT:
		is_master = false;
        cerr <<"is client" <<endl;
		break;

	case DB_EVENT_REP_STARTUPDONE: /* FALLTHROUGH */
	case DB_EVENT_REP_NEWMASTER:
        done = true;
        cerr <<"new master" <<endl;
		case DB_EVENT_REP_PERM_FAILED:
		// I don't care about this one, for now.
		break;

	default:
		dbenv->errx("ignoring event %d", which);
	}
}
int main(int argc, char **argv) {
    try {
        ifstream fin("store_rep.conf");
        if(!fin.is_open()) exit(1);
        string host;
        int port;
        vector<string> hosts;
        vector<int> ports;
        while(fin >>host >>port) {
            hosts.push_back(host);
            ports.push_back(port);
        }

        DbEnv env(0);
	    env.set_event_notify(event_callback);
        env.repmgr_set_local_site(hosts[0].c_str(), ports[0], 0);
        for (int i = 1; i < hosts.size(); ++i) {
            env.repmgr_add_remote_site(hosts[i].c_str(), ports[i], NULL, 0);
        }
        env.repmgr_set_ack_policy(DB_REPMGR_ACKS_ALL);
        env.rep_set_priority(100);
        env.open("data/bdb", DB_CREATE|DB_INIT_MPOOL|DB_INIT_TXN|DB_INIT_LOCK|DB_INIT_LOG| DB_RECOVER |DB_THREAD | DB_INIT_REP , 0);
        env.repmgr_start(3, DB_REP_ELECTION);

        while (!done);

        Db db(&env, 0);
        db.set_flags(DB_DUP);
        while (true) {
            try {
                u_int32_t flags = DB_AUTO_COMMIT;
                //if (is_master) 
                    flags |= DB_CREATE;
                db.open(NULL, "store.db", NULL, DB_BTREE, flags, 0);
                break;
            } catch (DbDeadlockException e) {
            } catch (DbRepHandleDeadException e) {
            }
        }

        // put values
        DbTxn *txn = NULL;
        env.txn_begin(NULL, &txn, 0);
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
                break;
            }
        }
        if (sflag) txn->commit(0);

        // get values
        Dbt key, data;
        int r;
        Dbc *pc;
        db.cursor(NULL, &pc, 0);
        while ((r = pc->get(&key, &data, DB_NEXT)) == 0) {
            int k = *(int *)key.get_data();
            char *d = (char *)data.get_data();
            cout <<k <<" " <<d <<endl;
        }
        pc->close();
        db.close(0);
        env.close(0);

    } catch (DbException &e) {
        cerr <<"Catch :" <<e.what() <<endl;
        return -1;
    }
    return 0;
}
