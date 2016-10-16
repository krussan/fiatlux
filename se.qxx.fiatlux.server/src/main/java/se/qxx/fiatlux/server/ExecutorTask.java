package se.qxx.fiatlux.server;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;

public class ExecutorTask extends Task{

	public enum ExecutorType {
		TURN_ON,
		TURN_OFF
	}
	
	private ExecutorType type;
	private ExecutorType getType() {
		return type;
	}
	
	private void setType(ExecutorType type) {
		this.type = type;
	}
	
	private int deviceId;
	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public ExecutorTask(ExecutorType type, int deviceId) {
		this.setType(type);
		this.setDeviceId(deviceId);
	}
	
	@Override
	public void execute(TaskExecutionContext context) throws RuntimeException {
		if (this.getType() == ExecutorType.TURN_ON)
			FiatLuxServer.getNative().tdTurnOn(this.getDeviceId());
		else
			FiatLuxServer.getNative().tdTurnOff(this.getDeviceId());
			
	}
 	
}
