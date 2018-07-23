package com.zshiba.android.glass.zeroshuttercamera;

import java.util.Iterator;
import java.util.Vector;

public class ZSBrainStatePredictionManager{

  private static final int QUEUE_SIZE_DEFAULT = 20; //expecting that data comes twice/[sec] from a client

  private int queueSize;
  private int numberOfNewPredictions;
  private boolean isRefilled;
  private Vector<ZSBrainStatePrediction> predictionQueue;

  public ZSBrainStatePredictionManager(){
    this(QUEUE_SIZE_DEFAULT);
  }
  public ZSBrainStatePredictionManager(int queueSize){
    this.queueSize = queueSize;
    this.predictionQueue = new Vector<ZSBrainStatePrediction>();
    this.initialize();
  }

  private void initialize(){
    this.predictionQueue.removeAllElements();
    this.numberOfNewPredictions = 0;
    this.isRefilled = false;
  }

  public void add(ZSBrainStatePrediction prediction){
    synchronized(this.predictionQueue){
      this.predictionQueue.add(prediction);
      if(this.predictionQueue.size() > this.queueSize)
        this.predictionQueue.remove(0);
      this.numberOfNewPredictions = (this.numberOfNewPredictions + 1) % this.queueSize;
      if(this.numberOfNewPredictions == 0)
        this.isRefilled = true;
    }
  }

  public void wipeAll(){
    synchronized(this.predictionQueue){
      this.initialize();
    }
  }

  public boolean isRefilledAfterAsked(){
    synchronized(this.predictionQueue){
      if(this.isRefilled){
        this.isRefilled = false;
        this.numberOfNewPredictions = 0;
        return true;
      }else{
        return false;
      }
    }
  }

  public double getAverageHighWorkloadConfidenceValue(){
    synchronized(this.predictionQueue){
      if(this.predictionQueue.size() == 0){
        return 0.0;
      }else{
        Iterator<ZSBrainStatePrediction> iterator = this.predictionQueue.iterator();
        double sum = 0.0;
        while(iterator.hasNext()){
          sum += iterator.next().getHighWorkloadConfidenceValue();
        }
        return sum / (double)this.predictionQueue.size();
      }
    }
  }

  public double getAverageLowWorkloadConfidenceValue(){
    synchronized(this.predictionQueue){
      if(this.predictionQueue.size() == 0){
        return 0.0;
      }else{
        Iterator<ZSBrainStatePrediction> iterator = this.predictionQueue.iterator();
        double sum = 0.0;
        while(iterator.hasNext())
          sum += iterator.next().getLowWorkloadConfidenceValue();
        return sum / (double)this.predictionQueue.size();
      }
    }
  }

}
