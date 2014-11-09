package se.qxx.android.fiatlux.client;

public class FiatluxConnectionMessage {
	private boolean result;
	private String message;
	
	public boolean result() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public FiatluxConnectionMessage(boolean result, String message) {
		this.setResult(result);
		this.setMessage(message);
	}
}
