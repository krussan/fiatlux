package se.qxx.android.fiatlux.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import se.qxx.fiatlux.domain.FiatluxComm.Device;
import se.qxx.fiatlux.domain.FiatluxComm.ListOfDevices;

public class Model {
	private static Model _instance;
	
	private ListOfDevices devices;
	private boolean initialized = false;
	
	private Model() {
		
	}
	
	public interface ModelUpdatedEventListener {
		public void handleModelUpdatedEventListener(java.util.EventObject e);
	}

	private List<ModelUpdatedEventListener> _listeners = new ArrayList<Model.ModelUpdatedEventListener>();
	
	public synchronized void addEventListener(ModelUpdatedEventListener listener) {
		_listeners.add(listener);
	}
	
	public synchronized void removeEventListener(ModelUpdatedEventListener listener) {
		_listeners.remove(listener);
	}
	
	private synchronized void fireModelUpdatedEvent() {
		ModelUpdatedEvent event = new ModelUpdatedEvent(this);
		Iterator<ModelUpdatedEventListener> i = _listeners.iterator();

		while(i.hasNext())  {
			i.next().handleModelUpdatedEventListener(event);
		}
	}
	
	
	public static Model get() {
		if (_instance == null)
			_instance = new Model();
		
		return _instance;
	}

	public void initialize(ListOfDevices dev) {
		this.setDevices(dev);
		this.setInitialized(true);
	}
	
	public int countDevices() throws ModelNotInitializedException {
		assertInitialized(); 
		
		return this.getDevices().getDeviceCount();
	}

	public Device getDevice(int index) throws ModelNotInitializedException {
		assertInitialized(); 
		
		return this.getDevices().getDevice(index);
	}
	
	public boolean isInitialized() {
		return initialized;
	}

	private void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public ListOfDevices getDevices() throws ModelNotInitializedException {
		assertInitialized();
		
		return devices;
	}

	private void assertInitialized() throws ModelNotInitializedException {
		if (!this.isInitialized())
			throw new ModelNotInitializedException();
	}

	public void setDevices(ListOfDevices devices) {
		this.devices = devices;
		this.fireModelUpdatedEvent();
	}
}
