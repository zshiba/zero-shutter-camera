package com.zshiba.android.glass.zeroshuttercamera;

import java.text.SimpleDateFormat;
import java.util.Random;

import com.google.android.glass.timeline.LiveCard;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class ZSCameraService extends Service{

  public class ZSCameraServiceBinder extends Binder{
    public ZSCameraService getService(){
      return ZSCameraService.this;
    }
  }

  private static final String TAG           = "ZSCameraService";
  private static final String LIVE_CARD_TAG = "zero shutter camera";

  public static final String EXTRA_KEY = "key";

  private IBinder binder;
  private RemoteViews liveCardRemoteViews;
  private LiveCard liveCard;
  private String key;
  private BroadcastReceiver shutterCommandBroadcastReceiver;
  private IntentFilter shutterCommandIntentFilter;
  private boolean hasRegisteredShutterCommandBroadcastReceiver;
  private SimpleDateFormat dateFormater;

  @Override
  public void onCreate(){
//Log.d(TAG, "onCreate");
    this.binder = new ZSCameraServiceBinder();
    this.liveCard = null;
    this.key = this.generateKey();
    this.shutterCommandBroadcastReceiver = new ZSShutterCommandBroadcastReceiver(this.key);
    this.shutterCommandIntentFilter = ZSCameraProtocol.createIntentFilter();
    this.hasRegisteredShutterCommandBroadcastReceiver = false;
    this.dateFormater = new SimpleDateFormat("HH:mm 'on' MMM d, ''yy");
  }

  @Override
  public IBinder onBind(Intent intent){
//Log.d(TAG, "onBind");
    return this.binder;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId){
//Log.d(TAG, "onStartCommand");
    if(this.liveCard == null){
      this.liveCard = new LiveCard(this, LIVE_CARD_TAG);

      this.liveCardRemoteViews = new RemoteViews(this.getPackageName(), R.layout.live_card_view);
      this.liveCardRemoteViews.setTextViewText(R.id.shutterCommandBroadcastReceiverStateTextView, this.getString(R.string.shutter_command_broadcast_receiver_state_off));
      this.liveCard.setViews(this.liveCardRemoteViews);
      Intent menuIntent = new Intent(this, ZSMenuActivity.class);
      menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      this.liveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
      this.liveCard.publish(LiveCard.PublishMode.REVEAL);
      this.startReceivingShutterCommands();
      this.startPhotographerService();
    }else{
      //Card is already published.
    }
    return START_STICKY;
  }

  @Override
  public void onDestroy(){
//Log.d(TAG, "onDestroy");
    if(this.hasRegisteredShutterCommandBroadcastReceiver)
      this.stopReceivingShutterCommands();
    this.stopPhotographerService();
    if(this.liveCard != null && this.liveCard.isPublished()){
      this.liveCard.unpublish();
      this.liveCard = null;
    }
  }

  private String generateKey(){
    String key = "";
    Random random = new Random();
    for(int i = 0; i < 10; i++)
      key += String.valueOf(random.nextInt(10));
    return key;
  }

  private void startPhotographerService(){
    Intent intent = new Intent(this, ZSPhotographerService.class);
    intent.putExtra(EXTRA_KEY, this.key);
    this.startService(intent);
  }
  private void stopPhotographerService(){
    this.stopService(new Intent(this, ZSPhotographerService.class));
  }

  public boolean hasRegisteredShutterCommandBroadcastReceiver(){
    return this.hasRegisteredShutterCommandBroadcastReceiver;
  }

  public void startReceivingShutterCommands(){
    this.registerReceiver(this.shutterCommandBroadcastReceiver, this.shutterCommandIntentFilter);
    this.liveCardRemoteViews.setTextViewText(R.id.shutterCommandBroadcastReceiverStateTextView, this.getString(R.string.shutter_command_broadcast_receiver_state_on));
    this.liveCardRemoteViews.setTextColor(R.id.shutterCommandBroadcastReceiverStateTextView, this.getResources().getColor(R.color.green));
    this.liveCardRemoteViews.setTextViewText(R.id.shutterCommandBroadcastReceiverSinceLableTextView, this.getString(R.string.shutter_command_broadcast_receiver_since_label));
    String now = this.dateFormater.format(System.currentTimeMillis());
    this.liveCardRemoteViews.setTextViewText(R.id.shutterCommandBroadcastReceiverSinceTextView, now);
    this.liveCardRemoteViews.setInt(R.id.iconImageView, "setColorFilter", 0);
    this.liveCard.setViews(this.liveCardRemoteViews);
    this.hasRegisteredShutterCommandBroadcastReceiver = true;
  }
  public void stopReceivingShutterCommands(){
    this.unregisterReceiver(this.shutterCommandBroadcastReceiver);
    this.liveCardRemoteViews.setTextViewText(R.id.shutterCommandBroadcastReceiverStateTextView, this.getString(R.string.shutter_command_broadcast_receiver_state_off));
    this.liveCardRemoteViews.setTextColor(R.id.shutterCommandBroadcastReceiverStateTextView, this.getResources().getColor(R.color.red));
    this.liveCardRemoteViews.setTextViewText(R.id.shutterCommandBroadcastReceiverSinceLableTextView, this.getString(R.string.shutter_command_broadcast_receiver_since_blank));
    this.liveCardRemoteViews.setTextViewText(R.id.shutterCommandBroadcastReceiverSinceTextView, this.getString(R.string.shutter_command_broadcast_receiver_since_blank));
    this.liveCardRemoteViews.setInt(R.id.iconImageView, "setColorFilter", this.getResources().getColor(R.color.gray));
    this.liveCard.setViews(this.liveCardRemoteViews);
    this.hasRegisteredShutterCommandBroadcastReceiver = false;
  }
}
