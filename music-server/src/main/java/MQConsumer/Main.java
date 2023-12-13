package MQConsumer;

import cs6650_assignment.Server.AlbumDAO;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {
  public static void main(String[] args) throws IOException, TimeoutException {
    int threadSize = Integer.parseInt(System.getProperty("consumer.size"));
    for(int i=0;i<threadSize;i++){
      new Thread(new ReviewConsumer(AlbumDAO.getDao().getFactory().newConnection())).start();
    }
  }
}
