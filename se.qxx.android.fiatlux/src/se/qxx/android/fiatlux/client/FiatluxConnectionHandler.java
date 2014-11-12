package se.qxx.android.fiatlux.client;

import se.qxx.android.tools.ResponseListener;
import se.qxx.android.tools.ResponseMessage;
import se.qxx.fiatlux.domain.FiatluxComm;
import se.qxx.fiatlux.domain.FiatluxComm.Device;
import se.qxx.fiatlux.domain.FiatluxComm.Empty;
import se.qxx.fiatlux.domain.FiatluxComm.FiatLuxService;
import se.qxx.fiatlux.domain.FiatluxComm.ListOfDevices;
import se.qxx.fiatlux.domain.FiatluxComm.Success;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.socketrpc.SocketRpcController;

public class FiatluxConnectionHandler {
	
	private ResponseListener listener;
	
	public FiatluxConnectionHandler(String serverIPaddress, int port) {
		FiatluxConnectionPool.setup(serverIPaddress, port);
	}
	
	public FiatluxConnectionHandler(String serverIPaddress, int port, ResponseListener listener) {
		FiatluxConnectionPool.setup(serverIPaddress, port);		
		this.setListener(listener);
	}
	
	//----------------------------------------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------- RPC Calls
	//----------------------------------------------------------------------------------------------------------------
	public void listDevices() {
		this.listDevices(null);
	}
			
	public void listDevices(final RpcCallback<FiatluxComm.ListOfDevices> rpcCallback) {
		final RpcController controller = new SocketRpcController();

		Thread t = new Thread() {
			public void run() {
				FiatLuxService service = FiatluxConnectionPool.get().getNonBlockingService();
				
				Empty request = Empty.newBuilder().build();
				
				service.list(controller, request, new RpcCallback<FiatluxComm.ListOfDevices>() {
					
					@Override
					public void run(ListOfDevices devices) {
						onRequestComplete(controller);
						if (rpcCallback != null) 				
							rpcCallback.run(devices);							
					}
				});
			
			}
		};
		t.start();
	}
	
	public void turnOn(final Device d) {
		turnOn(d, null);
	}
	
	public void turnOn(final Device d, final RpcCallback<FiatluxComm.Success> rpcCallback) {
		final RpcController controller = new SocketRpcController();

		Thread t = new Thread() {
			public void run() {
				FiatLuxService service = FiatluxConnectionPool.get().getNonBlockingService();
				
				service.turnOn(controller, d, new RpcCallback<FiatluxComm.Success>() {
					
					@Override
					public void run(Success s) {
						onRequestComplete(controller);
						if (rpcCallback != null) 				
							rpcCallback.run(s);							
					}
				});
			
			}
		};
		t.start();
	}	
	
	public void turnOff(final Device d) {
		turnOff(d, null);
	}
	
	public void turnOff(final Device d, final RpcCallback<FiatluxComm.Success> rpcCallback) {
		final RpcController controller = new SocketRpcController();

		Thread t = new Thread() {
			public void run() {
				FiatLuxService service = FiatluxConnectionPool.get().getNonBlockingService();
				
				service.turnOff(controller, d, new RpcCallback<FiatluxComm.Success>() {
					
					@Override
					public void run(Success s) {
						onRequestComplete(controller);
						if (rpcCallback != null) 				
							rpcCallback.run(s);							
					}
				});
			
			}
		};
		t.start();
	}		

	public ResponseListener getListener() {
		return listener;
	}

	public void setListener(ResponseListener listener) {
		this.listener = listener;
	}	
		
	private ResponseMessage checkResponse(RpcController controller) {
		return new ResponseMessage(!controller.failed(), controller.errorText());		
	}
	
	private void onRequestComplete(RpcController controller) {
		ResponseMessage msg = checkResponse(controller);
		
		if (this.getListener() != null)
			this.listener.onRequestComplete(msg);		
	}


}
