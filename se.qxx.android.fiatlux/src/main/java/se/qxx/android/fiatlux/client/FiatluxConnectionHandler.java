package se.qxx.android.fiatlux.client;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.RpcCallback;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import se.qxx.android.tools.ResponseListener;
import se.qxx.fiatlux.domain.FiatluxComm;
import se.qxx.fiatlux.domain.FiatluxComm.Device;
import se.qxx.fiatlux.domain.FiatluxComm.DimCommand;
import se.qxx.fiatlux.domain.FiatluxComm.Empty;
import se.qxx.fiatlux.domain.FiatluxComm.ListOfDevices;

import static se.qxx.fiatlux.domain.FiatLuxServiceGrpc.FiatLuxServiceFutureStub;
import static se.qxx.fiatlux.domain.FiatLuxServiceGrpc.newFutureStub;

public class FiatluxConnectionHandler {
	
	private ResponseListener listener;
	FiatLuxServiceFutureStub stub = null;

	public FiatluxConnectionHandler(String serverIPaddress, int port) {
		ManagedChannel channel = ManagedChannelBuilder
				.forAddress(serverIPaddress, port)
				.build();

		stub = newFutureStub(channel);
	}
	
	public FiatluxConnectionHandler(String serverIPaddress, int port, ResponseListener listener) {
		this(serverIPaddress, port);
		this.setListener(listener);
	}
	
	//----------------------------------------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------- RPC Calls
	//----------------------------------------------------------------------------------------------------------------
	public void listDevices() {
		this.listDevices(null);
	}
			
	public void listDevices(final RpcCallback<ListOfDevices> rpcCallback) {
		Empty request = Empty.newBuilder().build();
		FiatluxFuture observer = new FiatluxFuture(rpcCallback, this.getListener());

		ListenableFuture future = stub.list(request);
		Futures.addCallback(future, observer, MoreExecutors.directExecutor());
	}
	
	public void turnOn(final Device d) {
		turnOn(d, null);
	}
	
	public void turnOn(final Device d, final RpcCallback<FiatluxComm.Success> rpcCallback) {
		FiatluxFuture observer = new FiatluxFuture(rpcCallback, this.getListener());

		ListenableFuture future = stub.turnOn(d);
		Futures.addCallback(future, observer, MoreExecutors.directExecutor());
	}
	
	public void turnOff(final Device d) {
		turnOff(d, null);
	}
	
	public void turnOff(final Device d, final RpcCallback<FiatluxComm.Success> rpcCallback) {
		FiatluxFuture observer = new FiatluxFuture(rpcCallback, this.getListener());

		ListenableFuture future = stub.turnOff(d);
		Futures.addCallback(future, observer, MoreExecutors.directExecutor());
	}
	
	public void dim(final Device d, int percentage) {
		dim(d, percentage, null);
	}
	
	public void dim(final Device d, final int percentage, final RpcCallback<FiatluxComm.Success> rpcCallback) {

		DimCommand comm =
				DimCommand.newBuilder()
				.setDevice(d)
				.setDimPercentage(percentage)
				.build();

		FiatluxFuture observer = new FiatluxFuture(rpcCallback, this.getListener());

		ListenableFuture future = stub.dim(comm);
		Futures.addCallback(future, observer, MoreExecutors.directExecutor());
	}

	public ResponseListener getListener() {
		return this.listener;
	}

	public void setListener(ResponseListener listener) {
		this.listener = listener;
	}	
		


}
