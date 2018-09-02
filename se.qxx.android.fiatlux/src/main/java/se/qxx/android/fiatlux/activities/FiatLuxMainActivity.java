package se.qxx.android.fiatlux.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.protobuf.RpcCallback;

import java.util.EventObject;

import se.qxx.android.fiatlux.DeviceUpdatedListener;
import se.qxx.android.fiatlux.activities.DimmerActivity;
import se.qxx.android.fiatlux.OnOffHandler;
import se.qxx.android.fiatlux.adapters.DeviceLayoutAdapter;
import se.qxx.android.fiatlux.client.FiatluxConnectionHandler;
import se.qxx.fiatlux.domain.FiatluxComm;
import se.qxx.fiatlux.domain.FiatluxComm.ListOfDevices;
import se.qxx.fiatlux.domain.FiatluxComm.Device;
import se.qxx.fiatlux.domain.FiatluxComm.DeviceType;
import se.qxx.android.fiatlux.Settings;
import android.widget.AdapterView.OnItemClickListener;
import se.qxx.android.fiatlux.R;

public class FiatLuxMainActivity extends AppCompatActivity
        implements OnItemClickListener, DeviceUpdatedListener {

    private DeviceLayoutAdapter deviceLayoutAdapter;

    public OnOffHandler getHandler() {
        return handler;
    }

    public void setHandler(OnOffHandler handler) {
        this.handler = handler;
    }

    private OnOffHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Settings.init(this);
        this.setHandler(new OnOffHandler(this));


        initModel();
        initListView();

    }

    private void initModel() {
        FiatluxConnectionHandler h = this.getHandler().getHandler(this, "Getting devices...");

        getApplicationContext();
        h.listDevices(devices -> runOnUiThread(() -> {
            ListView v = findViewById(R.id.listMain);
            deviceLayoutAdapter = new DeviceLayoutAdapter(
                    FiatLuxMainActivity.this,
                    devices.getDeviceList(),
                    getHandler());
            v.setAdapter(deviceLayoutAdapter);
        }));
    }

    private void initListView() {
        ListView v = findViewById(R.id.listMain);
        v.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fiatlux_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            showPreferences();
            return true;
        }

        if (id == R.id.action_refresh) {
            initModel();
        }
        return super.onOptionsItemSelected(item);
    }



    private void showPreferences() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {

        if (deviceLayoutAdapter != null) {
            Device d = (Device) deviceLayoutAdapter.getItem(pos);

            if (d != null) {
                if (d.getType() == DeviceType.dimmer) {
                    Intent i = new Intent(this, DimmerActivity.class);
                    i.putExtra("Device", d);
                    startActivity(i);
                } else {
                    this.getHandler().handleClick(this, d);
                }
            }
        }
    }

    @Override
    public void dataChanged(final Device device) {
        runOnUiThread(() -> {
            deviceLayoutAdapter.updateDevice(device);
        });
    }
}
