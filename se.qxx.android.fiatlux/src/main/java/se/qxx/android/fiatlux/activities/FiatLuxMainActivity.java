package se.qxx.android.fiatlux.activities;

import android.Manifest;
import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import se.qxx.android.fiatlux.DeviceUpdatedListener;
import se.qxx.android.fiatlux.OnOffHandler;
import se.qxx.android.fiatlux.adapters.DeviceLayoutAdapter;
import se.qxx.android.fiatlux.client.FiatluxConnectionHandler;
import se.qxx.fiatlux.domain.FiatluxComm.Device;
import se.qxx.android.fiatlux.Settings;
import android.widget.Toast;

import se.qxx.android.fiatlux.R;

public class FiatLuxMainActivity extends AppCompatActivity
        implements DeviceUpdatedListener, SwipeRefreshLayout.OnRefreshListener {

    private BroadcastReceiver receiver;
    private DeviceLayoutAdapter deviceLayoutAdapter;
    private SwipeRefreshLayout swipeLayout;

    public OnOffHandler getHandler() {
        return handler;
    }

    public void setHandler(OnOffHandler handler) {
        this.handler = handler;
    }

    private OnOffHandler handler;

    private final ActivityResultLauncher<String> localNetworkPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            initializeApp();
                        } else {
                            Toast.makeText(this,
                                    "Local network permission is required",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        swipeLayout = findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setProgressViewOffset(false, 0, 100);

        checkLocalNetworkPermission();

    }

    private void checkLocalNetworkPermission() {

        // Permission is only required when running Android 17
        // with target SDK 37 or higher.
        if (Build.VERSION.SDK_INT >= 37 &&
                getApplicationInfo().targetSdkVersion >= 37) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_LOCAL_NETWORK)
                    != PackageManager.PERMISSION_GRANTED) {

                localNetworkPermissionLauncher.launch(
                        Manifest.permission.ACCESS_LOCAL_NETWORK);
                return;
            }
        }

        initializeApp();
    }

    private void initializeApp() {
        Settings.init(this);
        this.setHandler(new OnOffHandler(this, this));
        registerReceiver();
    }

    @Override
    public void onRefresh() {
        initModel();
    }

    private void initModel() {
        swipeLayout.setRefreshing(true);
        FiatluxConnectionHandler h = this.getHandler().getNonMessageHandler();

        h.listDevices(devices -> runOnUiThread(() -> {
            //swipeLayout = findViewById(R.id.swipe_container);
            if (devices != null) {
                ListView v = findViewById(R.id.listMain);
                deviceLayoutAdapter = new DeviceLayoutAdapter(
                        FiatLuxMainActivity.this,
                        devices.getDeviceList(),
                        getHandler());
                v.setAdapter(deviceLayoutAdapter);
            }
            swipeLayout.setRefreshing(false);
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
                Toast.makeText(this, R.string.noWifiConnection, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }



    private void showPreferences() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    @Override
    public void dataChanged(final Device device) {
        runOnUiThread(() -> deviceLayoutAdapter.updateDevice(device));
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
