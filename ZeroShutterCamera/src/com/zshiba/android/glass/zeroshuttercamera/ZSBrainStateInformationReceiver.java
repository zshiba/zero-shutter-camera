package com.zshiba.android.glass.zeroshuttercamera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ZSBrainStateInformationReceiver implements Runnable{

  public interface OnMessageListener{
    public abstract void onBluetoothDeviceConnected(BluetoothDevice device);
    public abstract void onBrainStateInformationReceived(String information);
  }

  private static final String TAG = "ZSBrainStateInformationReceiver";

  private static final String SERVICE_NAME_CONNECTION = "ZSBrainStateInformationReceiver";
  private static final UUID UUID_CONNECTION           = UUID.fromString("87549340-e2e2-11e3-a217-0002a5d5c51b");

  private boolean isListening;
  private OnMessageListener messageListener;
  private BluetoothAdapter bluetoothAdapter;
  private BluetoothServerSocket serverSocket;
  private BluetoothSocket clientSocket;
  private BufferedReader in;

  public ZSBrainStateInformationReceiver(OnMessageListener messageListener){
    this.isListening = true;
    this.messageListener = messageListener;
    this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    this.serverSocket = null;
    this.clientSocket = null;
    this.in = null;
  }

  public boolean isConnected(){
    if(this.clientSocket == null)
      return false;
    else
      return true;
  }

  public void wrapUp(){
//Log.d(TAG, "wrapUp");
    this.isListening = false;
    this.closeAll();
  }
  public void disconnect(){
//Log.d(TAG, "disconnect");
    this.closeAll();
  }

  private void closeAll(){
//Log.d(TAG, "closeAll");
    this.messageListener.onBluetoothDeviceConnected(null);
    try{
      /*
      if(this.in != null)
        this.in.close();
      */
      this.in = null;
      if(clientSocket != null)
        this.clientSocket.close();
      this.clientSocket = null;
      if(serverSocket != null)
        this.serverSocket.close();
      this.serverSocket = null;
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  @Override
  public void run(){
//Log.d(TAG, "run");
    while(this.isListening){
//Log.d(TAG, "run + new turn");
      this.serverSocket = null;
      this.clientSocket = null;
      this.in = null;
      try{
        this.serverSocket = this.bluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME_CONNECTION, UUID_CONNECTION);
        this.clientSocket = this.serverSocket.accept();
        BluetoothDevice connectedDevice = null;
        Set<BluetoothDevice> bondedDevices = this.bluetoothAdapter.getBondedDevices();
        Iterator<BluetoothDevice> iterator = bondedDevices.iterator();
        if(iterator.hasNext())
          connectedDevice = iterator.next(); //CHECK-LATER suppose only one device is connected.
        this.messageListener.onBluetoothDeviceConnected(connectedDevice);
        this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        String message;
        while((message = this.in.readLine()) != null && this.isListening){
          this.messageListener.onBrainStateInformationReceived(message);
        }
      }catch(IOException e){
        e.printStackTrace();
      }finally{
        this.closeAll();
      }
    }
  }

}
