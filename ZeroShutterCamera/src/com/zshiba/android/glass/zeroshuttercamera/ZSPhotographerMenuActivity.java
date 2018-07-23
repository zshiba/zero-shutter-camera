package com.zshiba.android.glass.zeroshuttercamera;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ZSPhotographerMenuActivity extends Activity{

  private static final String TAG = "ZSPhotographerMenuActivity";

  private ServiceConnection photographerServiceConnection;
  private ZSPhotographerService photographerService;

  @Override
  public void onAttachedToWindow(){
    super.onAttachedToWindow();
//Log.d(TAG, "onAttachedToWindow");
    this.photographerServiceConnection = new ServiceConnection(){
      @Override
      public void onServiceDisconnected(ComponentName name){
//Log.d(TAG, "onServiceDisconnected");
      }
      @Override
      public void onServiceConnected(ComponentName name, IBinder service){
//Log.d(TAG, "onServiceConnected");
        if(service instanceof ZSPhotographerService.ZSPhotographerServiceBinder){
          photographerService = ((ZSPhotographerService.ZSPhotographerServiceBinder)service).getService();
          openOptionsMenu();
        }
      }
    };
    Intent service = new Intent(this, ZSPhotographerService.class);
    this.bindService(service, this.photographerServiceConnection, 0);
  }

  @Override
  public void onDetachedFromWindow(){
    super.onDetachedFromWindow();
//Log.d(TAG, "onDetachedFromWindow");
    this.unbindService(this.photographerServiceConnection);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
//Log.d(TAG, "onCreateOptionsMenu");
    this.getMenuInflater().inflate(R.menu.zero_shutter_camera_photographer, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu){
//Log.d(TAG, "onPrepareOptionsMenu");
    MenuItem disconnectMenuItem = menu.findItem(R.id.disconnect);
    if(this.photographerService.isConnectedToBluetoothDevice())
      disconnectMenuItem.setVisible(true);
    else
      disconnectMenuItem.setVisible(false);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item){
//Log.d(TAG, "onOptionsItemSelected");
    int id = item.getItemId();
    switch(id){
      case R.id.disconnect:
        this.photographerService.disconnectFromBluetoothDevice();
        return true;
      case R.id.discoverable:
        this.makeDeviceDiscoverable();
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

  public void makeDeviceDiscoverable(){
//Log.d(TAG, "makeDeviceDiscoverable");
Log.d(TAG, "makeDeviceDiscoverable, NOT SUPPORTED");
/* Not supported
    if(BluetoothAdapter.getDefaultAdapter().getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
      Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
      //discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
      this.startActivity(discoverableIntent);
    }
*/
  }

}
