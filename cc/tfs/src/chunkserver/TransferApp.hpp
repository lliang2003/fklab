
#ifndef _CHUNK_SERVER_TRANSFER_APP_HPP
#define _CHUNK_SERVER_TRANSFER_APP_HPP

#include <iostream>
#include <string>
#include <sstream>
#include <cassert>
#include <Ice/Application.h>
#include "TransferImpl.hpp"
#include "ChunkManager.hpp"


using namespace std;
namespace tfs
{

	class TransferApp : virtual public Ice::Application 
	{
	public:
		TransferApp(ChunkManager * chunk_mgr){
			m_chunk_mgr = chunk_mgr;
		}

		int run(int argc,char ** argv) 
		{
			// configuration file
			//assert(argc == 2);
			
			Ice::PropertiesPtr props = Ice::createProperties(argc, argv);
			// Make sure that network and protocol tracing are off.
			// //
			props->setProperty("Ice.ThreadPool.Server.Size", "16");
			props->setProperty("Ice.ThreadPool.Server.SizeMax", "32");
			props->setProperty("Ice.MessageSizeMax", "65535");

			// props->setProperty("Ice.Trace.Protocol", "0");
			// // Initialize a communicator with these properties.
			// //
			Ice::InitializationData id;
			id.properties = props;
			Ice::CommunicatorPtr ic = Ice::initialize(id);
			//

			ChunkServerConf * pConf = ChunkServerConf::getInstance(); 

			stringstream ss;
			ss << "tcp -p " << pConf->DATA_SOCKET_PORT;

			m_adapter = ic->createObjectAdapterWithEndpoints("chunkserver", ss.str());

			slice::TransferPtr pTransfer= new TransferImpl(m_chunk_mgr);

			m_adapter->add(pTransfer, ic->stringToIdentity("Transfer"));            
			m_adapter->activate();

			ic->waitForShutdown();

			if (interrupted()) {
				LOG_INFO("TransferApp() Received signal, shutting down");
			}

			m_adapter = 0;
			log4cplus::getNDC().pop();
			return 0;
		}
	private:
		Ice::ObjectAdapterPtr m_adapter;
		ChunkManager * m_chunk_mgr;
	};
}

#endif
