package se.qxx.fiatlux.server;

public class TcpListener implements Runnable {

	private boolean isRunning = false;
	private int port;
	
	public TcpListener(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		isRunning = true;
		
		FiatLuxRpcServer server = new FiatLuxRpcServer(port);
  
		//Log.Info(String.format("Starting up RPC server. Listening on port %s",  port), LogType.COMM);
		try {
			server.runServer(FiatLuxServerConnection.class);
		
			while (isRunning) {
				try {
					Thread.sleep(3000);

				} catch (InterruptedException e) {
					//Log.Error("RPC service interrupted", LogType.COMM, e);
					this.isRunning = false;
				}				
			}
		} catch (InstantiationException | IllegalAccessException ex) {
			//Log.Error("Error occured when starting up RPC server", LogType.COMM, ex);
		}

		//Log.Info("Stopping RPC server", LogType.COMM);
		server.stopServer();
	}
	
	public void stopListening() {
		//Log.Debug("RPC Server: stop listening called", LogType.COMM);
		this.isRunning = false;
	}

	
	
}