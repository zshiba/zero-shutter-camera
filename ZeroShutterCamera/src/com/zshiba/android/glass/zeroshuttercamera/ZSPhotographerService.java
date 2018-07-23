package com.zshiba.android.glass.zeroshuttercamera;

import java.text.SimpleDateFormat;

import com.google.android.glass.timeline.LiveCard;

import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class ZSPhotographerService extends Service implements ZSBrainStateInformationReceiver.OnMessageListener{

  public class ZSPhotographerServiceBinder extends Binder{
    public ZSPhotographerService getService(){
      return ZSPhotographerService.this;
    }
  }

  private static final String TAG           = "ZSPhotographerService";
  private static final String LIVE_CARD_TAG = "zero shutter camera photographer";

  private static final double BUSYNESS_THRESHOLD = 85.0;

  private IBinder binder;
  private RemoteViews liveCardRemoteViews;
  private LiveCard liveCard;
  private ZSCameraProtocol cameraProtocol;
  private ZSBrainStatePredictionManager brainStatePredictionManager;
  private ZSBrainStateInformationReceiver brainStateInformationReceiver;
  private SimpleDateFormat dateFormater;

  @Override
  public void onCreate(){
//Log.d(TAG, "onCreate");
    this.binder = new ZSPhotographerServiceBinder();
    this.liveCard = null;
    this.cameraProtocol = null;
    this.brainStatePredictionManager = null;
    this.brainStateInformationReceiver = null;
    this.dateFormater = new SimpleDateFormat("HH:mm:ss");
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
      if(intent != null){
        String key = intent.getStringExtra(ZSCameraService.EXTRA_KEY);
        this.cameraProtocol = new ZSCameraProtocol(key);
      }

      this.liveCard = new LiveCard(this, LIVE_CARD_TAG);

      this.liveCardRemoteViews = new RemoteViews(this.getPackageName(), R.layout.photographer_service_live_card_view);
      this.liveCard.setViews(this.liveCardRemoteViews);
      Intent menuIntent = new Intent(this, ZSPhotographerMenuActivity.class);
      menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      this.liveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
      this.liveCard.publish(LiveCard.PublishMode.SILENT);

      this.brainStatePredictionManager = new ZSBrainStatePredictionManager();
      this.brainStateInformationReceiver = new ZSBrainStateInformationReceiver(this);
      new Thread(this.brainStateInformationReceiver).start();
    }else{
      //Card is already published.
    }
    return START_REDELIVER_INTENT;
  }

  @Override
  public void onDestroy(){
//Log.d(TAG, "onDestroy");
    this.brainStateInformationReceiver.wrapUp();
    if(this.liveCard != null && this.liveCard.isPublished()){
      this.liveCard.unpublish();
      this.liveCard = null;
    }
  }

  private void show(String brainStateInformation, long receivedDate){
    this.liveCardRemoteViews.setTextViewText(R.id.photographerReceivedDataValueTextView, brainStateInformation);
    String date = "(" + this.dateFormater.format(receivedDate) + ")";
    this.liveCardRemoteViews.setTextViewText(R.id.photographerReceivedDataOnValueTextView, date);
    this.liveCard.setViews(this.liveCardRemoteViews);
  }

  @Override
  public void onBrainStateInformationReceived(String information){
//Log.d(TAG, "onBrainStateInformationReceived: " + information);
    long now = System.currentTimeMillis();
    this.show(information, now);
    String[] data = information.split(";");
    if(data.length == 3){
      String classification = data[0];
      if(classification != null && (classification.equals(ZSBrainStatePrediction.CLASSIFICATION_LABEL_HIGH_WORKLOAD) || classification.equals(ZSBrainStatePrediction.CLASSIFICATION_LABEL_LOW_WORKLOAD))){
        try{
          double value1 = Double.parseDouble(data[1]);
          double value2 = Double.parseDouble(data[2]);
          double confidenceValue;
          double nonConfidenceValue;
          if(value1 > value2){
            confidenceValue = value1;
            nonConfidenceValue = value2;
          }else{
            confidenceValue = value2;
            nonConfidenceValue = value1;
          }
          this.brainStatePredictionManager.add(new ZSBrainStatePrediction(now, classification, confidenceValue, nonConfidenceValue));
          if(this.shoudlTakePictureNow())
            this.issueCameraCommandRequest();
        }catch(NumberFormatException e){ //Since at least we have to know the data format coming from the fNIRS data rely server, I do not strictly check received data.
          e.printStackTrace();
//Log.d(TAG, "onBrainStateInformationReceived: ERROR: Unrecognizable data recieved: cannot parse to double: confidenceValue or nonConfidenceValue");
        }
      }else{
//Log.d(TAG, "onBrainStateInformationReceived: ERROR: Unrecognizable data recieved: classification = " + classification);
      }
    }else{
//Log.d(TAG, "onBrainStateInformationReceived: ERROR: Unrecognizable data recieved: data.length = " + data.length);
    }
  }
  private boolean shoudlTakePictureNow(){
    if(this.brainStatePredictionManager.isRefilledAfterAsked()){
      double busyness = this.brainStatePredictionManager.getAverageHighWorkloadConfidenceValue();
//Log.d(TAG, "shoudlTakePictureNow: busyness: " + busyness);
      if(busyness > BUSYNESS_THRESHOLD)
        return true;
      else
        return false;
    }else{
      return false;
    }
  }
  private void issueCameraCommandRequest(){
    Intent request = this.cameraProtocol.composeCameraCommandRequest();
    this.sendBroadcast(request);
  }

  @Override
  public void onBluetoothDeviceConnected(BluetoothDevice device){
    String name;
    String address;
    if(device != null){
      name = device.getName();
      address = "(" + device.getAddress() + ")";
      this.brainStatePredictionManager.wipeAll();
    }else{
      name = this.getString(R.string.photographer_connected_bluetooth_device_name_default);
      address = this.getString(R.string.photographer_connected_bluetooth_device_address_default);
    }
//Log.d(TAG, "onBluetoothDeviceConnected, device Name: " + name + ", Address: " + address);
    this.liveCardRemoteViews.setTextViewText(R.id.photographerConnectedBluetoothDeviceNameTextView, name);
    this.liveCardRemoteViews.setTextViewText(R.id.photographerConnectedBluetoothDeviceAddressTextView, address);
    if(this.liveCard != null) //at the very end of when the app is closed, the receiver thread may call this method while the live card is already unpublished. 
      this.liveCard.setViews(this.liveCardRemoteViews);
  }

  public boolean isConnectedToBluetoothDevice(){
    return this.brainStateInformationReceiver.isConnected();
  }
  public void disconnectFromBluetoothDevice(){
    this.brainStateInformationReceiver.disconnect();
  }

}
