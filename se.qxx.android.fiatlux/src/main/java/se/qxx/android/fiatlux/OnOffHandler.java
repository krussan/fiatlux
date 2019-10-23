package se.qxx.android.fiatlux;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import se.qxx.android.fiatlux.client.FiatluxConnectionHandler;
import se.qxx.android.tools.ConnectionProgressDialog;
import se.qxx.fiatlux.domain.FiatluxComm;

public class OnOffHandler {

    private DeviceUpdatedListener listener;
    private Activity activity;

    public OnOffHandler(Activity activity, DeviceUpdatedListener listener) {
        this.setActivity(activity);
        this.setListener(listener);
    }

    public void handleClick(FiatluxComm.Device device) {

        if (!isConnected()) {
            this.getActivity().runOnUiThread(() -> {
                Toast.makeText(getActivity(), R.string.noWifiConnection, Toast.LENGTH_SHORT);
            });
            return;
        }

        if (device != null) {
            if (device.getIsOn()) {
                turnOff(device);
                device = FiatluxComm.Device.newBuilder(device).setIsOn(false).build();
                if (listener != null)
                    listener.dataChanged(device);
            } else {
                turnOn(device);
                device = FiatluxComm.Device.newBuilder(device).setIsOn(true).build();
                if (listener != null)
                    listener.dataChanged(device);
            }
        }

    }


    private void turnOn(FiatluxComm.Device d) {
        getHandler("Turning on device...").turnOn(d);
    }

    private void turnOff(FiatluxComm.Device d) {
        getHandler("Turning off device...").turnOff(d);
    }


    public FiatluxConnectionHandler getHandler(String message) {
        return new FiatluxConnectionHandler(
                Settings.get().getServerIpAddress(),
                Settings.get().getServerPort(),
                ConnectionProgressDialog.build(this.getActivity(), message));
    }

    public FiatluxConnectionHandler getNonMessageHandler() {
        return new FiatluxConnectionHandler(
                Settings.get().getServerIpAddress(),
                Settings.get().getServerPort());
    }

    public DeviceUpdatedListener getListener() {
        return listener;
    }

    public void setListener(DeviceUpdatedListener listener) {
        this.listener = listener;
    }


    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting() &&
                (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET);

    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
