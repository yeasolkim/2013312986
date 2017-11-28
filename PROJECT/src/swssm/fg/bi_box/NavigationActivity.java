package swssm.fg.bi_box;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapData.TMapPathType;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

public class NavigationActivity extends Activity implements onLocationChangedCallback {

	RelativeLayout relativeLayout;
	TMapView tmapView;
	TMapData tmapData;
	TMapGpsManager tmapGpsManager;
	double latitudeData, longitudeData;
	int zoomLevel = 17;
	Button pathSearchBtn, guideStartBtn, guideStopBtn;
	Button lastPathBtn;
	String searchResultName;
	String searchResultTmapPoint;
	boolean GuideButtonFlag = false;
	
	String startName, destinationName;
	double startLatitude, startLongitude, destinationLatitude, destinationLongitude;
	
	ArrayAdapter<String> listAdapter;
	View toolbar;

	TMapPoint startTmapPoint, destinationTmapPoint;
	static String jsonString;
	Intent roadGuideIntent;
	private boolean mIsBackKeyPressed = false;
	private long mCurrentTimeInMillis = 0;
	private static final int MSG_TIMER_EXPIRED = 1;
	private static final int BACKEY_TIMEOUT = 2000;
	
	private long minTime = 1000; //1 seconds
	private long minDistance = 5; //5 meters
	
	private static TMapPathType tmapPathType = TMapPathType.BICYCLE_PATH;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_navigation);
		overridePendingTransition(0, 0);
		relativeLayout = new RelativeLayout(this);
		relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
		guideStartBtn = (Button) findViewById(R.id.guidestartButton);
		guideStopBtn = (Button) findViewById(R.id.guidestopButton);

		tmapView = new TMapView(this);
		tmapData = new TMapData();
		
		relativeLayout.addView(tmapView);

		tmapGpsManager = new TMapGpsManager(this);
		tmapGpsManager.setMinTime(minTime);
		tmapGpsManager.setMinDistance(minDistance);
//		tmapGpsManager.setProvider(tmapGpsManager.NETWORK_PROVIDER);
		tmapGpsManager.setProvider(tmapGpsManager.GPS_PROVIDER);
		tmapGpsManager.OpenGps();

		createThreadAndDialog(1); 
		Tab3.titleText.setText("navigation");
		guideStartBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(GuideButtonFlag == true)
				{
					GuideButtonFlag = false;
					guideStartBtn.setActivated(true);
					guideStopBtn.setActivated(false);
					guideStartBtn.setClickable(true);
					guideStopBtn.setClickable(false);
					//guideStartBtn.setText("안내시작");
					roadGuideIntent = new Intent("swssm.fg.bi_box.roadguidestart");
					stopService(roadGuideIntent);
					
					
					
				}
				else
				{
					//스타트 눌렀을때...
					RoadGuideStartService.init();
					
					GuideButtonFlag = true;
					//guideStartBtn.setText("안내중...");
					guideStartBtn.setActivated(false);
					guideStopBtn.setActivated(true);

					guideStartBtn.setClickable(false);
					guideStopBtn.setClickable(true); 
					roadGuideIntent = new Intent("swssm.fg.bi_box.jsonstring");
					Bundle jsonDataBundle = new Bundle();
					jsonDataBundle.putString("jsonString", jsonString);
					roadGuideIntent.putExtras(jsonDataBundle);
					
					sendBroadcast(roadGuideIntent);
					
					
					
				}
				
			}
		});

		guideStopBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(GuideButtonFlag == true)
				{
					//스탑 눌렀을때...
					GuideButtonFlag = false;
					guideStartBtn.setActivated(true);
					guideStopBtn.setActivated(false);
					guideStartBtn.setClickable(true);
					guideStopBtn.setClickable(false);

					//guideStartBtn.setText("안내시작");
					roadGuideIntent = new Intent("swssm.fg.bi_box.roadguidestart");
					stopService(roadGuideIntent);
					
					RoadGuideStartService.init();
					
				}
				else
				{
					GuideButtonFlag = true;
					//guideStartBtn.setText("안내중...");
					guideStartBtn.setActivated(false);
					guideStopBtn.setActivated(true);
					guideStartBtn.setClickable(false);
					guideStopBtn.setClickable(true); 
					roadGuideIntent = new Intent("swssm.fg.bi_box.jsonstring");
					Bundle jsonDataBundle = new Bundle();
					jsonDataBundle.putString("jsonString", jsonString);
					roadGuideIntent.putExtras(jsonDataBundle);
					
					sendBroadcast(roadGuideIntent);
					
					
					
				}
				
			}
		});
		new Thread() {
			@Override
			public void run()
			{
				tmapView.setSKPMapApiKey(Global.APP_KEY);
				tmapView.setLanguage(tmapView.LANGUAGE_KOREAN);
				tmapView.setBicycleInfo(true);
//				tmapview.setBicycleFacilityInfo(true);
			}
		}.start();
		BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.pin);
		Bitmap pinbitmap = drawable.getBitmap();
		
		tmapView.setIconVisibility(true);
		tmapView.setIcon(pinbitmap);
		tmapView.setZoomLevel(zoomLevel);
		tmapView.setMapType(tmapView.MAPTYPE_STANDARD);
		tmapView.setTrackingMode(true);
		
	}


//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		Tab3.mTab.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
//	    TextView title = (TextView)findViewById(R.id.titleTextView);
//	   // if(title.equals("title"))
//	    //	title.setText("navigation");
//	} 
	
	private ProgressDialog loadingDialog; // 로딩화면
    void createThreadAndDialog(int i) {
         
        /* ProgressDialog */
    	loadingDialog = new ProgressDialog(NavigationActivity.this);
    	loadingDialog.setMessage("Getting Your Position");
    	loadingDialog.setCancelable(false);
    	loadingDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		    }
		});
    	loadingDialog.show();

    }

	public void menuClickHandler (View v) {
	    String text = null;
	    
	    

		if(v.getId() == R.id.item1)
		{
			 TMapPoint point = tmapGpsManager.getLocation();
				text = "위도 : " + point.getLatitude() + "//경도 : "+point.getLongitude();
				tmapView.setLocationPoint(point.getLongitude(),point.getLatitude());
				tmapView.setCenterPoint(point.getLongitude(),point.getLatitude(),true);
		        //break;
			
			
		}
		else if(v.getId() == R.id.item2)
		{
			text = "zoom_IN";
	        zoomLevel = tmapView.getZoomLevel() + 1;
	        tmapView.setZoomLevel(zoomLevel);
	        //break;
		}
		else if(v.getId() == R.id.item3)
		{
			text = "zoom_OUT";
	        zoomLevel = tmapView.getZoomLevel() - 1;
	        tmapView.setZoomLevel(zoomLevel);
	        //break;
		}
		 
	}
	

	@Override
	public void onLocationChange(Location location) {
		// TODO Auto-generated method stub
		Log.i("test","Location___Change");
		Log.i("testX@@", ""+location.getLatitude());
		Log.i("testY@@", ""+location.getLongitude());
		latitudeData = location.getLatitude();
		longitudeData = location.getLongitude();
		tmapView.setLocationPoint(longitudeData, latitudeData);
		tmapView.setCenterPoint(longitudeData, latitudeData, true);
		loadingDialog.dismiss();
		roadGuideIntent = new Intent("swssm.fg.bi_box.roadguideaction");
		Bundle currLocation = new Bundle();
		currLocation.putDouble("currentLongitude", longitudeData);
		currLocation.putDouble("currentLatitude", latitudeData);
		roadGuideIntent.putExtras(currLocation);
		
		sendBroadcast(roadGuideIntent);

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		
		
		switch(requestCode)
		{
		case Global.PATH_SEARCH_ACTIVITY:
			if(resultCode == RESULT_OK)
			{
				startName = data.getExtras().getString("startName");
				startLatitude = data.getExtras().getDouble("startLat");
				startLongitude = data.getExtras().getDouble("startLon");
				destinationName = data.getExtras().getString("destName");
				destinationLatitude = data.getExtras().getDouble("destLat");
				destinationLongitude = data.getExtras().getDouble("destLon");
				
				startTmapPoint = new TMapPoint(startLatitude, startLongitude);
				destinationTmapPoint = new TMapPoint(destinationLatitude, destinationLongitude);
				
				Log.i("test","########## " + requestCode + "#### " + resultCode + "#### " + data);
				
				tmapData.findPathDataWithType(tmapPathType, startTmapPoint, destinationTmapPoint, new TMapData.FindPathDataListenerCallback() {
					
					@Override
					public void onFindPathData(TMapPolyLine polyLine) {
						// TODO Auto-generated method stub
						final View guidebar = (View)findViewById(R.id.guidebar);
						runOnUiThread(new Runnable() {
							public void run() {
								Animation popup = AnimationUtils.loadAnimation(NavigationActivity.this, R.anim.in);
								guideStartBtn.setVisibility(Button.VISIBLE);
								guideStartBtn.startAnimation(popup);
								guideStopBtn.setVisibility(Button.VISIBLE);
								guideStopBtn.startAnimation(popup);
								guidebar.startAnimation(popup);
								//GuideButtonFlag = false;
								guideStartBtn.setActivated(true);
								guideStopBtn.setActivated(false);
								guideStartBtn.setClickable(true);
								guideStopBtn.setClickable(false);
							}
						});
						tmapView.removeTMapPolyLine("POLYLINE");
						tmapView.addTMapPolyLine("POLYLINE", polyLine);
						tmapView.setLocationPoint(startLongitude, startLatitude);
						
						HttpPostData();
						
					
						
					}
				});
				
				
				

			}
		}
	}


	public String HttpPostData() {
		
		try {
			// --------------------------
			// URL 설정하고 접속하기
			// --------------------------
			URL url = new URL(Global.POST_METHOD); // URL 설정
			HttpURLConnection http = (HttpURLConnection) url.openConnection(); // 접속
			// --------------------------
			// 전송 모드 설정 - 기본적인 설정이다
			// --------------------------
			http.setDefaultUseCaches(false);
			http.setDoInput(true); // 서버에서 읽기 모드 지정
			http.setDoOutput(true); // 서버로 쓰기 모드 지정
			http.setRequestMethod("POST"); // 전송 방식은 POST
			// 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
			http.setRequestProperty("x-skpop-userId", Global.TMAP_USER_ID);
			http.setRequestProperty("Accept-Language", Global.POST_LANGUAGE);
			http.setRequestProperty("Date", new Date(System.currentTimeMillis()).toString());
			http.setRequestProperty("Content-Type", Global.CONTENT_TYPE);
			http.setRequestProperty("Accept", Global.ACCEPT_TYPE);
			http.setRequestProperty("access_token", "");
			http.setRequestProperty("appKey", Global.APP_KEY);
			// --------------------------
			// 서버로 값 전송
			// --------------------------

			StringBuffer buffer = new StringBuffer();

			// parameter 추가
			buffer.append("startName").append("=").append(startName).append("&");
			buffer.append("endName").append("=").append(destinationName).append("&");
			buffer.append("reqCoordType").append("=").append("WGS84GEO").append("&");
			buffer.append("resCoordType").append("=").append("WGS84GEO").append("&");
			buffer.append("endX").append("=").append(destinationLongitude).append("&"); // php 변수에 값 대입
			buffer.append("endY").append("=").append(destinationLatitude).append("&"); // php 변수 앞에 '$' 붙이지 않는다
			buffer.append("startX").append("=").append(startLongitude).append("&"); // 변수 구분은 '&' 사용
			buffer.append("startY").append("=").append(startLatitude);
//			buffer.append("searchOption").append("=").append(1);

//			Log.i("test", buffer.toString());
			
			OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "utf-8");
			PrintWriter writer = new PrintWriter(outStream);
			writer.write(buffer.toString());
			writer.flush();
			// --------------------------
			// 서버에서 전송받기
			// --------------------------
			
			InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "utf-8");
			BufferedReader reader = new BufferedReader(tmp);
			StringBuilder builder = new StringBuilder();
			String str;
			while ((str = reader.readLine()) != null) { // 서버에서 라인단위로 보내줄 것이므로  라인단위로 읽는다
				builder.append(str + "\n"); // View에 표시하기 위해 라인 구분자 추가
			}
			jsonString = builder.toString(); // 전송결과를 전역 변수에 저장

			longInfo(jsonString);

		} catch (MalformedURLException e) {
			Log.i("test", "catch11111");
		} catch (IOException e) {
			Log.i("test", e.toString());
		} // try
		
		return jsonString;
	} // HttpPostData
	
	//LogCat 출력
	public static void longInfo(String str) {
	    if(str.length() > 4000) {
	        Log.i("test",str.substring(0, 4000));
	        longInfo(str.substring(4000));
	    } else
	        Log.i("test",str);
	}
	
	public void myClickHandler(View v) 
    {

		if(v.getId() == R.id.doSomething1)
		{
			startLatitude = latitudeData;
			startLongitude = longitudeData;
			Intent pathSearchIntent = new Intent(NavigationActivity.this, PathSearchActivity.class);
			Bundle currentLocation = new Bundle();
			currentLocation.putDouble("currentLatitude", startLatitude);
			currentLocation.putDouble("currentLongitude", startLongitude);
			pathSearchIntent.putExtras(currentLocation);
			startActivityForResult(pathSearchIntent, Global.PATH_SEARCH_ACTIVITY);
			overridePendingTransition(R.anim.in, android.R.anim.fade_out);
			
			
		}
	
    }
	@Override
	public void onBackPressed() 
	{
		if (mIsBackKeyPressed == false)
		{
			mIsBackKeyPressed = true;
			mCurrentTimeInMillis = Calendar.getInstance().getTimeInMillis();
			Toast.makeText(this, "Press the back again,\n     the app quits.", Toast.LENGTH_SHORT).show();
			startTimer();
		} 
		else 
		{
			mIsBackKeyPressed = false;
			if (Calendar.getInstance().getTimeInMillis() <= (mCurrentTimeInMillis + (BACKEY_TIMEOUT)))
			{
				finish();
			}
		}
	}

	private void startTimer() 
	{
		mTimerHander.sendEmptyMessageDelayed(MSG_TIMER_EXPIRED, BACKEY_TIMEOUT);
	}

	private Handler mTimerHander = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			case MSG_TIMER_EXPIRED:
			{
				mIsBackKeyPressed = false;
			}
				break;
			}
		}
	};
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.i("test","NavigationActivity_onDestroy()");
		tmapGpsManager.CloseGps();
		RoadGuideStartService.init();
	}

	
}


