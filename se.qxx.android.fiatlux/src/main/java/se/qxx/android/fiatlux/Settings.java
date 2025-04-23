package se.qxx.android.fiatlux;

import se.qxx.android.tools.SettingsBase;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings extends SettingsBase {
	private static Settings _instance = null;
	private SharedPreferences preferences;
	private SharedPreferences storage;
	
	private final String SERVER_IP_ADDRESS = "serverIpAddress";
	private final String SERVER_PORT = "serverPort";
	
	public String getServerIpAddress() {
		return preferences.getString(SERVER_IP_ADDRESS, "127.0.0.1");
	}

	public void setServerIpAddress(String _serverIpAddress) {
		this.putString(preferences, SERVER_IP_ADDRESS, _serverIpAddress);
	}

	public int getServerPort() {
		try {
			return Integer.parseInt(preferences.getString(SERVER_PORT, "2150"));
		} catch(NumberFormatException ex) {
			return 2150;
		}
	}

	public void setServerPort(int _serverPort) {
		this.putInt(preferences, SERVER_PORT, _serverPort);
	}

	private Settings(Context c) {
		preferences = PreferenceManager.getDefaultSharedPreferences(c);
		storage = c.getSharedPreferences("fiatluxstorage", 0);
	}
			
	public static Settings init(Context c) {
		if (_instance == null)
			_instance = new Settings(c);
		
		return _instance;
	}
	
	public static Settings get(){
		return _instance;
	}
			
}