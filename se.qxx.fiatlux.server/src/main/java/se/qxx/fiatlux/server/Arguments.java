package se.qxx.fiatlux.server;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.lang3.StringUtils;

public class Arguments {
	private int port = 2151;
	private static Arguments _instance = null;
	private boolean mock;
	private String file = "luxtab";

	private Arguments() {
	}
	
	public static void initialize(String commandargs[]) {
		Arguments.get().setup(commandargs);
	}
	
	private void setup(String commandargs[]) {
		ArgumentParser parser = ArgumentParsers.newFor("FiatluxServer").build()
				.defaultHelp(true)
				.description("Starts the fiatlux server");
		parser.addArgument("-p", "--port")
				.type(Integer.class)
				.setDefault(2152)
				.help("Sets the port the server should listen to. Defaults to 2152");
		parser.addArgument("-f", "--file")
				.setDefault("luxtab")
				.help("Sets the scheduling file to read from. Defaults to luxtab");
		parser.addArgument("-m", "--mock")
				.type(Boolean.class);

		Namespace ns = null;
		try {
			ns = parser.parseArgs(commandargs);

			this.setPort(ns.getInt("port"));
			this.setFile(ns.getString("file"));
			this.setMock(ns.getBoolean(("mock")));
		}
		catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}

	}
	
	public static Arguments get() {
		if (_instance == null)
			_instance = new Arguments();
		
		return _instance;
	}

	public int getPort() {
		return port;
	}

	private void setPort(int port) {
		this.port = port;
	}

	public String getFile() {return file;}
	private void setFile(String file) {
		this.file = file;
	}

	public boolean isMock() {
		return mock;
	}

	private void setMock(boolean mock) {
		this.mock = mock;
	}
}
