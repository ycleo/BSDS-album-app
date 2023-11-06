import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;

public class AlbumDAO {
  private static BasicDataSource dataSource;

  public AlbumDAO() {
    dataSource = DBCPDataSource.getDataSource();
  }

  public void createAlbum(Album newAlbum) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement = "INSERT INTO Albums (AlbumId, ImageSize, Image, Artist, Title, Year) " +
        "VALUES (?,?,?,?,?,?)";
    try {
      conn = dataSource.getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement);
      preparedStatement.setInt(1, newAlbum.getAlbumID());
      preparedStatement.setFloat(2, newAlbum.getImageSize());
      preparedStatement.setBytes(3, newAlbum.getImage());
      preparedStatement.setString(4, newAlbum.getArtist());
      preparedStatement.setString(5, newAlbum.getTitle());
      preparedStatement.setInt(6, newAlbum.getYear());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
  }

  public Album getAlbum(int albumId) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    Album album = null;

    String selectQuery = "SELECT AlbumId, ImageSize, Image, Artist, Title, Year FROM Albums WHERE AlbumId = ?";

    try {
      conn = dataSource.getConnection();
      preparedStatement = conn.prepareStatement(selectQuery);
      preparedStatement.setInt(1, albumId);

      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        album = new Album();
        album.setAlbumID(resultSet.getInt("AlbumId"));
        album.setImageSize(resultSet.getFloat("ImageSize"));
        album.setImage(resultSet.getBytes("Image"));
        album.setArtist(resultSet.getString("Artist"));
        album.setTitle(resultSet.getString("Title"));
        album.setYear(resultSet.getInt("Year"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }

    return album;
  }
}
