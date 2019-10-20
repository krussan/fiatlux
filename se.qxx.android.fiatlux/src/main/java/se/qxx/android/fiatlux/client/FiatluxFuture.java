package se.qxx.android.fiatlux.client;

import com.google.common.util.concurrent.FutureCallback;
import com.google.protobuf.RpcCallback;
import io.grpc.stub.StreamObserver;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import se.qxx.android.tools.ResponseListener;
import se.qxx.android.tools.ResponseMessage;
import se.qxx.fiatlux.domain.FiatluxComm;

public class FiatluxFuture<T> implements FutureCallback<T> {
    private RpcCallback<T> callback;
    private ResponseListener listener;

    public RpcCallback<T> getCallback() {
        return callback;
    }

    public void setCallback(RpcCallback<T> callback) {
        this.callback = callback;
    }


    public ResponseListener getListener() {
        return listener;
    }

    public void setListener(ResponseListener listener) {
        this.listener = listener;
    }

    public FiatluxFuture(RpcCallback<T> callback) {
        this.setCallback(callback);
    }

    public FiatluxFuture(RpcCallback<T> callback, ResponseListener listener) {
        this.setCallback(callback);
        this.setListener(listener);
    }

    @Override
    public void onSuccess(@NullableDecl T result) {
        if (callback != null)
            callback.run(result);

        if (this.getListener() != null)
            this.getListener().onRequestComplete(new ResponseMessage(true, ""));

    }

    @Override
    public void onFailure(Throwable t) {
        if (this.getListener() != null)
            this.getListener().onRequestComplete(new ResponseMessage(false, t.getMessage()));
    }
}
