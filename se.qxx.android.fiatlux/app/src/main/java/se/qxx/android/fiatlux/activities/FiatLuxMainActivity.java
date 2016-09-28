package se.qxx.android.fiatlux.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.protobuf.RpcCallback;

import java.util.EventObject;

import se.qxx.android.fiatlux.activities.DimmerActivity;
import se.qxx.android.fiatlux.OnOffHandler;
import se.qxx.android.fiatlux.activities.SettingsActivity;
import se.qxx.android.fiatlux.adapters.DeviceAdapter;
import se.qxx.android.fiatlux.adapters.DeviceLayoutAdapter;
import se.qxx.android.fiatlux.client.FiatluxConnectionHandler;
import se.qxx.android.fiatlux.model.ModelNotInitializedException;
import se.qxx.fiatlux.domain.FiatluxComm;
import se.qxx.fiatlux.domain.FiatluxComm.ListOfDevices;
import se.qxx.fiatlux.domain.FiatluxComm.Device;
import se.qxx.fiatlux.domain.FiatluxComm.DeviceType;
import se.qxx.android.fiatlux.Settings;
import se.qxx.android.fiatlux.model.Model;
import se.qxx.android.fiatlux.model.Model.ModelUpdatedEventListener;
import android.widget.AdapterView.OnItemClickListener;
import se.qxx.android.fiatlux.R;

public class FiatLuxMainActivity extends AppCompatActivity
        implements ModelUpdatedEventListener, OnItemClickListener {

    private DeviceLayoutAdapter deviceLayoutAdapter;

    private DeviceLayoutAdapter getDeviceLayoutAdapter() {
        return deviceLayoutAdapter;
    }

    private void setDeviceLayoutAdapter(DeviceLayoutAdapter deviceLayoutAdapter) {
        this.deviceLayoutAdapter = deviceLayoutAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Settings.init(this);

        Model.get().addEventListener(this);

        initModel();
        initListView();
    }

    private void initModel() {
        FiatluxConnectionHandler h = OnOffHandler.getHandler(this, "Getting devices...");

        h.listDevices(new RpcCallback<FiatluxComm.ListOfDevices>() {

            @Override
            public void run(ListOfDevices devices) {
                Model.get().setDevices(devices);
            }
        });

    }

    private void initListView() {
        ListView v = (ListView) findViewById(R.id.listMain);
        v.setOnItemClickListener(this);
        //v.setOnItemLongClickListener(this);


        this.setDeviceLayoutAdapter(new DeviceLayoutAdapter(this));
        v.setAdapter(this.getDeviceLayoutAdapter());

        //Model.get().addEventListener(this);
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
    public void handleModelUpdatedEventListener(EventObject e) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                deviceLayoutAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        Device d;
        try {
            d = Model.get().getDevice(pos);

            if (d.getType() == DeviceType.dimmer) {
                Intent i = new Intent(this, DimmerActivity.class);
                i.putExtra("DeviceID", pos);
                startActivity(i);
            }
            else {
                OnOffHandler.handleClick(this, pos);
            }
        } catch (ModelNotInitializedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
