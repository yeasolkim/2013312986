package swssm.fg.bi_box;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import swssm.fg.bi_box.model.Geometry;
import swssm.fg.bi_box.model.GeometryDeserializer;
import swssm.fg.bi_box.model.NaviDataModel;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RoadGuideStartService extends Service {

	static private String jsonString;
	private String actionName;
	private Bundle getDataBundle;
	private double currentLatitude, currentLongitude;

	private GsonBuilder gsonBuilder;
	private Gson gsonObject;
//	private static int serviceCallCount = 2;
	
	NaviDataModel naviModel;
	Location tempLocation, currentLocation;
	static Location beforeLocation = new Location("before");
	ArrayList<Double> pointTypeLongitudeArray = new ArrayList<Double>();
	ArrayList<Double> pointTypeLatitudeeArray = new ArrayList<Double>();
	ArrayList<Location> pointTypeLocationArray = new ArrayList<Location>();
	ArrayList<Integer> pointTypeIndexArray = new ArrayList<Integer>();
	
	public static int pointTypeIndexArrayIndex = 1;
	public static boolean onlyFirstFlag = true;
	
	public static Location myLoc;
	public static boolean mutex = true;
	private static int toleranceValue = 7;
	public static int accrueError = 0;
	public static float totalErrorDistance = 0;
	public static boolean pathErrorFlag = false;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i("test", "RoadGuideStartService_onCreate");
		gsonBuilder= new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Geometry.class, new GeometryDeserializer());
		gsonObject = gsonBuilder.create();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		Log.i("test", "RoadGuideStartService_onStartCommand");

		actionName = intent.getAction();
		if (actionName.equals("swssm.fg.bi_box.roadguidestart"))
		{
			getDataBundle = intent.getExtras();
			jsonString = getDataBundle.getString("jsonString");
			currentLatitude = getDataBundle.getDouble("currentLatitude");
			currentLongitude = getDataBundle.getDouble("currentLongitude");


			Log.i("test", " 현재위치 : " + currentLatitude + " ////////// " + currentLongitude);
			
//			NavigationActivity.longInfo(jsonString);	//jSon print
			
		
			naviModel = gsonObject.fromJson(jsonString, NaviDataModel.class);
			
			Log.i("test",naviModel.getFeatures().size() + "  Features 사이즈#######");
			
			
			for(int i=0;i<naviModel.getFeatures().size();i++)
			{
				if(naviModel.getFeatures().get(i).getGeometry().getType().equals("Point"))
				{
					double tempLatitude, tempLongitude;
					Log.i("test_Point",naviModel.getFeatures().get(i).getGeometry().getCoordinates().size() + "  ########Point");
					tempLongitude = Double.parseDouble(naviModel.getFeatures().get(i).getGeometry().getCoordinates().get(0));
					tempLatitude = Double.parseDouble(naviModel.getFeatures().get(i).getGeometry().getCoordinates().get(1));
					pointTypeLongitudeArray.add(tempLongitude);
					pointTypeLatitudeeArray.add(tempLatitude);
					pointTypeIndexArray.add(i);
					
					tempLocation = new Location("temp");
					tempLocation.setLatitude(tempLatitude);
					tempLocation.setLongitude(tempLongitude);
					
					pointTypeLocationArray.add(tempLocation);
					
				}
			}
			currentLocation = new Location("currnet");
			currentLocation.setLatitude(currentLatitude);
			currentLocation.setLongitude(currentLongitude);
			
			int minIndex = 0;
			float distance, minDistance = 999999;
			for(int i = pointTypeIndexArrayIndex; i < pointTypeIndexArray.size(); i++)
			{
				Log.i("test","Point Spot :  " + i + ". " + naviModel.getFeatures().get(pointTypeIndexArray.get(i)).getProperties().getDescription());
				Location tempLocation = new Location("temp");
				tempLocation.setLongitude(Double.parseDouble(naviModel.getFeatures().get(pointTypeIndexArray.get(i)).getGeometry().getCoordinates().get(0)));
				tempLocation.setLatitude(Double.parseDouble(naviModel.getFeatures().get(pointTypeIndexArray.get(i)).getGeometry().getCoordinates().get(1)));
				
				distance = currentLocation.distanceTo(tempLocation);
				if(distance < minDistance)
				{
					Log.i("test",i+"###");
					minDistance = distance;
					minIndex = i;
				}
			}
			pointTypeIndexArrayIndex = minIndex;
			myLoc = pointTypeLocationArray.get(pointTypeIndexArrayIndex);
			
			
//			beforeLocation = new Location("before");
			
			
			Log.i("test","현재위치 -> 다음Point 각도 ::: " + currentLocation.bearingTo(pointTypeLocationArray.get(pointTypeIndexArrayIndex)));
			if(!onlyFirstFlag)
			{
				Log.i("test","전위치 -> 현재위치 각도::: " + beforeLocation.bearingTo(currentLocation));
				
				if(gapOfTwoPoint(currentLocation.bearingTo(pointTypeLocationArray.get(pointTypeIndexArrayIndex)), beforeLocation.bearingTo(currentLocation)))
				{
					Log.i("test","옳은 방향으로 가고 있습니다.");
					accrueError = 0;
					totalErrorDistance = 0;
				}
				else
				{
					Log.i("test","경로를 이탈했습니다.");
					accrueError++;
					totalErrorDistance = totalErrorDistance + beforeLocation.distanceTo(currentLocation);
					Log.i("test","##########################   " + accrueError + " #############   "+ totalErrorDistance);
					if(accrueError > 2 && totalErrorDistance > 20)
					{
						//경로를 이탈했습니다...
						Toast.makeText(getApplicationContext(), "It is a re-search in the path.", Toast.LENGTH_SHORT).show();
						accrueError = 0;
						totalErrorDistance = 0;
						pathErrorFlag = true;
						
						try{
							if (Global.uHandler != null)
							{
								Log.i("test", "RESEARCH:: 데이터 기어에게 보낸다.");
								Global.uHandler.send(Global.channelID, new String("RESEARCH::").getBytes());
								try{
									Thread.sleep(5000);
								}catch(Exception e)
								{
									Log.i("test",e.toString());
								}
							}
								
							stopSelf();
						}catch(IOException e)
						{
							Log.i("test",e.toString());
						}
						
					}
				}
			}
			
			
			
			beforeLocation = currentLocation;
			
			Log.i("test","Point Spot :  " + pointTypeIndexArrayIndex + ". "+naviModel.getFeatures().get(pointTypeIndexArray.get(pointTypeIndexArrayIndex)).getProperties().getDescription());
			
			boolean isNextBoolean = isNextLocation(currentLocation);
			
			if(isNextBoolean && mutex)
			{
				mutex = false;
				Log.i("test","================isNextLocation(currentLocation) == true==================");
				//내위치는 포인트에 있다.
//				if(onlyFirstFlag)
//				{
//					onlyFirstFlag = false;
//				}else
//				{
//					if(pointTypeLocationArray.size() - 1 > pointTypeIndexArrayIndex)
//						pointTypeIndexArrayIndex++;
////					onlyFirstFlag = true;
//				}
//				Log.i("test","massage Send___Point Type");
//				Log.i("test","dir::"+naviModel.getFeatures().get(pointTypeIndexArray.get(pointTypeIndexArrayIndex)).getProperties().getTurnType()+" // "+naviModel.getFeatures().get(pointTypeIndexArray.get(pointTypeIndexArrayIndex)).getProperties().getDescription());
//				Log.i("test","dis::" + currentLocation.distanceTo(pointTypeLocationArray.get(pointTypeIndexArrayIndex)));
//				
//				myLoc = pointTypeLocationArray.get(pointTypeIndexArrayIndex);
//				pathErrorFlag = false;
				
				if (Global.uHandler != null) {
					try {
						
						if(onlyFirstFlag)
						{
							onlyFirstFlag = false;
						}else
						{
							if(pointTypeLocationArray.size() - 1 > pointTypeIndexArrayIndex)
								pointTypeIndexArrayIndex++;
//							onlyFirstFlag = true;
						}
						
//						distanceTo 사용시....
						Log.i("test","massage Send___Point Type");
						Log.i("test","dir::"+naviModel.getFeatures().get(pointTypeIndexArray.get(pointTypeIndexArrayIndex)).getProperties().getTurnType()+"//"+naviModel.getFeatures().get(pointTypeIndexArray.get(pointTypeIndexArrayIndex)).getProperties().getDescription());
						Log.i("test","dis::" + currentLocation.distanceTo(pointTypeLocationArray.get(pointTypeIndexArrayIndex)));
						
						Global.uHandler.send(Global.channelID, new String("dir::"+naviModel.getFeatures().get(pointTypeIndexArray.get(pointTypeIndexArrayIndex)).getProperties().getTurnType()+"//"+naviModel.getFeatures().get(pointTypeIndexArray.get(pointTypeIndexArrayIndex)).getProperties().getDescription()).getBytes());
						Global.uHandler.send(Global.channelID, new String("dis::" + currentLocation.distanceTo(pointTypeLocationArray.get(pointTypeIndexArrayIndex))).getBytes());
						
						myLoc = pointTypeLocationArray.get(pointTypeIndexArrayIndex);
						pathErrorFlag = false;
						
					} catch (IOException e) {
						Log.i("test", e.toString());
					}
				}
				mutex = true;
			}
			else if(!isNextBoolean && mutex)
			{
				Log.i("test","================isNextLocation(currentLocation) == false=======================");
				//내위치는 라인스트링한에 있다.
//				Log.i("test","massage Send___Point Type");
//				Log.i("test","dir::" + naviModel.getFeatures().get(pointTypeIndexArray.get(pointTypeIndexArrayIndex)).getProperties().getTurnType()+" // "+naviModel.getFeatures().get(pointTypeIndexArray.get(pointTypeIndexArrayIndex)).getProperties().getDescription());
//				Log.i("test","dis::" + currentLocation.distanceTo(pointTypeLocationArray.get(pointTypeIndexArrayIndex)));
				
				if (Global.uHandler != null) {
					try {
					
//						distanceTo 사용시....
						Log.i("test","massage Send___Point Type");
						Log.i("test","dir::"+naviModel.getFeatures().get(pointTypeIndexArray.get(pointTypeIndexArrayIndex)).getProperties().getTurnType()+"//"+naviModel.getFeatures().get(pointTypeIndexArray.get(pointTypeIndexArrayIndex)).getProperties().getDescription());
						Log.i("test","dis::" + currentLocation.distanceTo(pointTypeLocationArray.get(pointTypeIndexArrayIndex)));
						
						if(pathErrorFlag)
						{
							Global.uHandler.send(Global.channelID, new String("dir::"+naviModel.getFeatures().get(pointTypeIndexArray.get(pointTypeIndexArrayIndex)).getProperties().getTurnType()+"//"+naviModel.getFeatures().get(pointTypeIndexArray.get(pointTypeIndexArrayIndex)).getProperties().getDescription()).getBytes());
							pathErrorFlag = false;
						}
						Global.uHandler.send(Global.channelID, new String("dis::" + currentLocation.distanceTo(pointTypeLocationArray.get(pointTypeIndexArrayIndex))).getBytes());
					
					
					} catch (IOException e) {
						Log.i("test", e.toString());
					}
				}
			}
			
			
			
			
		}

		
//		send(dir::turn type//description)
//		send(dis::몇미터??)
		
		
		stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}



	public boolean isNextLocation(Location newLoc) {

		if (onlyFirstFlag) 
		{
			myLoc = pointTypeLocationArray.get(pointTypeIndexArrayIndex);
//			pointTypeIndexArrayIndex++;
			Log.i("test","처음 들어온거라서 TRUE 반환");
			return true;
		} 
		else if (newLoc.getAccuracy() > 5)
		{
			Log.i("test","정확도가 낮아서 FALSE반환");
			return false;
		}
		
		Log.i("test","포인트 좌표..." + myLoc.getLatitude() + " /// " + myLoc.getLongitude());
		double distBetTwo = myLoc.distanceTo(newLoc);
		Log.i("test","distBetTwo_____" + distBetTwo);
		if (distBetTwo < toleranceValue) {
			//다음 포인트 값을 myLoc에 넣어줘야함....add code
			myLoc = pointTypeLocationArray.get(pointTypeIndexArrayIndex);
//			pointTypeIndexArrayIndex++;

			Log.i("test","포인트와의 오차범위 7m 안이라서 TRUE 반환");
			return true;
		} else {
			Log.i("test","포인트와의 범위가 7m 밖이라 FALSE 반환");
			return false;
		}
	};
	
	boolean gapOfTwoPoint(float onePoint, float twoPoint)
	{
		if(Math.abs(onePoint-twoPoint) < 50)
		{
			return true;
		}
		return false;
	}
	

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		

		Log.i("test", "RoadGuideStartService_onDestroy");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("test", "onBind");
		return null;
	}
	public static void init()
	{
		mutex = true;
		myLoc = null;
		onlyFirstFlag = true;
		accrueError = 0;
		beforeLocation = null;
		totalErrorDistance = 0;
		pathErrorFlag = false;
		pointTypeIndexArrayIndex = 1;
	}

}
