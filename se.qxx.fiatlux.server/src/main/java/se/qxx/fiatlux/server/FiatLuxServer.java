package se.qxx.fiatlux.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
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
	
    private static final Logger logger = LogManager.getLogger(FiatLuxServer.class);
	private static FiatluxScheduler scheduler;
	private Server server;

	public static void main(String commandargs[]) {
		Arguments.initialize(commandargs);
		
		FiatLuxServer s = new FiatLuxServer();
		try {
			s.initialize();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void initialize() throws InterruptedException {
		setupListening();
		setupScheduling();

		server.awaitTermination();
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
			server = ServerBuilder.forPort(Arguments.get().getPort())
					.addService(new FiatLuxServerConnection()).build();

			server.start();
		}
		catch (Exception e) {
			logger.error("Error in main", e);
		}
		
		logger.debug("Exiting ...");
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
