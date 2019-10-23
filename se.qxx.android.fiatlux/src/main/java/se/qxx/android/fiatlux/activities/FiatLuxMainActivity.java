package se.qxx.android.fiatlux.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import se.qxx.android.fiatlux.DeviceUpdatedListener;
import se.qxx.android.fiatlux.OnOffHandler;
import se.qxx.android.fiatlux.adapters.DeviceLayoutAdapter;
import se.qxx.android.fiatlux.client.FiatluxConnectionHandler;
import se.qxx.fiatlux.domain.FiatluxComm.Device;
import se.qxx.android.fiatlux.Settings;
import android.widget.Toast;

import se.qxx.android.fiatlux.R;

public class FiatLuxMainActivity extends AppCompatActivity
        implements DeviceUpdatedListener {

    private BroadcastReceiver receiver;
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
        this.setHandler(new OnOffHandler(this, this));

        registerReceiver();

    }

    private void initModel() {
        FiatluxConnectionHandler h = this.getHandler().getHandler("Getting devices...");

        getApplicationContext();
        h.listDevices(devices -> runOnUiThread(() -> {
            if (devices != null) {
                ListView v = findViewById(R.id.listMain);
                deviceLayoutAdapter = new DeviceLayoutAdapter(
                        FiatLuxMainActivity.this,
                        devices.getDeviceList(),
                        getHandler());
                v.setAdapter(deviceLayoutAdapter);
            }
        }));
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
            if (getHandler().isConnected())
                initModel();
            else
                Toast.makeText(this, R.string.noWifiConnection, Toast.LENGTH_SHORT);
        }
        return super.onOptionsItemSelected(item);
    }



    private void showPreferences() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    @Override
    public void dataChanged(final Device device) {
        runOnUiThread(() -> {
            deviceLayoutAdapter.updateDevice(device);
        });
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getHandler().isConnected())
                    initModel();
            }
        };
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }
}
