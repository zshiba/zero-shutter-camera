package com.zshiba.android.glass.zeroshuttercamera;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ZSMenuActivity extends Activity{

  private static final String TAG = "ZSMenuActivity";

  private ServiceConnection cameraServiceConnection;
  private ZSCameraService cameraService;

  @Override
  public void onAttachedToWindow(){
    super.onAttachedToWindow();
//Log.d(TAG, "onAttachedToWindow");
    this.cameraServiceConnection = new ServiceConnection(){
      @Override
      public void onServiceDisconnected(ComponentName name){
//Log.d(TAG, "onServiceDisconnected");
      }
      @Override
      public void onServiceConnected(ComponentName name, IBinder service){
//Log.d(TAG, "onServiceConnected");
        if(service instanceof ZSCameraService.ZSCameraServiceBinder){
          cameraService = ((ZSCameraService.ZSCameraServiceBinder)service).getService();
          openOptionsMenu();
        }
      }
    };
    Intent service = new Intent(this, ZSCameraService.class);
    this.bindService(service, this.cameraServiceConnection, 0);
    //this.openOptionsMenu();
  }

  @Override
  public void onDetachedFromWindow(){
    super.onDetachedFromWindow();
//Log.d(TAG, "onDetachedFromWindow");
    this.unbindService(this.cameraServiceConnection);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
//Log.d(TAG, "onCreateOptionsMenu");
    this.getMenuInflater().inflate(R.menu.zero_shutter_camera, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu){
//Log.d(TAG, "onPrepareOptionsMenu");
    MenuItem startMenuItem = menu.findItem(R.id.start);
    MenuItem stopMenuItem = menu.findItem(R.id.stop);
    if(this.cameraService.hasRegisteredShutterCommandBroadcastReceiver()){
      startMenuItem.setVisible(false);
      stopMenuItem.setVisible(true);
    }else{
      startMenuItem.setVisible(true);
      stopMenuItem.setVisible(false);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item){
//Log.d(TAG, "onOptionsItemSelected");
    int id = item.getItemId();
    switch(id){
      case R.id.start:
        this.cameraService.startReceivingShutterCommands();
        return true;
      case R.id.stop:
        this.cameraService.stopReceivingShutterCommands();
        return true;
      case R.id.done:
        this.stopService(new Intent(this, ZSCameraService.class));
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onOptionsMenuClosed(Menu menu){
//Log.d(TAG, "onOptionsMenuClosed");
    this.finish();
  }
}
