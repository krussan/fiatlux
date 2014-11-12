package se.qxx.fiatlux.server;

import com.google.protobuf.Service;

public class TcpListener implements Runnable {

	private boolean isRunning = false;
	private int port;
	private Class<? extends Service> serviceType;
	
	public TcpListener(int port, Class<? extends Service> serviceType) {
		this.setPort(port);
		this.setServiceType(serviceType);
	}

	@Override
	public void run() {
		isRunning = true;
		
		CommRpcServer server = new CommRpcServer(port);

		//Log.Info(String.format("Starting up RPC server. Listening on port %s",  port), LogType.COMM);
		try {
			server.runServer(this.getServiceType());
		
			while (isRunning) {
				try {
					Thread.sleep(3000);

				} catch (InterruptedException e) {
					//Log.Error("RPC service interrupted", LogType.COMM, e);
					this.isRunning = false;
				}				
			}
		} catch (InstantiationException | IllegalAccessException ex) {
			ex.printStackTrace();
			//Log.Error("Error occured when starting up RPC server", LogType.COMM, ex);
		}

		//Log.Info("Stopping RPC server", LogType.COMM);
		server.stopServer();
	}
	
	public void stopListening() {
		//Log.Debug("RPC Server: stop listening called", LogType.COMM);
		this.isRunning = false;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private Class<? extends Service> getServiceType() {
		return serviceType;
	}

	private void setServiceType(Class<? extends Service> serviceType) {
		this.serviceType = serviceType;
	}	
	
}