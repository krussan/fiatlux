package se.qxx.fiatlux.server;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.sun.jna.Native;

import se.qxx.fiatlux.domain.FiatluxComm.Device;
import se.qxx.fiatlux.domain.FiatluxComm.Empty;
import se.qxx.fiatlux.domain.FiatluxComm.FiatLuxService;
import se.qxx.fiatlux.domain.FiatluxComm.ListOfDevices;
import se.qxx.fiatlux.domain.FiatluxComm.Success;

public class FiatLuxServerConnection extends FiatLuxService {
	static TellstickLibrary lib;
	
	@Override
	public void list(RpcController controller, Empty request,
			RpcCallback<ListOfDevices> done) {
		
		initLib();
		ListOfDevices.Builder list = ListOfDevices.newBuilder();
		
		System.out.println("LIST DEVICES");

		int nrOfDevices = lib.tdGetNumberOfDevices();
		for (int i=1;i<=nrOfDevices;i++) {
			String name = lib.tdGetName(i);
			int last_cmd = lib.tdLastSentCommand(i, TellstickLibrary.TELLSTICK_TURNON | TellstickLibrary.TELLSTICK_TURNOFF);
			
			list.addDevice(Device.newBuilder()
					.setDeviceID(i)
					.setName(name)
					.setIsOn(last_cmd == TellstickLibrary.TELLSTICK_TURNON)
					.build());
			
			
			
		}
		
		

		done.run(list.build());
	}
	
	private void initLib() {
		if (lib == null)
			lib = (TellstickLibrary)Native.loadLibrary("libtelldus-core.so.2", TellstickLibrary.class);		
	}

	@Override
	public void turnOn(RpcController controller, Device request,
			RpcCallback<Success> done) {
		initLib();
		
		int deviceID = request.getDeviceID();
		System.out.println(String.format("Turning on device %s", deviceID));
		lib.tdTurnOn(request.getDeviceID());
		
	}

	@Override
	public void turnOff(RpcController controller, Device request,
			RpcCallback<Success> done) {
		initLib();
		
		int deviceID = request.getDeviceID();
		System.out.println(String.format("Turning off device %s", deviceID));
		lib.tdTurnOff(request.getDeviceID());
		
	}
}