package swssm.fg.bi_box;

import java.util.Calendar;

import swssm.fg.bi_box.FTSampleProviderImpl.FTSampleProviderConnection;
import android.os.Environment;
import android.widget.ListView;

public class Global {
	static final String dirPath = Environment.getExternalStorageDirectory().getPath() + "/BI-Box";
	static final int PATH_SEARCH_ACTIVITY = 0;
	static final String APP_KEY = "6d314492-4feb-3539-997c-93812d0f8397";
	static final String TMAP_USER_ID = "tprudzzang7";
	static final String POST_METHOD = "https://apis.skplanetx.com/tmap/routes/bicycle?callback=&bizAppId=&version=1";
//	static final String POST_METHOD = "https://apis.skplanetx.com/tmap/routes/pedestrian?callback=&bizAppId=&version=1";
	static final String ACCEPT_TYPE = "application/json";
	static final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8";
	static final String POST_LANGUAGE = "ko_KR";	
	static String todayName = String.valueOf(Calendar.getInstance().get(Calendar.YEAR))
			+ String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1)
			+ String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
	static boolean fileAppendFlag = false;
	public static  ListView list;
	static FTSampleProviderConnection uHandler;
	static int channelID = 107;
	public static final String CONFIG_FILE_NAME = "history.txt";
}
