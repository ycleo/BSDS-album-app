package MQConsumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import cs6650_assignment.Server.AlbumDAO;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReviewConsumer implements Runnable{
  private final static String QUEUE_NAME = "REVIEW_QUEUE";
  private final Connection connection;

  public ReviewConsumer(Connection connection){
    this.connection = connection;
  }

  @Override
  public void run() {
    try {
      Channel channel = connection.createChannel();
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String mes = new String(delivery.getBody(), StandardCharsets.UTF_8);
        int retry = 5;
        while (true){
          try{
            writeToDB(mes);
            break;
          }catch (SQLException e){
            if (--retry==0){
              System.out.println("Retry 5 times failed!\n");
              break;
            }
          }
        }

      };
      // process messages
      channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    } catch (Exception ex) {
      Logger.getLogger(ReviewConsumer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void writeToDB(String message) throws SQLException {
    long albumId = Long.parseLong(message.split("_")[0]);
    boolean like = Boolean.parseBoolean(message.split("_")[1]);
    AlbumDAO.getDao().reviewAlbum(albumId,like);
  }

}
