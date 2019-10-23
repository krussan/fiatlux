package se.qxx.fiatlux.server;

import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import se.qxx.fiatlux.domain.FiatLuxServiceGrpc.FiatLuxServiceImplBase;
import se.qxx.fiatlux.domain.FiatluxComm.Device;
import se.qxx.fiatlux.domain.FiatluxComm.DeviceType;
import se.qxx.fiatlux.domain.FiatluxComm.DimCommand;
import se.qxx.fiatlux.domain.FiatluxComm.ListOfDevices;
import se.qxx.fiatlux.domain.FiatluxComm.Success;

public class FiatLuxServerConnection extends FiatLuxServiceImplBase {
	
    private static final Logger logger = LogManager.getLogger(FiatLuxServerConnection.class);

    @Override
    public void list(se.qxx.fiatlux.domain.FiatluxComm.Empty request,
         StreamObserver<ListOfDevices> responseObserver) {

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

        responseObserver.onNext(list.build());
		responseObserver.onCompleted();
    }



	@Override
	public void turnOn(Device request,
			StreamObserver<Success> responeObserver) {

		int deviceID = request.getDeviceID();
		logger.info(String.format("Turning on device %s", deviceID));
		FiatLuxServer.getNative().tdTurnOn(deviceID);

		responeObserver.onNext(Success.newBuilder().setSuccess(true).build());
		responeObserver.onCompleted();
	}

	@Override
	public void turnOff(Device request,
			StreamObserver<Success> responeObserver) {

		int deviceID = request.getDeviceID();
		logger.info(String.format("Turning off device %s", deviceID));
		FiatLuxServer.getNative().tdTurnOff(deviceID);

		responeObserver.onNext(Success.newBuilder().setSuccess(true).build());
		responeObserver.onCompleted();

	}

	@Override
	public void dim(DimCommand request,
			StreamObserver<Success> responeObserver) {

		int deviceID = request.getDevice().getDeviceID();
		int percentage = request.getDimPercentage();
		int level = 255 * percentage / 100;

		logger.info(String.format("Dimming device %s to %s", deviceID, percentage));
		FiatLuxServer.getNative().tdDim(deviceID, level);

		responeObserver.onNext(Success.newBuilder().setSuccess(true).build());
        responeObserver.onCompleted();
	}
}
