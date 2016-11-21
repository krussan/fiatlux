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
import android.widget.Switch;
import android.widget.ToggleButton;

public class DeviceLayoutAdapter extends DeviceAdapter implements CompoundButton.OnCheckedChangeListener {
	private Context context;
	public DeviceLayoutAdapter(Context context) {
		super();
		this.setContext(context);
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
	            v = vi.inflate(R.layout.item_device, null);
	        }
	        Device d = (Device)this.getItem(position);
	        
	        if (d != null) {
	        	GUITools.setTextOnTextview(R.id.lblDeviceName, d.getName(), v);

                Switch toggle = (Switch) v.findViewById(R.id.lblDeviceName);
                toggle.setChecked(d.getIsOn());

                DeviceToggleSwitchListener listener = new DeviceToggleSwitchListener(getContext(), d, position);
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
}
