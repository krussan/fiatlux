package se.qxx.fiatlux.server;

import org.apache.commons.lang3.StringUtils;

public class Arguments {
	private boolean success = false;
	private int port = 2151;
	private static Arguments _instance = null;
	private String errorMessage = StringUtils.EMPTY;
	private String tabFile = "luxtab";
	
	private Arguments() {
		
	}
	
	public static void initialize(String commandargs[]) {
		Arguments.get().setup(commandargs);
	}
	
	private void setup(String commandargs[]) { 
		if (commandargs.length < 1) {
			this.setErrorMessage("There must be at least one argument on the command line");
			this.setSuccess(false);
		}
		else if (!StringUtils.isNumeric(commandargs[0])) {
			this.setErrorMessage(String.format("The argument %s is not a number"));
			this.setSuccess(false);
		}
		else {
			this.setPort(Integer.parseInt(commandargs[0]));
			this.setSuccess(true);

			if (commandargs.length > 1) {
			    this.setTabFile(commandargs[1]);
            }
		}		
	}
	
	public static Arguments get() {
		if (_instance == null)
			_instance = new Arguments();
		
		return _instance;
	}

	public boolean isSuccess() {
		return success;
	}

	private void setSuccess(boolean success) {
		this.success = success;
	}

	public int getPort() {
		return port;
	}

	private void setPort(int port) {
		this.port = port;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	private void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

    public String getTabFile() {
        return tabFile;
    }

    public void setTabFile(String tabFile) {
        this.tabFile = tabFile;
    }
}
