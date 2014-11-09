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
	
	public int countDevices() throws ModelNotInitializedException {
		assertInitialized(); 
		
		return this.getDevices().getDeviceCount();
	}
	
	public void setDevice(int index, Device d) throws ModelNotInitializedException {
		assertInitialized();
		
		ListOfDevices list = ListOfDevices.newBuilder(this.getDevices())
				.setDevice(index, d)
				.build();
		
		this.setDevices(list);
	}
	
	public void turnOn(int index) throws ModelNotInitializedException {
		Device d = this.getDevice(index);
		this.setDevice(index, Device.newBuilder(d).setIsOn(true).build());
	}
	
	public void turnOff(int index) throws ModelNotInitializedException {
		Device d = this.getDevice(index);
		this.setDevice(index, Device.newBuilder(d).setIsOn(false).build());
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
		
		if (this.devices != null)
			this.setInitialized(true);
		
		this.fireModelUpdatedEvent();
	}
}
