package swssm.fg.bi_box;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

public class Tab3 extends TabActivity {
	public static TextView titleText;
	public static  TabHost tabHost;
    @SuppressLint("ResourceAsColor")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
       // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
       // Tab3.mTab = this;
       // TextView title = (TextView)findViewById(R.id.titleTextView); 
        //title.setText("title");
        setContentView(R.layout.activity_tab);
        tabHost = (TabHost)findViewById(android.R.id.tabhost); 
       // final  TabHost tabHost = getTabHost();
        titleText  =(TextView)findViewById(R.id.texttitle);
        
        tabHost.getTabWidget().setRightStripDrawable(android.R.color.black);
        tabHost.getTabWidget().setLeftStripDrawable(android.R.color.black);
        
        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("")
                .setContent(new Intent(this, NavigationActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.navibutton);
 
        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("")
                .setContent(new Intent(this, OrdinaryListActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.alwaysbutton);
         
        // 클릭할때 마다 리플레쉬 
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator("")
                .setContent(new Intent(this, EventListActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        tabHost.getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.eventbutton);
    }
    
    public void Tutorial(View v) 
    {

		if(v.getId() == R.id.tutorial)
		{
			//createThreadAndDialog(0); 
			Intent navigationIntent = new Intent( Tab3.this, GuideActivity.class );
			startActivityForResult(navigationIntent,0);
			overridePendingTransition(android.R.anim.slide_in_left, R.anim.slide_out_left);
		}
    }
}