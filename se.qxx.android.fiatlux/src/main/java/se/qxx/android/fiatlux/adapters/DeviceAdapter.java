package se.qxx.android.fiatlux.adapters;

import se.qxx.fiatlux.domain.FiatluxComm;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class DeviceAdapter extends BaseAdapter {

	private List<FiatluxComm.Device> devices = new ArrayList<FiatluxComm.Device>();

    public List<FiatluxComm.Device> getDevices() {
        return devices;
    }

    public void setDevices(List<FiatluxComm.Device> devices) {
        this.devices = devices;
    }

    public DeviceAdapter(List<FiatluxComm.Device> devices) {
        super();

        this.setDevices(devices);
    }

    @Override
    public int getCount() {
        return this.getDevices().size();
    }

    @Override
    public Object getItem(int position) {
        return this.getDevices().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

}
