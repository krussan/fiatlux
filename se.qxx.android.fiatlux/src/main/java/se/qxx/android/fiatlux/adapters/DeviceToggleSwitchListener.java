package se.qxx.android.fiatlux.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;

import se.qxx.android.fiatlux.OnOffHandler;
import se.qxx.android.fiatlux.activities.DimmerActivity;
import se.qxx.fiatlux.domain.FiatluxComm;

/**
 * Created by chris on 9/23/16.
 */
public class DeviceToggleSwitchListener implements View.OnClickListener
{
    public FiatluxComm.Device getDevice() {
        return device;
    }

    public void setDevice(FiatluxComm.Device device) {
        this.device = device;
    }

    private FiatluxComm.Device device;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

    private OnOffHandler onOffHandler;


    public DeviceToggleSwitchListener(Context context, FiatluxComm.Device device, OnOffHandler handler) {
        this.setDevice(device);
        this.setContext(context);
        this.setOnOffHandler(handler);
    }

    @Override
    public void onClick(View v) {
        if (this.getDevice().getType() == FiatluxComm.DeviceType.dimmer) {
            Intent i = new Intent(getContext(), DimmerActivity.class);
            i.putExtra("Device", this.getDevice());
            getContext().startActivity(i);
        }
        else {
            this.getOnOffHandler().handleClick(this.getDevice());
        }
    }

    public OnOffHandler getOnOffHandler() {
        return onOffHandler;
    }

    public void setOnOffHandler(OnOffHandler onOffHandler) {
        this.onOffHandler = onOffHandler;
    }
}
