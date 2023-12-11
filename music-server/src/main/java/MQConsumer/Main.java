package MQConsumer;

import cs6650_assignment.Server.AlbumDAO;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

public class Main {
  public static void main(String[] args) throws IOException, TimeoutException {
    int threadSize = Integer.parseInt(System.getProperty("consumer.size"));
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threadSize);
    for(int i=0;i<threadSize;i++){
      scheduler.submit(new ReviewConsumer(AlbumDAO.getDao().getFactory().newConnection()));
    }
  }
}
