package se.qxx.fiatlux.server;

public class FiatLuxServer {
	int port = 2151;
	TcpListener _listener;
	
	public static void main(String commandargs[]) {
		Arguments.initialize(commandargs);
		
		if (Arguments.get().isSuccess()) {
			FiatLuxServer s = new FiatLuxServer();
			s.initialize();
		}
		else {
			System.out.println(Arguments.get().getErrorMessage());
			printHelp();
		}		
	}

	private void initialize() {
		setupListening();
	}

	private void setupListening() {
		try {
			_listener = new TcpListener(Arguments.get().getPort(), FiatLuxServerConnection.class);
			Thread t = new Thread(_listener);
			t.start();
		}
		catch (Exception e) {
			
		}
	}

	public static void printHelp() {
		System.out.println("fiatlux-server");
		System.out.println("	run.sh [port]");
		System.out.println("		- port 		Specifices which port the server should listen to");
		System.out.println("");
		
	}
}
