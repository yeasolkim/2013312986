package swssm.fg.bi_box;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class GuideActivity extends Activity {
	


	ViewFlipper viewFlipper;
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		overridePendingTransition(0, 0);

		viewFlipper = (ViewFlipper) findViewById(R.id.GuideView);
		sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
		editor = sp.edit();

	}
	
	public void viewClickListner(View v) {
		// TODO Auto-generated method stub

		if(v.getId()==R.id.guideclose){
			editor.putInt("init", 1);
			editor.commit();
			finish();
		}else if(v.getId() != R.id.finalflipview){
			viewFlipper.setInAnimation(this, android.R.anim.fade_in);
			viewFlipper.setOutAnimation(this, android.R.anim.fade_out);
			viewFlipper.showNext();
		}
	}

}
