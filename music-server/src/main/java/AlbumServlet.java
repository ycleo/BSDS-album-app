import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import model.AlbumInfo;

@WebServlet(name = "AlbumServlet", value = "/albums/*")
public class AlbumServlet extends HttpServlet {

  private final Gson gson = new Gson();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("text/plain");
//     Handle GET requests to retrieve album data by ID
    String pathInfo = req.getPathInfo();

    if (pathInfo != null && pathInfo.length() > 1) {
      // Extract the album ID from the URL (assuming the URL format is /albums/{albumId})
      int albumId = Integer.parseInt(pathInfo.substring(1));

      // Use your AlbumDAO to retrieve album information
      AlbumDAO albumDAO = new AlbumDAO();
      Album album = albumDAO.getAlbum(albumId);
      AlbumInfo albumInfo = new AlbumInfo("Sex Pistols", "Never Mind The Bollocks!", "1977");

      // Send the retrieved album data as JSON response
      res.setStatus(HttpServletResponse.SC_OK);
      sendJsonResponse(res, albumInfo);

    } else {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("text/plain");
    ImageMetaData imageMetaData = new ImageMetaData("fixed-album-ID", "fixed-image-size");
//     1. Read the image file and store it as a byte array
    byte[] imageData = readImageFile("src/main/resources/album-image.jpg");

    // 2. Create an Album object
    Album newAlbum = new Album();
    newAlbum.setAlbumID(1); // Set the album ID as needed
    newAlbum.setImageSize(123.45f); // Set image size
    newAlbum.setArtist("Artist Name");
    newAlbum.setTitle("Album Title");
    newAlbum.setYear(2023);
    newAlbum.setImage(imageData); // Set the image data

    // 3. Use your AlbumDAO to insert the new album data into the database
    AlbumDAO albumDAO = new AlbumDAO();
    albumDAO.createAlbum(newAlbum);

    res.setStatus(HttpServletResponse.SC_OK);
    sendJsonResponse(res, imageMetaData);
  }

  // Helper method to read an image file and return it as a byte array
  private static byte[] readImageFile(String filePath) {
    try (InputStream inputStream = new FileInputStream(filePath)) {
      byte[] buffer = new byte[inputStream.available()];
      inputStream.read(buffer);
      return buffer;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private void sendJsonResponse(HttpServletResponse res, Object data) throws IOException {
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    PrintWriter out = res.getWriter();
    out.print(this.gson.toJson(data));
    out.flush();
  }

}
