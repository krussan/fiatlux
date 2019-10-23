package se.qxx.android.fiatlux.client;

public interface RpcCallback<T> {
    void run(T value);
}
