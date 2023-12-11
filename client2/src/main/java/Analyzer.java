import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Analyzer {
  BlockingQueue<String> recordList;
  ArrayList<Long> getLatencyList;
  ArrayList<Long> postLatencyList;
  CSVWriter csvWriter;
  AtomicInteger successCounter;
  AtomicInteger failCounter;

  public Analyzer(String output) {
    this.recordList = new LinkedBlockingDeque<>();
    this.getLatencyList = new ArrayList<>();
    this.postLatencyList = new ArrayList<>();
    this.csvWriter = new CSVWriter(output,recordList);
    this.successCounter = new AtomicInteger(0);
    this.failCounter = new AtomicInteger(0);
    new Thread(csvWriter).start();
  }

  public void end(){
    this.csvWriter.setRunning(false);
  }

  synchronized public void addRecord(long start,String type,long latency,int statusCode){
    if(statusCode==200||statusCode==201){
      this.successCounter.getAndIncrement();
    }else{
      this.failCounter.getAndIncrement();
    }
    if(type.equals("GET")){
      getLatencyList.add(latency);
    }else{
      postLatencyList.add(latency);
    }
    DateFormat obj = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String record = String.format("%s, %s, %d, %d\n",obj.format(new Date(start)),type,latency,statusCode);
    recordList.add(record);
  }
  public float getGetMeanResponseTime(){
    return getMeanResponseTime(getLatencyList);
  }

  public float getPostMeanResponseTime(){
    return getMeanResponseTime(postLatencyList);
  }

  public float getMeanResponseTime(ArrayList<Long> latencyList){
    long sum = 0;
    for(long t : latencyList)
      sum += t;
    return (float)sum/latencyList.size();
  }

  public float getGetMedianResponseTime(){
    return getMedianResponseTime(getLatencyList);
  }

  public float getPostMedianResponseTime(){
    return getMedianResponseTime(postLatencyList);
  }

  public float getMedianResponseTime(ArrayList<Long> latencyList){
    Collections.sort(latencyList);
    if (latencyList.size()%2 == 0) {
      return (float)(latencyList.get(latencyList.size()/2) + latencyList.get(latencyList.size()/2-1))/2;
    } else {
      return latencyList.get(latencyList.size()/2);
    }
  }

  public float getGetP99ResponseTime(){
    return getP99ResponseTime(getLatencyList);
  }

  public float getPostP99ResponseTime(){
    return getP99ResponseTime(postLatencyList);
  }

  public long getP99ResponseTime(ArrayList<Long> latencyList){
    Collections.sort(latencyList);
    return latencyList.get(latencyList.size()*99/100);
  }

  public float getGetMinResponseTime(){
    return getMinResponseTime(getLatencyList);
  }

  public float getPostMinResponseTime(){
    return getMinResponseTime(postLatencyList);
  }

  public long getMinResponseTime(ArrayList<Long> latencyList){
    return Collections.min(latencyList);
  }

  public float getGetMaxResponseTime(){
    return getMaxResponseTime(getLatencyList);
  }

  public float getPostMaxResponseTime(){
    return getMaxResponseTime(postLatencyList);
  }

  public long getMaxResponseTime(ArrayList<Long> latencyList){
    return Collections.max(latencyList);
  }
}
