
package swssm.fg.bi_box;

import java.io.IOException;

import swssm.fg.bi_box.model.NaviDataModel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RoadGuideBroadcast extends BroadcastReceiver{


	static String jsonString;
	String actionName;
	Bundle getDataBundle, setDataBundle;
	Intent startServiceIntent;
	static double currentLatitude, currentLongitude;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		actionName = intent.getAction();
		
		if(actionName.equals("swssm.fg.bi_box.roadguideaction"))
		{
			getDataBundle = intent.getExtras();
			currentLatitude = getDataBundle.getDouble("currentLatitude");
			currentLongitude = getDataBundle.getDouble("currentLongitude");

			
			
			
			Log.i("test","swssm.fg.bi_box.roadguideaction");
			
			if(jsonString != null)
			{
				Log.i("test","jsonString.isEmpty()");
				startServiceIntent = new Intent("swssm.fg.bi_box.roadguidestart");
				setDataBundle = new Bundle();
				setDataBundle.putString("jsonString", jsonString);
				setDataBundle.putDouble("currentLatitude", currentLatitude);
				setDataBundle.putDouble("currentLongitude", currentLongitude);
				startServiceIntent.putExtras(setDataBundle);	
			
				context.startService(startServiceIntent);
			}
			
			
		}
		else if(actionName.equals("swssm.fg.bi_box.jsonstring"))
		{
			getDataBundle = intent.getExtras();
			jsonString = getDataBundle.getString("jsonString");
			Log.i("test","swssm.fg.bi_box.jsonstring");
			
			startServiceIntent = new Intent("swssm.fg.bi_box.roadguidestart");
			setDataBundle = new Bundle();
			setDataBundle.putString("jsonString", jsonString);
			setDataBundle.putDouble("currentLatitude", currentLatitude);
			setDataBundle.putDouble("currentLongitude", currentLongitude);
			startServiceIntent.putExtras(setDataBundle);	
		
			context.startService(startServiceIntent);
			
		}
	}



}
