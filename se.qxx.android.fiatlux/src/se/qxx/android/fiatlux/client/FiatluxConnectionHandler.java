package se.qxx.android.fiatlux.client;

import se.qxx.fiatlux.domain.FiatluxComm;
import se.qxx.fiatlux.domain.FiatluxComm.Empty;
import se.qxx.fiatlux.domain.FiatluxComm.FiatLuxService;
import se.qxx.fiatlux.domain.FiatluxComm.ListOfDevices;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.socketrpc.SocketRpcController;

public class FiatluxConnectionHandler {
	
	private FiatluxResponseListener listener;
	
	public FiatluxConnectionHandler(String serverIPaddress, int port) {
		FiatluxConnectionPool.setup(serverIPaddress, port);
	}
	
	public FiatluxConnectionHandler(String serverIPaddress, int port, FiatluxResponseListener listener) {
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

	public FiatluxResponseListener getListener() {
		return listener;
	}

	public void setListener(FiatluxResponseListener listener) {
		this.listener = listener;
	}	
		
	private FiatluxConnectionMessage checkResponse(RpcController controller) {
		return new FiatluxConnectionMessage(!controller.failed(), controller.errorText());		
	}
	
	private void onRequestComplete(RpcController controller) {
		FiatluxConnectionMessage msg = checkResponse(controller);
		
		if (this.getListener() != null)
			this.listener.onRequestComplete(msg);		
	}


}
