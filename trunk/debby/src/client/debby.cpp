/**
 * @file	debby.cpp
 * @author	Fan Kai(fk), Peking University	
 */

#include "debby.h"
#include "ClientImpl.hpp"

using namespace debby;
using namespace std;
using namespace boost;

Client::Client(const ClientConfig &conf) { _client = new ClientImpl(conf); }

Client::~Client(){ delete _client; }

bool Client::create(const string &path, bool tmp) { return _client->create(path, tmp); }
void Client::remove(const string &path) { _client->remove(path); }
bool Client::exists(const string &path) { return _client->exists(path); }
bool Client::istmp(const string &path) { return _client->istmp(path); }
vector<string> Client::list(const string &path){ return _client->list(path); }

bool Client::lock(const string &name) { return _client->lock(name); }
void Client::release(const string &name) { _client->release(name); }

string Client::read(const string &path) { return _client->read(path); }
void Client::write(const string &path, const string &data) { 
    _client->write(path, data);
}

void Client::regcb(const string &path, EventType t, shared_ptr<Callback> pcb) { 
    _client->regcb(path, t, pcb);
}
void Client::clearcb(const string &path, EventType t) { _client->clearcb(path, t); }

void Client::close(){ _client->close(); }



