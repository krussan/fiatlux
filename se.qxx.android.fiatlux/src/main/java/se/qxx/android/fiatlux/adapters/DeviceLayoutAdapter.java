package se.qxx.android.fiatlux.adapters;

import se.qxx.android.fiatlux.OnOffHandler;
import se.qxx.android.fiatlux.R;
import se.qxx.android.fiatlux.activities.DimmerActivity;
import se.qxx.android.tools.GUITools;
import se.qxx.android.tools.Logger;
import se.qxx.fiatlux.domain.FiatluxComm;
import se.qxx.fiatlux.domain.FiatluxComm.Device;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DeviceLayoutAdapter extends DeviceAdapter implements CompoundButton.OnCheckedChangeListener {
	private Context context;
	private OnOffHandler handler;

	public DeviceLayoutAdapter(Context context, List<Device> devices, OnOffHandler handler) {
		super(devices);
		this.setContext(context);
		this.setHandler(handler);
	}
	
	private Context getContext() {
		return context;
	}
	
	private void setContext(Context context) {
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView; 
		
		try {
			
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.item_device, parent, false);
	        }
	        Device d = (Device)this.getItem(position);
	        
	        if (d != null) {
                Date dd = new Date(d.getNextScheduledTime());
                DateFormat df  = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                GUITools.setTextOnTextview(R.id.lblDeviceName, d.getName(), v);
                GUITools.setTextOnTextview(
					R.id.txtNextSchedulingTime,
					String.format("Turn %s at %s",d.getNextAction(), df.format(dd)),
					v);

                Switch toggle = v.findViewById(R.id.lblDeviceName);
                toggle.setChecked(d.getIsOn());

                DeviceToggleSwitchListener listener = new DeviceToggleSwitchListener(getContext(), d, this.getHandler());
                toggle.setOnClickListener(listener);

	    	}
		}
		catch (Exception e) {
			Logger.Log().e("Error occured while populating list", e);
		}
			
        return v;
	}


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    public OnOffHandler getHandler() {
        return handler;
    }

    public void setHandler(OnOffHandler handler) {
        this.handler = handler;
    }

    public void updateDevice(Device device) {
        for (int i = 0; i < this.getDevices().size(); i++) {
            if (this.getDevices().get(i).getDeviceID() == device.getDeviceID()) {
                this.getDevices().set(i, device);
                notifyDataSetChanged();
                break;
            }
        }
    }
}
