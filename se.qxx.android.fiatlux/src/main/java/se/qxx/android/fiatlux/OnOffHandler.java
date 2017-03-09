package se.qxx.android.fiatlux;

import se.qxx.android.fiatlux.client.FiatluxConnectionHandler;
import se.qxx.android.fiatlux.model.Model;
import se.qxx.android.fiatlux.model.ModelNotInitializedException;
import se.qxx.android.tools.ConnectionProgressDialog;
import se.qxx.fiatlux.domain.FiatluxComm.Device;
import android.app.Activity;

public class OnOffHandler {
	
	public static void handleClick(Activity activity, int position) {
		Device d;
		try {
			d = Model.get().getDevice(position);
			
			if (d.getIsOn()) {
				turnOff(activity, d);
				Model.get().turnOff(position);
			}
			else {
				turnOn(activity, d);
				Model.get().turnOn(position);
			}
		} catch (ModelNotInitializedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private static void turnOn(final Activity activity, final Device d) {
		getHandler(activity, "Turning on device...").turnOn(d);
	}
	
	private static void turnOff(final Activity activity, final Device d) {
		getHandler(activity, "Turning off device...").turnOff(d);
	}
	

	public static FiatluxConnectionHandler getHandler(Activity activity, String message) {
		return new FiatluxConnectionHandler(
					Settings.get().getServerIpAddress(),
					Settings.get().getServerPort(),
					ConnectionProgressDialog.build(activity, message));
	}
	
	public static FiatluxConnectionHandler getNonMessageHandler(Activity activity, String message) {
		return new FiatluxConnectionHandler(
					Settings.get().getServerIpAddress(),
					Settings.get().getServerPort());
	}
}
