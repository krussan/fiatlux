package se.qxx.android.fiatlux.adapters;

import se.qxx.android.fiatlux.model.Model;
import se.qxx.android.fiatlux.model.ModelNotInitializedException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class DeviceAdapter extends BaseAdapter {

		public DeviceAdapter() {
			super();
		}
		
		@Override
		public int getCount() {
			try {
				return Model.get().countDevices();
			} catch (ModelNotInitializedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		}
		
		@Override
		public Object getItem(int position) {
			
			try {
				return Model.get().getDevice(position);
			} catch (ModelNotInitializedException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
		
		@Override
		public long getItemId(int position) {
			
			return position;
		}
		
		@Override
		public abstract View getView(int position, View convertView, ViewGroup parent);
}
