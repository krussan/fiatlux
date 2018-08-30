package se.qxx.fiatlux.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.sun.jna.Native;

import se.qxx.fiatlux.domain.FiatluxComm;
import se.qxx.fiatlux.domain.FiatluxComm.Device;
import se.qxx.fiatlux.domain.FiatluxComm.DeviceType;
import se.qxx.fiatlux.domain.FiatluxComm.DimCommand;
import se.qxx.fiatlux.domain.FiatluxComm.Empty;
import se.qxx.fiatlux.domain.FiatluxComm.FiatLuxService;
import se.qxx.fiatlux.domain.FiatluxComm.ListOfDevices;
import se.qxx.fiatlux.domain.FiatluxComm.Success;

public class FiatLuxServerConnection extends FiatLuxService {
	
    private static final Logger logger = LogManager.getLogger(FiatLuxServerConnection.class);

	@Override
	public void list(RpcController controller, Empty request,
			RpcCallback<ListOfDevices> done) {
		
		ListOfDevices.Builder list = ListOfDevices.newBuilder();
		
		
		logger.info("LIST DEVICES");

		int nrOfDevices = FiatLuxServer.getNative().tdGetNumberOfDevices();
		for (int i=1;i<=nrOfDevices;i++) {
			String name = FiatLuxServer.getNative().tdGetName(i);
			int last_cmd = FiatLuxServer.getNative().tdLastSentCommand(i, TellstickLibrary.TELLSTICK_TURNON | TellstickLibrary.TELLSTICK_TURNOFF);
			
			int supportedMethods = TellstickLibrary.TELLSTICK_TURNOFF | TellstickLibrary.TELLSTICK_TURNON | TellstickLibrary.TELLSTICK_DIM;
			int methods = FiatLuxServer.getNative().tdMethods(i, supportedMethods);
			
			DeviceType dt = ((methods & TellstickLibrary.TELLSTICK_DIM) == TellstickLibrary.TELLSTICK_DIM) ? DeviceType.dimmer : DeviceType.onoffswitch;

			ExecutorTask task = FiatLuxServer.getScheduler().getLowestExecutor(i);

			Device.Builder builder = Device.newBuilder()
                    .setDeviceID(i)
                    .setName(name)
                    .setIsOn(last_cmd == TellstickLibrary.TELLSTICK_TURNON)
                    .setType(dt);

			if (task != null) {
                builder
                    .setNextScheduledTime(task.getNextSchedulingTime())
                    .setNextAction(task.getAction());
            }


			list.addDevice(builder.build());
			
			
			
		}
		
		

		done.run(list.build());
	}
	

	@Override
	public void turnOn(RpcController controller, Device request,
			RpcCallback<Success> done) {
		
		int deviceID = request.getDeviceID();
		logger.info(String.format("Turning on device %s", deviceID));
		FiatLuxServer.getNative().tdTurnOn(deviceID);
		
		done.run(Success.newBuilder().setSuccess(true).build());
	}

	@Override
	public void turnOff(RpcController controller, Device request,
			RpcCallback<Success> done) {
		
		int deviceID = request.getDeviceID();
		logger.info(String.format("Turning off device %s", deviceID));
		FiatLuxServer.getNative().tdTurnOff(deviceID);
		
		done.run(Success.newBuilder().setSuccess(true).build());
		
	}

	@Override
	public void dim(RpcController controller, DimCommand request,
			RpcCallback<Success> done) {
		
		int deviceID = request.getDevice().getDeviceID();
		int percentage = request.getDimPercentage();
		int level = 255 * percentage / 100;
		
		logger.info(String.format("Dimming device %s to %s", deviceID, percentage));
		FiatLuxServer.getNative().tdDim(deviceID, level);
		
		done.run(Success.newBuilder().setSuccess(true).build());

	}
}
