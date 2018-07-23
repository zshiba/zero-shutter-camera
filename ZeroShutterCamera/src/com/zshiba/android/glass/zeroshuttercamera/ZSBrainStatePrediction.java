package com.zshiba.android.glass.zeroshuttercamera;

public class ZSBrainStatePrediction{

  public static final String CLASSIFICATION_LABEL_HIGH_WORKLOAD = "h";
  public static final String CLASSIFICATION_LABEL_LOW_WORKLOAD  = "l";

  private long acquisitionTimeInMillis;
  private String classificationLabel;
  private double confidenceValue;
  private double nonConfidenceValue;

  public ZSBrainStatePrediction(long acquisitionTimeInMillis, String classificationLabel, double confidenceValue, double nonConfidenceValue){
    this.acquisitionTimeInMillis = acquisitionTimeInMillis;
    this.classificationLabel = classificationLabel;
    this.confidenceValue = confidenceValue;
    this.nonConfidenceValue = nonConfidenceValue;
  }

  public long getAcquisitionTimeInMillis(){
    return this.acquisitionTimeInMillis;
  }
  public String getClassificationLabel(){
    return this.classificationLabel;
  }
  public double getConfidenceValue(){
    return this.confidenceValue;
  }
  public double getNonConfidenceValue(){
    return this.nonConfidenceValue;
  }

  public double getHighWorkloadConfidenceValue(){
    if(this.classificationLabel.equals(CLASSIFICATION_LABEL_HIGH_WORKLOAD))
      return this.confidenceValue;
    else
      return this.nonConfidenceValue;
  }
  public double getLowWorkloadConfidenceValue(){
    if(this.classificationLabel.equals(CLASSIFICATION_LABEL_LOW_WORKLOAD))
      return this.confidenceValue;
    else
      return this.nonConfidenceValue;
  }

  @Override
  public String toString(){
    return this.getAcquisitionTimeInMillis() + ";" +
           this.getClassificationLabel()     + ";" +
           this.getConfidenceValue()         + ";" +
           this.getNonConfidenceValue();
  }
}
