package com.zshiba.android.glass.zeroshuttercamera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class ZSShutterCommandBroadcastReceiver extends BroadcastReceiver{

  private static final String TAG = "ZSShutterCommandBroadcastReceiver";

  private ZSCameraProtocol protocol;

  public ZSShutterCommandBroadcastReceiver(String key){
    this.protocol = new ZSCameraProtocol(key);
  }

  @Override
  public void onReceive(Context context, Intent intent){
//Log.d(TAG, "onReceive");
    Uri request = intent.getData();
//Log.d(TAG, "onReceive + request: " + request.toString());
    if(this.protocol.verify(request)){
//Log.d(TAG, "onReceive + request was verified.");
      Intent cameraButtonActionIntent = new Intent(Intent.ACTION_CAMERA_BUTTON);
      context.sendBroadcast(cameraButtonActionIntent);
    }
  }

}
