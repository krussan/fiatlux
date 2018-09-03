package se.qxx.android.fiatlux.activities;

import se.qxx.android.fiatlux.DeviceUpdatedListener;
import se.qxx.android.fiatlux.OnOffHandler;
import se.qxx.android.fiatlux.R;
import se.qxx.android.fiatlux.RoundKnobButton;
import se.qxx.android.fiatlux.RoundKnobButton.RoundKnobButtonListener;
import se.qxx.fiatlux.domain.FiatluxComm;
import se.qxx.fiatlux.domain.FiatluxComm.Device;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class DimmerActivity extends AppCompatActivity implements DeviceUpdatedListener {
    Device device;
	long latestTriggerTimestamp = System.currentTimeMillis();
	
	public static final long TRIGGER_MILLIS = 800;

	private OnOffHandler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setHandler(new OnOffHandler(this, this));

		RelativeLayout panel = new RelativeLayout(this);
        setContentView(panel);

        this.setDevice((Device)getIntent()
            .getSerializableExtra("Device"));

        RoundKnobButton rv = new RoundKnobButton(this, R.drawable.stator, R.drawable.rotoron, R.drawable.rotoroff,
        		Scale(250, this), Scale(250, this));
        LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		panel.addView(rv, lp);
        
        rv.setRotorPercentage(100);
        rv.SetListener(new RoundKnobButtonListener() {
			public void onStateChange(boolean newstate) {
				getHandler().handleClick(getDevice());
			}
			
			public void onRotate(final int percentage) {
				long currentTimestamp = System.currentTimeMillis();

				if (currentTimestamp - latestTriggerTimestamp > TRIGGER_MILLIS) {
					handler.getNonMessageHandler(
							DimmerActivity.this,
							"Dimming...")
								.dim(
									getDevice(),
										percentage);

					latestTriggerTimestamp = currentTimestamp;
				}

			}

			@Override
			public void onRelease(final int percentage) {
				Device dev = getDevice();

				if (dev != null)
					getHandler().getNonMessageHandler(
						DimmerActivity.this,
						"Dimming...")
							.dim(dev, percentage);

			}
		});

	}
	
	public int Scale(int v, Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		float fFrameS = (float)dm.widthPixels / 640.0f;
		Log.d("DimmerActivity", String.format("width :: %s", dm.widthPixels));
		Log.d("DimmerActivity", String.format("fFrameS :: %s", fFrameS));
				
		float s = (float)v * fFrameS; 
		
		int rs = 0;
		if (s - (int)s >= 0.5) rs= ((int)s)+1; else rs= (int)s;
		
		return rs;
	}


    public OnOffHandler getHandler() {
        return handler;
    }

    public void setHandler(OnOffHandler handler) {
        this.handler = handler;
    }

    @Override
    public void dataChanged(Device device) {
	    this.setDevice(device);
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

}
