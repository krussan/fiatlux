package se.qxx.android.fiatlux.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import se.qxx.fiatlux.domain.FiatluxComm.FiatLuxService;

import com.google.protobuf.RpcChannel;
import com.google.protobuf.Service;
import com.googlecode.protobuf.socketrpc.RpcChannels;
import com.googlecode.protobuf.socketrpc.RpcConnectionFactory;
import com.googlecode.protobuf.socketrpc.SocketRpcConnectionFactories;

public class ClientConnectionPool<T extends Service> {

	private static ClientConnectionPool<? extends Service> instance;

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
	private T service;
	
	public T getNonBlockingService() {
		return service;
	}
	
	public void setNonBlockingService(T service) {
		this.service = service;
	}	

	public ClientConnectionPool(String serverIPaddress, int port) {
		this.setServerIPaddress(serverIPaddress);
		this.setPort(port);
		this.setupNonBlockingService();		
	}
	
	@SuppressWarnings("unchecked")
	private void setupNonBlockingService() {
		ExecutorService threadPool = Executors.newFixedThreadPool(1);
		
		RpcConnectionFactory connectionFactory = SocketRpcConnectionFactories.createRpcConnectionFactory(
				this.getServerIPaddress(), 
    			this.getPort()); 
    			
		RpcChannel channel = RpcChannels.newRpcChannel(connectionFactory, threadPool);
		
		this.setNonBlockingService((T) FiatLuxService.newStub(channel));		
	}

}
