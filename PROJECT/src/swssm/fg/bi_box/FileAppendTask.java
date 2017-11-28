package swssm.fg.bi_box;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;


public class FileAppendTask extends BroadcastReceiver{
	
	private String firstFile, secondFile,actionName;
	private int fileCount;
	
	public static boolean onlyFirstFlag = true;
	public static int fileNameInteger = 1;
	public static int tempFileName = 1;
	public static int resultIndex = 1;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		actionName= intent.getAction();
		
		if(actionName.equals("swssm.fg.bi_box.filetransfercomplete"))
		{
			
			
			Bundle getFileNameBundle = intent.getExtras();
			fileCount = getFileNameBundle.getInt("fileCount");
			
			Log.i("test","@@@FileAppendTask____" + fileCount);

			
			Log.i("test","1");
			
			
			try {
				Log.i("test", "file_merge_start");

				//if (onlyFirstFlag) {
					Log.i("test", "2");
					firstFile = Global.dirPath + "/temp/mergeTemp_"+ (tempFileName - 1) + ".3gp";
//					onlyFirstFlag = false;
					if(!(new File(firstFile).exists()))
					{
						firstFile = Global.dirPath + "/temp/" + (fileCount - 1)	+ ".3gp";
					}
						
						
//				} else {
					Log.i("test", "3");
//					firstFile = Global.dirPath + "/temp/mergeTemp_"	+ (tempFileName - 1) + ".3gp";
//				}
				Log.i("test", "4");
				secondFile = Global.dirPath + "/temp/" + fileCount + ".3gp";
				Log.i("test", "4_0");
				Log.i("test",firstFile + "////" + secondFile);
				
				Movie[] inMovies = new Movie[] { 
						MovieCreator.build(firstFile),
						MovieCreator.build(secondFile),
						};

				Log.i("test", "4_1");

				List<Track> videoTracks = new LinkedList<Track>();
				List<Track> audioTracks = new LinkedList<Track>();

				Log.i("test", "4_2");

				for (Movie m : inMovies) {
					for (Track t : m.getTracks()) {
						if (t.getHandler().equals("soun")) {
							audioTracks.add(t);
						}
						if (t.getHandler().equals("vide")) {
							videoTracks.add(t);
						}
					}
				}

				Log.i("test", "5");
				Movie result = new Movie();

				if (audioTracks.size() > 0) {
					result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
				}
				if (videoTracks.size() > 0) {
					result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
				}

				Log.i("test", "6");
				Container out = new DefaultMp4Builder().build(result);

				Log.i("test", "7");
				File tempOutputFile = new File(Global.dirPath + "/temp/mergeTemp_"+ tempFileName + ".3gp");
				tempFileName++;
				Log.i("test", "8");
				FileOutputStream fc = new FileOutputStream(tempOutputFile);
				// RandomAccessFile(String.format("output.mp4"),
				// "rw").getChannel();
				Log.i("test", "9");
				out.writeContainer(fc.getChannel());
				Log.i("test", "10");
				Log.i("test_first", firstFile);
				Log.i("test_second", secondFile);

				// ADD CODE --> 여기에 기어에서 들어온 파일 삭제하는 코드 추가(주석해제하면됨)
//				tempOutputFile = new File(firstFile);
//				if(!(firstFile.equals(Global.dirPath + "/temp/mergeTemp.3gp")))
//				{
//					tempOutputFile.delete();
//				}
//					
//
//				tempOutputFile = new File(secondFile);
//				tempOutputFile.delete();

				Log.i("test", "file_merge_end");

				fc.close();

			} catch (IOException e) {
				Log.i("test", "파일 합치기 실패");
			}

		}

	}
		
	
	
}