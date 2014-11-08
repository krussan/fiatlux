package se.qxx.android.fiatlux;

import java.util.EventObject;

import com.google.protobuf.RpcCallback;

import se.qxx.android.fiatlux.adapters.DeviceLayoutAdapter;
import se.qxx.android.fiatlux.client.FiatluxConnectionHandler;
import se.qxx.android.fiatlux.model.Model;
import se.qxx.android.fiatlux.model.Model.ModelUpdatedEventListener;
import se.qxx.android.tools.ProgressDialogHandler;
import se.qxx.fiatlux.domain.FiatluxComm;
import se.qxx.fiatlux.domain.FiatluxComm.ListOfDevices;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.os.Build;

public class FiatLuxMainActivity extends Activity 
	implements ModelUpdatedEventListener {
	private DeviceLayoutAdapter deviceLayoutAdapter;
	private FiatluxConnectionHandler handler;
	
	private DeviceLayoutAdapter getDeviceLayoutAdapter() {
		return deviceLayoutAdapter;
	}

	private void setDeviceLayoutAdapter(DeviceLayoutAdapter deviceLayoutAdapter) {
		this.deviceLayoutAdapter = deviceLayoutAdapter;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fiat_lux_main);
			
		initConnection();
		initModel();
		initListView();

	}

	private void initConnection() {
		handler = new FiatluxConnectionHandler("192.168.1.125", 2151);
	}

	private void initModel() {
		final ProgressDialog d = new ProgressDialog(this);
		final ProgressDialogHandler h = new ProgressDialogHandler(this,d);
		d.show();
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				handler.listDevices(new RpcCallback<FiatluxComm.ListOfDevices>() {
					
					@Override
					public void run(ListOfDevices devices) {
						Model.get().setDevices(devices);
					}
				});
				
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putBoolean("success", true);
				b.putString("message", "success");
				msg.setData(b);
				h.sendMessage(msg);
			}
		});
		t.run();
		
		
		
	}

	private void initListView() {
		ListView v = (ListView) findViewById(R.id.listMain);
		//v.setOnItemClickListener(this);
		//v.setOnItemLongClickListener(this);
		

		this.setDeviceLayoutAdapter(new DeviceLayoutAdapter(this));
		v.setAdapter(this.getDeviceLayoutAdapter());

		//Model.get().addEventListener(this);		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fiat_lux_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void handleModelUpdatedEventListener(EventObject e) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				deviceLayoutAdapter.notifyDataSetChanged();
			}
		});
		
	}

}
