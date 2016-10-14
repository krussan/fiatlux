package se.qxx.fiatlux.server;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;

import org.apache.logging.log4j.LogManager;
 
public class FiatLuxServer {
	TcpListener _listener;
	
    private static final Logger logger = LogManager.getLogger(FiatLuxServer.class);
    	
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
			logger.debug("Starting up");
			
			_listener = new TcpListener(Arguments.get().getPort(), FiatLuxServerConnection.class);
			Thread t = new Thread(_listener);
			t.start();
			
		}
		catch (Exception e) {
			logger.error("Error in main", e);
		}
		
		logger.debug("Exiting ...");
	}

	public static void printHelp() {
		System.out.println("fiatlux-server");
		System.out.println("	run.sh [port] [scheduling-file]");
		System.out.println("		- port 				Specifices which port the server should listen to");
		System.out.println("		- scheduling-file 	Specifies where to read the scheduled jobs");
		System.out.println("");
		
	}
}
