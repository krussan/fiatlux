package se.qxx.android.fiatlux.model;

public class ModelNotInitializedException extends Exception {
	public ModelNotInitializedException() {
		super("Model has not been initialized. Please call method initialize first.");
	}
}
