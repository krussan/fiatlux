package se.qxx.android.fiatlux;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

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

    Override
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
