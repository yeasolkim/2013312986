package swssm.fg.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;



/**
 * 2013.10.22 - sky
 * 
 * This class help making or reading files.
 */



public class FileHelper {
	private static final String TAG = "FileHelper";
	
	private Context mContext;
	
	public FileHelper( Context context ){
		mContext = context;
	}

	
	
	public File makeDirectory( String dirPath ){
        File dir = new File( Environment.getExternalStorageDirectory() + "/BI-Box" +dirPath );
        
        if ( !dir.exists() ){
            if( dir.mkdirs() == false ){
            	Log.e(TAG, "mkdir fail");
            	return null;
            }
            Log.i( TAG , dirPath + " Directory is not exist. So make it." );
        }
        else{
            Log.i( TAG , dirPath + " Directory already exist" );
        }
 
        return dir;
    }
	
	public File makeFile(File dir, String _filePath) {
		File file = null;
		boolean isSuccess = false;
		
		String filePath = Environment.getExternalStorageDirectory() +"/BI-Box"+ _filePath;
		
		if ( dir.isDirectory() ) {
			file = new File(filePath);
			
			if (file != null && !file.exists()) {
				try {
					isSuccess = file.createNewFile();
				} 
				catch (IOException e) {
					Log.d( TAG, "Fail to make file - " + filePath );
					e.printStackTrace();
				}
				finally {
					Log.i(TAG, "Creating file YN = " + isSuccess);
				}
			} 
			else {
				Log.i(TAG, "file already exists");
			}
		}
		return file;
	}

	public String getAbsolutePath(File file) {
		return "" + file.getAbsolutePath();
	}

	public boolean deleteFile(File file) {
		boolean result;
		
		if (file != null && file.exists()) {
			file.delete();
			result = true;
		} 
		else {
			result = false;
		}
		
		return result;
	}

	public boolean isFile(File file) {
		boolean result;
		
		if (file != null && file.exists() && file.isFile()) {
			result = true;
		} 
		else {
			result = false;
		}
		
		return result;
	}

	public boolean isDirectory(File dir) {
		boolean result;
		
		if (dir != null && dir.isDirectory()) {
			result = true;
		} 
		else {
			result = false;
		}
		
		return result;
	}

	public boolean isFileExist(File file) {
		boolean result;
		
		if (file != null && file.exists()) {
			result = true;
		} 
		else {
			result = false;
		}
		
		return result;
	}

	public boolean reNameFile(File file, File new_name) {
		boolean result;
		
		if (file != null && file.exists() && file.renameTo(new_name)) {
			result = true;
		} 
		else {
			result = false;
		}
		
		return result;
	}

	public String[] getList(File dir) {
		if (dir != null && dir.exists())
			return dir.list();
		
		return null;
	}

	public boolean writeFile(File file, byte[] file_content) {
		boolean result;
		FileOutputStream fos;
		
		if (file != null && file.exists() && file_content != null) {
			try {
				fos = new FileOutputStream(file,true);
				try {
					fos.write(file_content);
					fos.flush();
					fos.close();
				} 
				catch (IOException e) {
					Log.d( TAG , "Fail writing file" );
					e.printStackTrace();
				}
			} 
			catch (FileNotFoundException e) {
				Log.d( TAG , "Fail writing file" );
				e.printStackTrace();
			}
			Log.i( TAG , "Write file success" );
			result = true;
		} 
		else {
			Log.d( TAG , "Fail reading file" );
			result = false;
		}
		
		return result;
	}

	public byte[] readFile(File file) {
		int readcount = 0;
		byte[] buffer = null;
		
		if (file != null && file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				readcount = (int) file.length();
				buffer = new byte[readcount];
				fis.read(buffer);
				fis.close();
				Log.i( TAG , "Read file success" );
				return buffer;
				
			} 
			catch (Exception e) {
				Log.d( TAG , "Fail reading file" );
				e.printStackTrace();
			}
		}
		
		return buffer;
	}

	public boolean copyFile(File file, String save_file) {
		boolean result;
		
		if (file != null && file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				FileOutputStream newfos = new FileOutputStream(save_file);
				int readcount = 0;
				byte[] buffer = new byte[1024];
				while ((readcount = fis.read(buffer, 0, 1024)) != -1) {
					newfos.write(buffer, 0, readcount);
				}
				newfos.close();
				fis.close();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			result = true;
		} 
		else {
			result = false;
		}
		return result;
	}
}
