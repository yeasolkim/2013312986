package swssm.fg.bi_box;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import swssm.fg.tools.SectionableAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class EventListActivity extends Activity {

	public static ArrayList<String> videoFileList= new ArrayList<String>();	
	private GridView eventGridView;
	//private MyThumbnaildapter adapter;
	private AlbumAdapter adapter;
	String temp;
	private ArrayList<String> SECTIONS;
	private ArrayList<String> SECTIONS_ALL;
	private String[][] VIDEO; 
	private int[] section_index;
	ArrayAdapter<String> listAdapter;
	 ListView EventMenuList;
	 //View toolbar;
	 //ImageButton back;
	 public static HashMap<String, Integer> map= new HashMap<String, Integer>();
	 public static ArrayList<Bitmap> bitmapArray= new ArrayList<Bitmap>();
	 ListView eventtitle;
	 
		private static final int MSG_TIMER_EXPIRED = 1;
		private static final int BACKEY_TIMEOUT = 2000;
		private boolean mIsBackKeyPressed = false;
		private long mCurrentTimeInMillis = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_list);
		overridePendingTransition(android.R.anim.slide_in_left, R.anim.slide_out_left);
		//overridePendingTransition(0, 0);

		Log.i("test",Global.dirPath + "/Event ####################################################################");
		new SingleMediaScanner(this, new File(Global.dirPath + "/Event"));
		
		eventGridView = (GridView)findViewById(R.id.eventGridView);
		eventGridView.setSmoothScrollbarEnabled(true);
		eventGridView.setNumColumns(1);
		
		//videoFileList = new ArrayList<String>();
		SECTIONS =  new ArrayList<String>();
		SECTIONS_ALL =  new ArrayList<String>();
		//back = (ImageButton)findViewById(R.id.back_button_event);
		//map = new HashMap<String, Integer>();
		//bitmapArray = new ArrayList<Bitmap>();

		Tab3.titleText.setText("event record");   
	
		 	///////////////////////////////////////////////////////////////////////////

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
		////////////////////////////////////////////////////////////////////////////
		if ( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String path = Global.dirPath + "/Event";
			File file = new File(path);
			String str;
			
			if (file.listFiles().length > 0)
			{
				for (File f : file.listFiles()) 
				{
					str = f.getName(); //파일 이름 얻어오기
					videoFileList.add(path + "/" + str);
					String input = str.split("_")[0];
					SECTIONS_ALL.add(input);
				}
			}
			else
			{
				Toast.makeText(EventListActivity.this, "there is no file", Toast.LENGTH_SHORT).show();
			}
			for (int i = 0; i < SECTIONS_ALL.size(); i++) {
				if(SECTIONS.size() == 0){
					SECTIONS.add(SECTIONS_ALL.get(i));
				}else if(!(SECTIONS.contains(SECTIONS_ALL.get(i)))){
					SECTIONS.add(SECTIONS_ALL.get(i));
				}

				
			}
			
			VIDEO = new String[SECTIONS.size()][SECTIONS_ALL.size()];
			
			section_index = new int[SECTIONS.size()];
			 
			for (File f : file.listFiles()) 
			{
				str = f.getName();
				videoFileList.add(path + "/" + str);
				String cmp = str.split("_")[0];
				
				for(int i=0; i< SECTIONS.size(); i++){
					
					if(SECTIONS.get(i).equals(cmp)){
						VIDEO[i][section_index[i]] = str;
						section_index[i]++;
					}
				}
			}
			for(int i=0; i< SECTIONS.size(); i++){
				Log.i("test", "section index"+Integer.toString(i)+Integer.toString(section_index[i]));
			}
			Log.i("test", Integer.toString(SECTIONS.size()));
			for(int i = 0 ; i < SECTIONS.size(); ++i){
				Log.i("test", SECTIONS.get(i));
				for(int j = 0 ; j < section_index[i]; ++j)
					Log.i("test", "************filename "+VIDEO[i][j]+"i"+i+"j"+ j);
			}
			
		}
		
		adapter = new AlbumAdapter(this,
				getLayoutInflater(), R.layout.list_row, R.id.listRow_header,
				R.id.listRow_itemHolder, SectionableAdapter.MODE_VARY_WIDTHS);
		
		eventGridView.setAdapter(adapter);

	
		
	}

	@Override
	public void finish(){
		super.finish();
		overridePendingTransition(android.R.anim.slide_in_left, R.anim.slide_out_left);
	}

	class AlbumAdapter extends SectionableAdapter implements
		View.OnClickListener {

	String temp;
	String path = Global.dirPath + "/Event";
	File file = new File(path);
	private Activity activity;
	ImageButton imageThumbnail;
	TextView thumbnailname;

	public AlbumAdapter(Activity activity, LayoutInflater inflater,	int rowLayoutID, int headerID, int itemHolderID, int resizeMode) {
		super(inflater, rowLayoutID, headerID, itemHolderID, resizeMode);
		this.activity = activity;
		
	}
	
	
	@Override
	public Object getItem(int position) {
		
		for (int i = 0; i < SECTIONS.size(); ++i) {
			if (position < section_index[i]) {
				Log.i("test",position+"t");
				
				Log.i("test","김예솔짱짱짱짱짱");

				return VIDEO[i][position];
			}
			position -= section_index[i];
		}
		// This will never happen.
		return null;
	}
	@Override
	protected int getDataCount() {
		int total = 0;
		for (int i = 0; i < SECTIONS.size(); ++i) {
			total += section_index[i];
		}
		return total;
	}
	@Override
	protected int getSectionsCount() {
		return SECTIONS.size();
	}
	@Override
	protected int getCountInSection(int index) {
		return section_index[index];
	}
	@Override
	protected int getTypeFor(int position) {
		int runningTotal = 0;
		int i = 0;
		for (i = 0; i < SECTIONS.size(); ++i) {
			int sectionCount = section_index[i];
			if (position < runningTotal + sectionCount)
				return i;
			runningTotal += sectionCount;
		}
		// This will never happen.
		return -1;
	}
	@Override
	protected String getHeaderForSection(int section) {
		return SECTIONS.get(section);
	}
	@Override
	protected void bindView(View convertView, int position) {
		String title = (String) getItem(position);
		imageThumbnail = (ImageButton)convertView.findViewById(R.id.Thumbnail);
		thumbnailname = (TextView)convertView.findViewById(R.id.textView1);
		Bitmap bmThumbnail;
		int hashnum;
		final String videofile;
		String hashstr;
		try{
		videofile=videoFileList.get(position).split("/Event/")[0]+"/Event/"+title;
		hashstr=videoFileList.get(position).split("/Event/")[1];
		hashnum=map.get(hashstr);
		bmThumbnail=bitmapArray.get(hashnum);
	
		thumbnailname.setText(title);
		bmThumbnail = setRoundCorner(bmThumbnail, 20);
		imageThumbnail.setBackground(new BitmapDrawable(getResources(),bmThumbnail));
		
		imageThumbnail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent videoPlayerIntent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse(videofile);
				videoPlayerIntent.setDataAndType(uri, "video/*");
				startActivity(videoPlayerIntent);
			}
			
			
		});
		
		imageThumbnail.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				 AlertDialog.Builder alt_bld = new AlertDialog.Builder(EventListActivity.this);
				    alt_bld.setMessage("Are you sure you want to delete the file?").setCancelable(
				        false).setPositiveButton("Yes",
				        new DialogInterface.OnClickListener() {
				        @Override
						public void onClick(DialogInterface dialog, int id) {
				            // Action for 'Yes' Button
				        	File file = new File(videofile);
				        	file.delete();
				        	Toast.makeText(EventListActivity.this,"[" + videofile.split("/Event/")[1] + "] was deleted.", Toast.LENGTH_SHORT).show();
				        	
//				        	adapter.notifyDataSetChanged();
							for(int i=0; i< videoFileList.size(); i++){
								if(videoFileList.get(i).equals(videofile)){
									videoFileList.remove(i);
									Log.i("test", "wooooooooooo~"+i);
									
								    Tab3.tabHost.setCurrentTab(1);
								    Tab3.tabHost.setCurrentTab(2);
//									adapter.notifyDataSetChanged();
									break;
								}
							}
							
							adapter.notifyDataSetChanged();
							
							
       	
				        }
				        }).setNegativeButton("No",
				        new DialogInterface.OnClickListener() {
				        @Override
						public void onClick(DialogInterface dialog, int id) {
				            // Action for 'NO' Button
				        	
				            dialog.cancel();
				        }
				        });
				    AlertDialog alert = alt_bld.create();
				    // Title for AlertDialog
				    alert.setTitle("Delete [" + videofile.split("/Event/")[1] + "]");
				    // Icon for AlertDialog
				    alert.show();
				
				return true;
			}
		});
		}catch(IndexOutOfBoundsException e){}

	}


	@Override
	public void onClick(View v) {
//		
	}
	public Bitmap setRoundCorner(Bitmap bitmap, int pixel) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	         
	    int color = 0xff424242;
	    Paint paint = new Paint();
	    Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    RectF rectF = new RectF(rect);
	         
	    paint.setAntiAlias(true);
	    paint.setColor(color);
	    canvas.drawARGB(0, 0, 0, 0);
	    canvas.drawRoundRect(rectF, pixel, pixel, paint);
	         
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	         
	    return output;
	}
}
	
	public void myClickHandler(View v) 
    {

		if(v.getId() == R.id.doSomething1)
		{
//			startLatitude = latitudeData;
//			startLongitude = longitudeData;
//			Intent pathSearchIntent = new Intent(NavigationActivity.this, PathSearchActivity.class);
//			Bundle currentLocation = new Bundle();
//			currentLocation.putDouble("currentLatitude", startLatitude);
//			currentLocation.putDouble("currentLongitude", startLongitude);
//			pathSearchIntent.putExtras(currentLocation);
//			startActivityForResult(pathSearchIntent, Global.PATH_SEARCH_ACTIVITY);
			
			
		}
		else if(v.getId() == R.id.doSomething2)
		{
			//createThreadAndDialog(0); 
			Intent videointent = new Intent( EventListActivity.this, OrdinaryListActivity.class );
			startActivityForResult(videointent,0);
		}
		else if(v.getId() == R.id.doSomething3)
		{
			//createThreadAndDialog(0); 
			Intent navigationIntent = new Intent( EventListActivity.this, EventListActivity.class );
			startActivityForResult(navigationIntent,0);
		}
		 
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
