package swssm.fg.bi_box;

import java.io.File;
import java.util.ArrayList;

import swssm.fg.tools.FileHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapData.FindAllPOIListenerCallback;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;

public class PathSearchActivity extends Activity implements OnClickListener{

	EditText destTextView;
	String startStr;
	String destStr;
	TMapData tmapdata;
	Button destEnterBtn;
	Button lastPathBtn;
	ListView searchListView;
	ArrayAdapter<String> searchPathAdapter;
	ArrayList<String> searchPoiNameStr;
	//ArrayAdapter<String> lastPathAdapter;
	//ArrayList<String> lastPathStr;
	ArrayList<TMapPoint> searchPoiTMapPoint;
	Bundle searchResultExtra;
	Intent getIntent;
	String startName, destinationName;
	double startLatitude, startLongitude;
	double destinationLatitude, destinationLongitude;
	public static File configFile_list;
	private FileHelper fileHelper;
	ArrayList<String> temps;
	ArrayList<Double> tempsLatitude;
	ArrayList<Double> tempsLongitude;
	Button back;
//	SearchPoiBackground searchPoiTask;
	private boolean searchButtonClickFlag;
	
	int check =0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path_search);
		overridePendingTransition(R.anim.in,android.R.anim.fade_out);
		destTextView = (EditText) findViewById(R.id.destinationTextView);
		destEnterBtn = (Button) findViewById(R.id.destibationEnter);
		searchListView = (ListView) findViewById(R.id.searchListView);
		back = (Button)findViewById(R.id.back);
		
		fileHelper = new FileHelper(this);
		File dir =  new File(Global.dirPath + "/History");
		configFile_list = fileHelper.makeFile(dir,"/History/" + Global.CONFIG_FILE_NAME);
		
		
		tmapdata = new TMapData();
		
		searchPoiNameStr = new ArrayList<String>();
		searchPoiTMapPoint = new ArrayList<TMapPoint>();
		
		temps = new ArrayList<String>();
		tempsLatitude = new ArrayList<Double>();
		tempsLongitude = new ArrayList<Double>();
		searchButtonClickFlag = false;
		
		getIntent = getIntent();
		startName = new String("현 위치");
		startLatitude = getIntent.getDoubleExtra("currentLatitude", 0);
		startLongitude = getIntent.getDoubleExtra("currentLongitude", 0);
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stubfini
				finish();
				overridePendingTransition(0,R.anim.out);
			}
		});
		//=================
		searchPathAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchPoiNameStr);
		searchListView.setAdapter(searchPathAdapter);
		
//		searchPoiTask = new SearchPoiBackground();
		
//		TextWatcher textWatcher = new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				// TODO Auto-generated method stub
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//				// TODO Auto-generated method stub
//			}
//			
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//				Log.i("test", s.toString());
//				startStr = s.toString();
//				
//				new SearchPoiBackground().execute();
//				
//				//searchPathAdapter.clear();
//				//searchPathAdapter.addAll(searchPoiNameStr);
//				//searchPathAdapter.notifyDataSetChanged();
//			}
//		};
//		destTextView.addTextChangedListener(textWatcher);
		//=================
		 

		destEnterBtn.setOnClickListener(this);
		//lastPathBtn.setOnClickListener(this);
		destTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO ||actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_SEND)
				{
					destEnterBtn.performClick();
				}
				return false;
			}
		});
		
		searchListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				// TODO Auto-generated method stub
				String dialogString = new String();
				Log.i("test",searchPoiNameStr.get(position));
				if(searchButtonClickFlag)
					dialogString = searchPoiNameStr.get(position).split("\\(")[0];
				else
					dialogString = temps.get(position);
				
				AlertDialog.Builder searchDialog = new AlertDialog.Builder(PathSearchActivity.this);
				searchDialog.setMessage(dialogString + " is it correct?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//Action 'Yes' Button
						
										if (searchButtonClickFlag) 
										{
											destinationName = searchPoiNameStr.get(position).split("\\(")[0];
											destinationLatitude = Double.parseDouble(searchPoiTMapPoint.get(position).toString().split(" Lon ")[0].split("Lat ")[1]);
											destinationLongitude = Double.parseDouble(searchPoiTMapPoint.get(position).toString().split(" Lon ")[1]);
										}
										else
										{
											destinationName = temps.get(position);
											destinationLatitude = tempsLatitude.get(position);
											destinationLongitude = tempsLongitude.get(position);
											searchButtonClickFlag = false;
										}
										
										Intent searchToNavi = new Intent();
										searchResultExtra = new Bundle();
										searchResultExtra.putString("startName", startName);
										searchResultExtra.putDouble("startLat", startLatitude);
										searchResultExtra.putDouble("startLon", startLongitude);
										searchResultExtra.putString("destName", destinationName);
										searchResultExtra.putDouble("destLat", destinationLatitude);
										searchResultExtra.putDouble("destLon", destinationLongitude);

										searchToNavi.putExtras(searchResultExtra);
										setResult(RESULT_OK, searchToNavi);
										finish();

										String input = (destinationName + "//" + destinationLatitude + "@@" + destinationLongitude + "\n");
										//최근 3개랑 일치하면 history에 넣지 않음
			                              byte[] configData = fileHelper.readFile(configFile_list);      
			                              String str = new String(configData);
			                              String[] configList = str.split("\n");
			                              
			                              if((configList.length<3)||(!configList[configList.length-1].split("//")[0].equals(destinationName))&&
			                                    (!configList[configList.length-2].split("//")[0].equals(destinationName))&&
			                                       (!configList[configList.length-3].split("//")[0].equals(destinationName))){
			                                 fileHelper.writeFile(configFile_list, input.getBytes());
			                                 }
										searchToNavi.putExtras(searchResultExtra);

										setResult(RESULT_OK, searchToNavi);
										finish();
										
									}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//Action 'NO' Button
						dialog.cancel();
					}
				});
				
				AlertDialog alert = searchDialog.create();
				alert.setTitle("Destination Confirm");
				alert.show();
				
				
			}
		});
		
		try{
			new SearchlastPathBackground().execute();
			
			
			
		}catch(NullPointerException e)
		{
			Log.i("test",e.toString());
		}
		
	}
	@Override
	public void finish(){
		super.finish();
		overridePendingTransition(0,R.anim.out);
	}
//	class SearchPoiBackground extends AsyncTask<Void, Void, Void> {
//
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
//			searchPoiNameStr.clear();
//			searchPoiTMapPoint.clear();
//			searchPathAdapter.clear();
//			
//		}
//		@Override
//		protected Void doInBackground(Void... params) {
//			// TODO Auto-generated method stub
//			Log.i("test", startStr);
//
//			tmapdata.findAllPOI(startStr, new FindAllPOIListenerCallback() {
//
//				@Override
//				public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
//					// TODO Auto-generated method stub
//					for (int i = 0; i < poiItem.size(); i++) {
//						TMapPOIItem item = poiItem.get(i);
////						 Log.i("test",i+" : "+item.getPOIName() + "////" + item.getPOIAddress() + "/////" + item.getPOIPoint());
//						searchPoiTMapPoint.add(item.getPOIPoint());
//						searchPoiNameStr.add(item.getPOIName());
//					}
//					
//					searchPathAdapter.clear();
//					searchPathAdapter.addAll(searchPoiNameStr);
//
//				}
//			});
//			
//			return null;
//		}
//		@Override
//		protected void onPostExecute(Void result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//
//			Log.i("test","onPostExecute");
//			try
//			{
//				searchPathAdapter.notifyDataSetChanged();
//			}catch(Exception e)
//			{
//				Log.e("test","error");
//			}
//			
//
//		}
//	}
	
	class SearchlastPathBackground extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			searchPathAdapter.clear();
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//+++++++++++++++++++history

			byte[] configData = fileHelper.readFile(configFile_list);
			if( configData.length == 0 ){
				return null;
			}
			String str = new String(configData);

			if( str == "" ){
				return null;
			}
	      
			String[] configList = str.split("\n");


			int length = configList.length;	
			Log.d("test", "length send prev = " + length);
			try {
				if(length > 0 ){
					
		            for (int i = length-1; i >=0; i--) {
		            Log.i("test", "length "+length+" i "+i);
		               temps.add(configList[i].split("//")[0]);
		               tempsLatitude.add(Double.parseDouble(configList[i].split("//")[1].split("@@")[0]));
		               tempsLongitude.add(Double.parseDouble(configList[i].split("//")[1].split("@@")[1]));
		            }
				}
	            if (length > 20) 
	            {
	               int lengthMinus = length - 20;
	               for(int i=0;i<lengthMinus;i++)
	               {
	                  temps.remove(length-1-i);
	                  tempsLatitude.remove(length-1-i);
	                  tempsLongitude.remove(length-1-i);
	               }
	               
	            }
	         } catch (NullPointerException e1) {
	            Log.i("test",e1.toString());
	         }
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(temps.size() > 0)
				searchPathAdapter.addAll(temps);
			searchPathAdapter.notifyDataSetChanged();
		}
	}
	

	
	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	 int nullFlag =0;
	@Override
	public void onClick(View v) {
		nullFlag = 0;
		// TODO Auto-generated method stub
		if(v.getId() == R.id.destibationEnter)
		{
			searchButtonClickFlag = true;
			searchPoiNameStr.clear();
			searchPoiTMapPoint.clear();
			
			tmapdata.findAllPOI(destTextView.getText().toString(), new FindAllPOIListenerCallback() {

				@Override
				public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
					// TODO Auto-generated method stub
					if(poiItem.size() == 0)
						nullFlag =1;
					
					for (int i = 0; i < poiItem.size(); i++) {
						TMapPOIItem item = poiItem.get(i);
						searchPoiTMapPoint.add(item.getPOIPoint());
						searchPoiNameStr.add(item.getPOIName()+"("+item.getPOIAddress().replace("null", "")+")");
						Log.i("test",i + "");
					}
					
					runOnUiThread(new Runnable() {
						public void run() {
							Log.i("test","runOnUiThread");
							searchPathAdapter.notifyDataSetChanged();
							if(nullFlag == 1)
								Toast.makeText(PathSearchActivity.this, "There is no result.", Toast.LENGTH_SHORT).show();
							
							
							
						}
					});

				}
			});
			searchPathAdapter.clear();
			searchPathAdapter.addAll(searchPoiNameStr);
			hideKeyboard();
			
		}
		
	}
	private void hideKeyboard(){
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

	}
}


