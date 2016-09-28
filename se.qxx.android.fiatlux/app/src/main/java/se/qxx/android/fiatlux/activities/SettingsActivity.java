package se.qxx.android.fiatlux.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	  // TODO Auto-generated method stub
	  super.onCreate(savedInstanceState);
	  
	  getFragmentManager().beginTransaction()
  		.replace(android.R.id.content, new SettingsFragment())
  		.commit();		
	}

}