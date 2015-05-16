package se.qxx.android.fiatlux;

import se.qxx.android.fiatlux.RoundKnobButton.RoundKnobButtonListener;
import se.qxx.android.fiatlux.client.FiatluxConnectionHandler;
import se.qxx.android.fiatlux.model.Model;
import se.qxx.android.fiatlux.model.ModelNotInitializedException;
import se.qxx.android.tools.ConnectionProgressDialog;
import se.qxx.fiatlux.domain.FiatluxComm.Device;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class DimmerActivity extends Activity {
	int m_deviceID = -1;
	long latestTriggerTimestamp = System.currentTimeMillis();
	
	public static final long TRIGGER_MILLIS = 800;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		Intent intent = getIntent();
		m_deviceID = intent.getIntExtra("DeviceID", -1);		
		
		RelativeLayout panel = new RelativeLayout(this);
        setContentView(panel);

//        final TextView tv2 = new TextView(this); tv2.setText("");
//        LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//  		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
//  		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//  		panel.addView(tv2, lp);

          		
        RoundKnobButton rv = new RoundKnobButton(this, R.drawable.stator, R.drawable.rotoron, R.drawable.rotoroff, 
        		Scale(250, this), Scale(250, this));
        LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		panel.addView(rv, lp);
        
        rv.setRotorPercentage(100);
        rv.SetListener(new RoundKnobButtonListener() {
			public void onStateChange(boolean newstate) {
				OnOffHandler.handleClick(DimmerActivity.this, m_deviceID);
			}
			
			public void onRotate(final int percentage) {
				try {
					long currentTimestamp = System.currentTimeMillis();
					
					if (currentTimestamp - latestTriggerTimestamp > TRIGGER_MILLIS) {
						Device dev = Model.get().getDevice(m_deviceID);
					
						OnOffHandler.getNonMessageHandler(DimmerActivity.this, "Dimming...").dim(dev, percentage);
						
						latestTriggerTimestamp = currentTimestamp;
        			}
					
					
				} catch (ModelNotInitializedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				tv2.post(new Runnable() {
//					public void run() {
//						tv2.setText("\n" + percentage + "%\n");
//					}
//				});
				
				
			}

			@Override
			public void onRelease(final int percentage) {
				try {
					Device dev = Model.get().getDevice(m_deviceID);
					
					OnOffHandler.getNonMessageHandler(DimmerActivity.this, "Dimming...").dim(dev, percentage);
				} catch (ModelNotInitializedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				tv2.post(new Runnable() {
//					public void run() {
//						tv2.setText("\n" + percentage + "%\n");
//					}
//				});
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
	
}
