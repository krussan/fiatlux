package se.qxx.android.fiatlux;

import android.app.Activity;

import se.qxx.android.fiatlux.client.FiatluxConnectionHandler;
import se.qxx.android.tools.ConnectionProgressDialog;
import se.qxx.fiatlux.domain.FiatluxComm;

public class OnOffHandler {

    private DeviceUpdatedListener listener;

    public OnOffHandler(DeviceUpdatedListener listener) {
        this.setListener(listener);
    }

    public void handleClick(Activity activity, FiatluxComm.Device device) {

        if (device != null) {
            if (device.getIsOn()) {
                turnOff(activity, device);
                device = FiatluxComm.Device.newBuilder(device).setIsOn(false).build();
                if (listener != null)
                    listener.dataChanged();
            } else {
                turnOn(activity, device);
                device = FiatluxComm.Device.newBuilder(device).setIsOn(true).build();
                if (listener != null)
                    listener.dataChanged();
            }
        }

    }


    private void turnOn(Activity activity, FiatluxComm.Device d) {
        getHandler(activity, "Turning on device...").turnOn(d);
    }

    private void turnOff(Activity activity, FiatluxComm.Device d) {
        getHandler(activity, "Turning off device...").turnOff(d);
    }


    public FiatluxConnectionHandler getHandler(Activity activity, String message) {
        return new FiatluxConnectionHandler(
                Settings.get().getServerIpAddress(),
                Settings.get().getServerPort(),
                ConnectionProgressDialog.build(activity, message));
    }

    public FiatluxConnectionHandler getNonMessageHandler(Activity activity, String message) {
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

}
