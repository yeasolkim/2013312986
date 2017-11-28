/* Copyright (c) 2014 Samsung Electronics Co., Ltd.   
 * All rights reserved.   
 *   
 * Redistribution and use in source and binary forms, with or without   
 * modification, are permitted provided that the following conditions are   
 * met:   
 *   
 *     * Redistributions of source code must retain the above copyright   
 *        notice, this list of conditions and the following disclaimer.  
 *     * Redistributions in binary form must reproduce the above  
 *       copyright notice, this list of conditions and the following disclaimer  
 *       in the documentation and/or other materials provided with the  
 *       distribution.  
 *     * Neither the name of Samsung Electronics Co., Ltd. nor the names of its  
 *       contributors may be used to endorse or promote products derived from  
 *       this software without specific prior written permission.  
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS  
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT  
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR  
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT  
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,  
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY  
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT  
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE  
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
 
package swssm.fg.bi_box;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;
import com.samsung.android.sdk.accessoryfiletransfer.SAFileTransfer;
import com.samsung.android.sdk.accessoryfiletransfer.SAFileTransfer.EventListener;
import com.samsung.android.sdk.accessoryfiletransfer.SAft;

public class FTSampleProviderImpl extends SAAgent {
    public static final int MSG_PUSHFILE_ACCEPTED = 1;
    public static final int MSG_PUSHFILE_NOT_ACCEPTED = 2;

    private FileAction mFileAction = null;

    private final IBinder mBinder = new LocalBinder();
    private FTSampleProviderConnection mConnection = null;
    private SAFileTransfer mSAFileTransfer = null;
    private EventListener mCallback;
    
    private Context mContext;
    public static int receiveCount = 0;
    public static String receiveData = new String();
    public static boolean eventSendCheck = false;
    

    public FTSampleProviderImpl() {
        super("FTSampleProviderImpl", FTSampleProviderConnection.class);
    }

    public class LocalBinder extends Binder {
        public FTSampleProviderImpl getService() {
            return FTSampleProviderImpl.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // super.findPeerAgents();
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        
        Log.i("test","FTSampleProviderImpl_onCreate");
        
        mCallback = new EventListener() {                
            @Override
            public void onProgressChanged(int transId, int progress) {
                //Log.d("test", "onTransferProgress@@@@ : " + progress + " transId : " + transId);

                if (mFileAction != null) {
                    mFileAction.onProgress(progress);
                }
            }
                
            @Override
            public void onTransferCompleted(int transId, String fileName, int errorCode) {
                Log.i("test", "onTransferComplete____file name : " + fileName);
                
                
                if (errorCode == 0)
                {
                    mFileAction.onTransferComplete(fileName);
                    Log.i("test","onTransferCompleted :: " + fileName);
                    
                } 
                else
                {
                    mFileAction.onError("Error", errorCode);
                }
                
                
                
            }                

            @Override
            public void onTransferRequested(int id, String fileName) {
                Log.i("test", "onTransferRequested_____file name : " + fileName);
//                if (MainActivity.isUp)
                    mFileAction.onTransferRequested(id, fileName);
//                else
//                    mContext.startActivity(new Intent().setClass(mContext, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setAction("incomingFT").putExtra("tx", id).putExtra("fileName", fileName));
               
            }

        };

        SAft SAftPkg = new SAft();
        try 
        {
            SAftPkg.initialize(this);
        }
        catch (SsdkUnsupportedException e)
        {
            if (e.getType() == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
                
            } else if (e.getType() == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
              
            } else {
              
            }

            e.printStackTrace();                
            return;
        } catch (Exception e1) {
           
            e1.printStackTrace();
            return;
        }
        
        Log.i("test","mSAFileTransfer  UPPPER");
        
        mSAFileTransfer = new SAFileTransfer(FTSampleProviderImpl.this, mCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("test", "Service Stopped.");
    }

    public void onDataAvailableonChannel(int connectionId, long channelId, String data) {
    	//기어에서 메세지 보낼때 받는 함수... 여기에 data 변수에 메세지 들어옴묭..ㅋㅋㅋㅋㅋㅋㅋㅋㅋ
        Log.i("test", "기어에서 온데이터....connectionID : "+ connectionId+"//채널ID : " + channelId + "//들어온 데이터 : " + data);
    }

    @Override
    protected void onServiceConnectionResponse(SASocket uSocket, int error) {
    	if (error == 0) {
        	FTSampleProviderConnection localConnection = (FTSampleProviderConnection) uSocket;
            if (uSocket != null) {
                mConnection = localConnection;
                Global.uHandler = mConnection;
               Log.i("test","연결 되었습니다.");
            }
        }
    }

    @Override
    protected void onFindPeerAgentResponse(SAPeerAgent peerAgent, int result) {
    }

    public void registerFileAction(FileAction action){
        this.mFileAction = action;
    }

    public void cancelFileTransfer(int transId) {
        if (mSAFileTransfer != null) { 
            mSAFileTransfer.cancel(transId);
        }
    }

    public void receiveFile(int transId, String path, boolean bAccept) {
        Log.i("test", "receiving fil@@@@@ : path : " + path + "///// " + bAccept);
        if (mSAFileTransfer != null) {
            if (bAccept) {
                mSAFileTransfer.receive(transId, path);
            } else {
                mSAFileTransfer.reject(transId);
            }
        }
    }

    public class FTSampleProviderConnection extends SASocket {
       // public static final String TAG = "FTSampleProviderConnection";
        int mConnectionId;

        public FTSampleProviderConnection() {
            super(FTSampleProviderConnection.class.getName());
        }

        
        @Override
        protected void onServiceConnectionLost(int errorCode) {
            Log.i("test", "onServiceConectionLost  for peer = " + mConnectionId + "error code =" + errorCode);
            Log.i("test", "연결 끊겼어요오오오오오오오~");
            mConnection = null;
            mFileAction.onConnectionLost();
//            Global.fileAppendFlag = false;
//  			receiveData = new String();
//  			receiveCount = 0;
//  			MainActivity.receiveCount = 0;
  			
        }

        @Override
        public void onReceive(int channelId, byte[] data) {
        	Log.i("test","온리시버~~!!들르와아아아~~");
        	
 
        	
        	Global.uHandler = mConnection;
        	Global.channelID = channelId;
        	
        	
            try {
            	receiveData = new String(data, "UTF-8"); 
            	
            	Log.i("test", "@@@@@기어에서 보낸 데이터@@@@@@  " + receiveData);
            	
            	
            	//이벤트 메세지 들어왔을때는..이게 안되니까.. skip
            	if(receiveData.equals("EVENT_SEND"))
            	{
            		eventSendCheck = true;
            		receiveData = new String();
            		try{
            			Log.i("test","EVENT_SEND메세지가 왔고...내가 EVENT_ACK을 보낼것이다...");
                		Global.uHandler.send(Global.channelID, new String("EVENT_ACK").getBytes());
                	}catch(IOException e){
                		Log.i("test","EEEEEEEEEEEEEEERRRRRRRRRRRRRROOOOOOOOOOOOOOORRRRRRRRRRRRRR");
                		e.printStackTrace();
                	}
            	}
            	else
            	{
            		receiveCount = Integer.parseInt(receiveData);
            	}
            	
            	
                onDataAvailableonChannel(mConnectionId, channelId, new String(data, "UTF-8"));
                
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(int channelId, String errorMessage, int errorCode) {
            mFileAction.onError(errorMessage, errorCode);
            Log.e("test", "OnError");
            Log.e("test", "Connection is not alive ERROR: " + errorMessage + "  " + errorCode);
        }
        
    }

    public interface FileAction {
        void onError(String errorMsg, int errorCode);
        void onProgress(long progress);
        void onTransferComplete(String path);
        void onTransferRequested(int id, String path);
        void onConnectionLost();
    }
}
