package Server;

import com.rabbitmq.client.ConnectionFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cs6650_assignment.MQConsumer.RMQChannelFactory;
import cs6650_assignment.MQConsumer.RMQChannelPool;
import cs6650_assignment.Models.Album;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AlbumDAO {
  private final String DB_URL = System.getProperty("db.url");
  //localhost:3306/xxx
  private final String DB_USER = System.getProperty("db.username");
  private final String DB_PASSWORD = System.getProperty("db.password");
  private final String MQ_URL = System.getProperty("mq.url");
  private final int MQ_PORT = Integer.parseInt(System.getProperty("mq.port"));
  private final String MQ_USER = System.getProperty("mq.user");
  private final String MQ_PASSWORD = System.getProperty("mq.password");

  private final String MQ_VHOST = System.getProperty("mq.vhost");
//  private final String DB_URL = "jdbc:mysql://localhost:3306/cs6650";
//  //localhost:3306/xxx
//  private final String DB_USER = "cs6650";
//  private final String DB_PASSWORD = "cs66506650";
//private final String host = "localhost";
  private final int consumerSize = 10;
  private final String queueName = "REVIEW_QUEUE";
  private final HikariDataSource dataSource;
  private static final AlbumDAO dao = new AlbumDAO();

  private final RMQChannelPool channelPool;

  private ConnectionFactory factory;

  public AlbumDAO(){
    dataSource = setupHikari();
    channelPool = setupRabbitMQ(consumerSize);
  }

  public RMQChannelPool setupRabbitMQ(int consumerSize){
    factory = new ConnectionFactory();
    factory.setHost(MQ_URL);
    factory.setPort(MQ_PORT);
    factory.setUsername(MQ_USER);
    factory.setPassword(MQ_PASSWORD);
    factory.setVirtualHost(MQ_VHOST);
    try{
      com.rabbitmq.client.Connection conn = factory.newConnection();
      return new RMQChannelPool(consumerSize,new RMQChannelFactory(conn));
    }catch (Exception e){
      throw new RuntimeException("RabbitMQ connection error!",e);
    }
  }

  public HikariDataSource setupHikari(){
    HikariConfig config = new HikariConfig();
    config.setDriverClassName("com.mysql.jdbc.Driver");
    config.setJdbcUrl(DB_URL);
    config.setUsername(DB_USER);
    config.setPassword(DB_PASSWORD);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "500");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.addDataSourceProperty("useServerPrepStmts", "true");
    HikariDataSource ret;
    try{
      ret = new HikariDataSource(config);
      return ret;
    }catch (Exception e){
      throw new RuntimeException("MySQL server connection error!",e);
    }
  }

  public ConnectionFactory getFactory() {
    return factory;
  }

  public long createAlbum(Album album){
    //Connection conn = getConnection()
    try(Connection conn = dataSource.getConnection()){
      PreparedStatement s = conn.prepareStatement("INSERT INTO Albums(Artist,Title,Year,Image,likes,dislikes) VALUES(?,?,?,?,0,0)",
          Statement.RETURN_GENERATED_KEYS);
      s.setString(1,album.getArtist());
      s.setString(2,album.getTitle());
      s.setString(3,album.getYear());
      s.setBytes(4,album.getImageBytes());
      s.executeUpdate();
      ResultSet rs = s.getGeneratedKeys();
      if(rs.next()){
        return rs.getLong(1);
      }
    }catch (Exception e){
      System.out.println("Error during createAlbum: " + e.getMessage());
      e.printStackTrace();
    }
    return -1;
  }

  public Album queryAlbum(long albumId){
    //Connection conn = getConnection()
    try(Connection conn = dataSource.getConnection()){
      PreparedStatement s = conn.prepareStatement("SELECT Id,Artist,Title,Year,Image FROM Albums WHERE Id=?");
      s.setLong(1,albumId);
      ResultSet rs = s.executeQuery();
      if(rs.next()){
        return new Album(rs.getLong(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getBytes(5));
      }
    }catch (Exception e){
      System.out.println("Error during queryAlbum: " + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  public boolean reviewAlbum(long albumId, boolean like) throws SQLException {
    try(Connection conn = dataSource.getConnection()){
      PreparedStatement s = conn.prepareStatement("UPDATE Albums SET dislikes = dislikes + 1 WHERE Id = ?");
      if(like){
        s = conn.prepareStatement("UPDATE Albums SET likes = likes + 1 WHERE Id = ?");
      }

      s.setLong(1,albumId);
      int ret = s.executeUpdate();
      if(ret!=0){
        return true;
      }
    }catch (Exception e){
      System.out.println("Error during reviewAlbum: " + e.getMessage());
      throw e;
    }
    return false;
  }

  public static AlbumDAO getDao(){
    return dao;
  }

  public int getConsumerSize() {
    return consumerSize;
  }

  public RMQChannelPool getChannelPool() {
    return channelPool;
  }

  public String getQueueName() {
    return queueName;
  }
}
