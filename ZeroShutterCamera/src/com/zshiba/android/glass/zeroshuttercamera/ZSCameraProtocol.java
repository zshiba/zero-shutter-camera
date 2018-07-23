package com.zshiba.android.glass.zeroshuttercamera;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

public class ZSCameraProtocol{

  private static final String INTENT_DATA_SCHEME = "zsc";
  private static final String INTENT_DATA_HOST   = "zshiba.com";

  private static final String INTENT_DATA_SERVICE = "zeroshuttercamera";
  private static final String INTENT_DATA_QUERY   = "query";
  private static final String INTENT_DATA_KEY     = "key";
  private static final String INTENT_DATA_COMMAND = "command";

  private static final String COMMAND_CAMERA = "camera";


  private String key;

  public static IntentFilter createIntentFilter(){
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addDataScheme(INTENT_DATA_SCHEME);
    intentFilter.addDataAuthority(INTENT_DATA_HOST, null);
    return intentFilter;
  }

  public ZSCameraProtocol(String key){
    this.key = key;
  }

  public boolean verify(Uri request){
    boolean shouldTakePictureNow = false;
    if(request != null){
      String scheme = request.getScheme();
      if(scheme != null && scheme.equals(INTENT_DATA_SCHEME)){
        String host = request.getHost();
        if(host != null && host.equals(INTENT_DATA_HOST)){
          String path = request.getPath();
          String[] pathes = path.split("/");
          if(pathes.length == 3){
            String service = pathes[1];
            if(service != null && service.equals(INTENT_DATA_SERVICE)){
              String query = pathes[2];
              if(query.equals(INTENT_DATA_QUERY)){
                String key = request.getQueryParameter(INTENT_DATA_KEY);
                if(key != null && key.equals(this.key)){
                  String command = request.getQueryParameter(INTENT_DATA_COMMAND);
                  if(command != null && command.equals(COMMAND_CAMERA))
                    shouldTakePictureNow = true;
                }
              }
            }
          }
        }
      }
    }
    return shouldTakePictureNow;
  }

  public Intent composeCameraCommandRequest(){
    Uri.Builder builder = new Uri.Builder();
    builder.scheme(INTENT_DATA_SCHEME);
    builder.authority(INTENT_DATA_HOST);
    builder.appendPath(INTENT_DATA_SERVICE);
    builder.appendPath(INTENT_DATA_QUERY);
    builder.appendQueryParameter(INTENT_DATA_KEY, this.key);
    builder.appendQueryParameter(INTENT_DATA_COMMAND, COMMAND_CAMERA);
    Uri data = builder.build();
    Intent request = new Intent();
    request.setData(data);
    return request;
  }
}
