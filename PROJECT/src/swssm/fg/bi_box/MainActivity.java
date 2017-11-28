package swssm.fg.bi_box;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import swssm.fg.bi_box.FTSampleProviderImpl.FileAction;
import swssm.fg.bi_box.FTSampleProviderImpl.LocalBinder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity {

	public static boolean isUp = false;
	private int fileNameCount = 0;
	private boolean sendBroadcastFlag = false;

	private ImageButton normalButton;
	private ImageButton eventButton;
	private Button navigationButton;

	private static final int MSG_TIMER_EXPIRED = 1;
	private static final int BACKEY_TIMEOUT = 2000;
	private boolean mIsBackKeyPressed = false;
	private long mCurrentTimeInMillis = 0;

	private Context mContext;
	private FTSampleProviderImpl mFTService;
	public int mTransId;
	int receiveCount = 0;
	private int twelveMinCut = 0;
	LocationManager locationManager;
	private int GearConnectFlag = 0;
	ViewFlipper viewFlipper;
	// private int checkboxflag=0;
	SharedPreferences sp;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		overridePendingTransition(0, 0);
		String context = Context.LOCATION_SERVICE;

		viewFlipper = (ViewFlipper) findViewById(R.id.GuideView);
		// CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox);
		sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
		editor = sp.edit();
		int myIntValue = sp.getInt("init", 0);
		Log.i("test", "visited!!!!!!!!!" + myIntValue);
		if (myIntValue != 1) {
			Intent guideintent = new Intent(MainActivity.this, GuideActivity.class);
			startActivity(guideintent);
			// finish();
		}

		//
		// 占쏙옙占� 占쏙옙
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File biboxDir = new File(Global.dirPath);
			if (!biboxDir.exists()) {
				biboxDir.mkdir();
				Log.i("test", "BI-Box 폴더생성");
			}
			biboxDir = new File(Global.dirPath + "/Event");
			if (!biboxDir.exists()) {
				biboxDir.mkdir();
				Log.i("test", "BI-Box/Event 폴더생성");
			}
			biboxDir = new File(Global.dirPath + "/Ordinary");
			if (!biboxDir.exists()) {
				biboxDir.mkdir();
				Log.i("test", "BI-Box/Ordinary 폴더생성");
			}
			biboxDir = new File(Global.dirPath + "/History");
			if (!biboxDir.exists()) {
				biboxDir.mkdir();
				Log.i("test", "BI-Box/History 폴더생성");
			}
			biboxDir = new File(Global.dirPath + "/temp");
			if (!biboxDir.exists()) {
				biboxDir.mkdir();
				Log.i("test", "BI-Box/temp 폴더생성");
			}

		} else {
		
		}
		// -----占쏙옙占쏙옙占� END----
		mContext = getApplicationContext();
		mContext.bindService(new Intent(getApplicationContext(),FTSampleProviderImpl.class), this.mFTConnection, Context.BIND_AUTO_CREATE);
		Intent i = getIntent();
		if (i.getAction().equalsIgnoreCase("incomingFT")) {
			getFileAction().onTransferRequested(i.getIntExtra("tx", -1), i.getStringExtra("fileName"));
		}
		locationManager = (LocationManager) getSystemService(context);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			alertCheckGPS();
		}
		// /////////////////////////////////////////////////////////////////////////////////////
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String path = Global.dirPath + "/Ordinary";
			File file = new File(path);
			String str;

			if (file.listFiles().length > 0) {
				for (File f : file.listFiles()) {
					str = f.getName();
					OrdinaryListActivity.videoFileList.add(path + "/" + str);
					String input = str.split("_")[0];
				}
			}

			for (File f : file.listFiles()) {
				str = f.getName();
				OrdinaryListActivity.videoFileList.add(path + "/" + str);
				String cmp = str.split("_")[0];

			}

		}

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String path = Global.dirPath + "/Ordinary";
			File file = new File(path);
			String str;
			OrdinaryListActivity.videoFileList = new ArrayList<String>();
			Bitmap myBitmapa;
			if (file.listFiles().length > 0) {
				for (File f : file.listFiles()) {
					str = f.getName();
					OrdinaryListActivity.videoFileList.add(path + "/" + str);
					myBitmapa = ThumbnailUtils.createVideoThumbnail(path + "/" + str, Thumbnails.MINI_KIND);
					OrdinaryListActivity.map.put(str, OrdinaryListActivity.bitmapArray.size());
					OrdinaryListActivity.bitmapArray.add(myBitmapa);
				}
			} else {
				Log.i("test", "there is no file");
			}
		}

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String path = Global.dirPath + "/Event";
			File file = new File(path);
			String str;

			if (file.listFiles().length > 0) {
				for (File f : file.listFiles()) {
					str = f.getName();
					EventListActivity.videoFileList.add(path + "/" + str);
					String input = str.split("_")[0];
				}
			}

			for (File f : file.listFiles()) {
				str = f.getName();
				EventListActivity.videoFileList.add(path + "/" + str);
				String cmp = str.split("_")[0];

			}

		}

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String path = Global.dirPath + "/Event";
			File file = new File(path);
			String str;
			EventListActivity.videoFileList = new ArrayList<String>();
			Bitmap myBitmapa;
			if (file.listFiles().length > 0) {
				for (File f : file.listFiles()) {
					str = f.getName();
					EventListActivity.videoFileList.add(path + "/" + str);
					myBitmapa = ThumbnailUtils.createVideoThumbnail(path + "/" + str, Thumbnails.MINI_KIND);
					EventListActivity.map.put(str, EventListActivity.bitmapArray.size());
					EventListActivity.bitmapArray.add(myBitmapa);
				}
			} else {
				Log.i("test", "there is no file");
			}

		}
		// ///////////////////////////////////////////////////////////////////////////////////////////
	}

	public void viewClickListner(View v) {
		// TODO Auto-generated method stub
		viewFlipper.setInAnimation(this, android.R.anim.fade_in);
		viewFlipper.setOutAnimation(this, android.R.anim.fade_out);
		viewFlipper.showNext();
	}

	private void alertCheckGPS() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your GPS is disabled! Would you like to enable it?")
				.setCancelable(false)
				.setPositiveButton("Enable GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								moveConfigGPS();
							}
						})
				.setNegativeButton("Do nothing",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// GPS 설정화면으로 이동
	private void moveConfigGPS() {
		Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}

	public void StartOnClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == R.id.mainBackLayout) {
			if (GearConnectFlag == 1) {
				Intent eventintent = new Intent(MainActivity.this, Tab3.class);
				startActivity(eventintent);
				finish();
			} else if (GearConnectFlag == 0) {
				Toast.makeText(mContext, "Please connect Gear2", Toast.LENGTH_SHORT).show();
			}

		}
	}

	private ServiceConnection mFTConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.d("test", "FT service connection lost(연결끊김)");
			GearConnectFlag = 0;
			mFTService = null;
			FileTransferComplete();
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder service) {
			Log.d("test", "FT service connected");
			GearConnectFlag = 1;
			mFTService = ((LocalBinder) service).getService();
			mFTService.registerFileAction(getFileAction());
		}
	};

	private FileAction getFileAction() {
		return new FileAction() {
			@Override
			public void onError(final String errorMsg, final int errorCode) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
		
					}
				});
			}

			@Override
			public void onProgress(final long progress) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// mRecvProgressBar.setProgress((int) progress);
						Log.i("test", progress + "% 받는중");
					}
				});
			}

			@Override
			public void onTransferComplete(String path) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						if (sendBroadcastFlag == false)
							sendBroadcastFlag = true;
						else {
							Intent fileTransferCompleteIntent = new Intent("swssm.fg.bi_box.filetransfercomplete");
							Bundle fileCountBundle = new Bundle();
							fileCountBundle.putInt("fileCount", fileNameCount);
							fileTransferCompleteIntent.putExtras(fileCountBundle);
							sendBroadcast(fileTransferCompleteIntent);

						}
						Log.i("test", "@@@@@onTransferComplete___"	+ fileNameCount);
						// broadcast sender
						fileNameCount++;
						receiveCount++;
						twelveMinCut++;

						Log.i("test", "receiveCount : " + receiveCount	+ "//////" + "형이보낸 카운터 : "	+ FTSampleProviderImpl.receiveCount);

						if ((receiveCount == FTSampleProviderImpl.receiveCount || twelveMinCut == 12))
						{
							FileTransferComplete();
						}

					}
				});
			}

			@Override
			public void onTransferRequested(int id, String path) {
				// mFilePath = path;
				mTransId = id;
				// mFTService.receiveFile(mTransId, DEST_PATH, true);

				Log.i("test", "MainActivity_onTransferRequested");

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							mFTService.receiveFile(mTransId, Global.dirPath	+ "/temp/" + fileNameCount + ".3gp", true);
							Log.i("test", "/temp/" + fileNameCount + ".3gp 만들어 졌습니다..");

							// showQuitDialog();
						} catch (Exception e) {
							e.printStackTrace();
	
						}
					}
				});

			}
			@Override
			public void onConnectionLost() {
				// TODO Auto-generated method stub
				FileTransferComplete();
				receiveCount = 0;
				RoadGuideStartService.init();
			}
			
		};
	}

	void FileTransferComplete() {
		File file1 = new File(Global.dirPath + "/temp/mergeTemp_" + (FileAppendTask.tempFileName - 1) + ".3gp");
		File file2;
		if (FTSampleProviderImpl.eventSendCheck == true) 
		{
			file2 = new File(Global.dirPath + "/Event/" + Global.todayName + "_" + FileAppendTask.resultIndex +".3gp");
		} else 
		{
			file2 = new File(Global.dirPath + "/Ordinary/"  + Global.todayName + "_" + FileAppendTask.resultIndex +".3gp");
		}

		if (!file1.renameTo(file2))
			Log.i("test", "파일 이름 변경 실패" + file1);
		Log.i("test", "★★★★★최종파일 생성됨★★★★★__" + FileAppendTask.resultIndex + "__" + file1.getName());
		FileAppendTask.resultIndex++;
		Global.fileAppendFlag = false;
		// FileAppendTask.onlyFirstFlag = true;
		FTSampleProviderImpl.receiveData = new String();
		FTSampleProviderImpl.receiveCount = 0;
		receiveCount = 0;
		twelveMinCut = 0;
		sendBroadcastFlag = false;
		FTSampleProviderImpl.eventSendCheck = false;
		DeleteDir(Global.dirPath + "/temp");
	}

	void DeleteDir(String path) {
		File file = new File(path);
		File[] childFileList = file.listFiles();
		for (File childFile : childFileList) {
			if (childFile.isDirectory()) {
				DeleteDir(childFile.getAbsolutePath());
			} else {
				childFile.delete();
			}
		}

		file.delete();

		file = new File(Global.dirPath + "/temp");
		if (!file.exists()) {
			file.mkdir();
			Log.i("test", "BI-Box/temp 폴더생성");
		}

	}

	public void onDestroy() {
		super.onDestroy();
		isUp = false;
		RoadGuideStartService.init();
	}

	@Override
	protected void onStart() {
		super.onStart();
		isUp = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		isUp = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		isUp = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		isUp = true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("resultFileNameKey", FileAppendTask.resultIndex);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		FileAppendTask.resultIndex = savedInstanceState.getInt("resultFileNameKey");
		Log.i("test", "resultFileName (저장된거 불름)......." + FileAppendTask.resultIndex);
	}

	@Override
	public void onBackPressed() {
		if (mIsBackKeyPressed == false) {
			mIsBackKeyPressed = true;
			mCurrentTimeInMillis = Calendar.getInstance().getTimeInMillis();
			Toast.makeText(this, "Press the back again,\n     the app quits.", Toast.LENGTH_SHORT).show();
			startTimer();
		} else {
			mIsBackKeyPressed = false;
			if (Calendar.getInstance().getTimeInMillis() <= (mCurrentTimeInMillis + (BACKEY_TIMEOUT))) {
				finish();
			}
		}
	}

	private void startTimer() {
		mTimerHander.sendEmptyMessageDelayed(MSG_TIMER_EXPIRED, BACKEY_TIMEOUT);
	}

	private Handler mTimerHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_TIMER_EXPIRED: {
				mIsBackKeyPressed = false;
			}
				break;
			}
		}
	};
}
