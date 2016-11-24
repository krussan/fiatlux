package se.qxx.fiatlux.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.Service;

public class TcpListener implements Runnable {

	private static final Logger logger = LogManager.getLogger(FiatLuxServer.class);
	
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

		logger.info(String.format("Starting up RPC server. Listening on port %s",  port));
		
		try {
			server.runServer(this.getServiceType());
		
			while (isRunning) {
				try {
					Thread.sleep(3000);

				} catch (Exception e) {
					logger.error("RPC service interrupted", e);
					this.isRunning = false;
				}				
			}
		} catch (InstantiationException | IllegalAccessException ex) {
			logger.error("Error occured when starting up RPC server", ex);
		}

		logger.info("Stopping RPC server");
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