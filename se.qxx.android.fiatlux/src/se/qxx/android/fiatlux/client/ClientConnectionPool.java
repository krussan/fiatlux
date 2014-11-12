package se.qxx.android.fiatlux.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import se.qxx.fiatlux.domain.FiatluxComm.FiatLuxService;

import com.google.protobuf.RpcChannel;
import com.googlecode.protobuf.socketrpc.RpcChannels;
import com.googlecode.protobuf.socketrpc.RpcConnectionFactory;
import com.googlecode.protobuf.socketrpc.SocketRpcConnectionFactories;

public class ClientConnectionPool {

	private static ClientConnectionPool instance;

	public String getServerIPaddress() {
		return serverIPaddress;
	}

	public void setServerIPaddress(String serverIPaddress) {
		this.serverIPaddress = serverIPaddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private String serverIPaddress;
	private int port;
	private FiatLuxService service;
	
	public FiatLuxService getNonBlockingService() {
		return service;
	}
	
	public void setNonBlockingService(FiatLuxService service) {
		this.service = service;
	}	

	private FiatluxConnectionPool() {
	}
	
	public static void setup(String serverIPaddress, int port){
		FiatluxConnectionPool.get().setServerIPaddress(serverIPaddress);
		FiatluxConnectionPool.get().setPort(port);
		FiatluxConnectionPool.get().setupNonBlockingService();
	}

	public static FiatluxConnectionPool get() {
		if (instance == null)
			instance = new FiatluxConnectionPool();
		
		return instance;
	}
	
	private void setupNonBlockingService() {
		ExecutorService threadPool = Executors.newFixedThreadPool(1);
		
		RpcConnectionFactory connectionFactory = SocketRpcConnectionFactories.createRpcConnectionFactory(
				this.getServerIPaddress(), 
    			this.getPort()); 
    			
		RpcChannel channel = RpcChannels.newRpcChannel(connectionFactory, threadPool);
		
		this.setNonBlockingService(FiatLuxService.newStub(channel));		
	}

}
