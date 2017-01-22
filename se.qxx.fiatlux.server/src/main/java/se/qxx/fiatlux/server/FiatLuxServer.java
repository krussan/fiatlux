package se.qxx.fiatlux.server;

import org.apache.logging.log4j.Logger;

import com.sun.jna.Native;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
 
public class FiatLuxServer {
	private static TellstickLibrary lib;
	
	TcpListener _listener;
	
    private static final Logger logger = LogManager.getLogger(FiatLuxServer.class);
	private static FiatluxScheduler scheduler;
    
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
		setupScheduling();
	}

	private void setupScheduling() {
		scheduler = new FiatluxScheduler();
				
		try (BufferedReader br = new BufferedReader(new FileReader("luxtab"))){ 
			String line = br.readLine();
			logger.debug(String.format("Parsing line :: %s", line));
			
			while (line != null) {
				String[] splits = line.split("\\s+");
				
				if (!StringUtils.startsWithIgnoreCase(line, "#")) { 
					if (StringUtils.startsWithIgnoreCase(line, "COORDS")) {
						if (splits.length >= 3) {
							scheduler.setLatitude(Double.parseDouble(splits[1]));
							scheduler.setLongitude(Double.parseDouble(splits[2]));
						}
					}
					else if (StringUtils.startsWithIgnoreCase(line, "TIMEZONE")) {
						if (splits.length >= 2) {
							scheduler.setTimezone(splits[1]);
						}
					}
					else {
						scheduler.schedule(line);
					}
				}
				
				line = br.readLine();
			}
		}
		catch (FileNotFoundException fex) {
			// do nothing = no scheduling
			logger.error("File not found", fex);
		}
		catch (IOException ioex) {
			// do nothing = no scheduling
			logger.error("Error occured while reading file", ioex);
		}
		
		scheduler.start();
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
	
	public static TellstickLibrary getNative() {
		if (lib == null)
			lib = (TellstickLibrary)Native.loadLibrary("libtelldus-core.so.2", TellstickLibrary.class);		

		return lib;
	}
	
	public static FiatluxScheduler getScheduler() {
		return scheduler;
	}

}
