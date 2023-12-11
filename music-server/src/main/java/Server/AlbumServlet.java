package Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cs6650_assignment.Models.Album;
import cs6650_assignment.Models.AlbumResponse;
import cs6650_assignment.Models.Status;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(name = "AlbumServlet", value = "/albums/*")
@MultipartConfig(fileSizeThreshold = 1024*1024*10,
    maxFileSize = 1024*1024*50,
    maxRequestSize = 1024*1024*100)
public class AlbumServlet extends HttpServlet {

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
  private final AlbumDAO albumDAO = AlbumDAO.getDao();
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("application/json; charset=UTF-8");
    String urlPath = req.getPathInfo();

    try{
      if (urlPath == null || urlPath.length()==0) {
        throw new IllegalArgumentException();
      }
      String[] urlParts = urlPath.split("/");
      long album_id = Long.parseLong(urlParts[1]);
      Album ret = albumDAO.queryAlbum(album_id);
      if(ret!=null){
        res.setStatus(200);
        res.getOutputStream().print(gson.toJson(ret));
        res.getOutputStream().flush();
      }else{
        res.setStatus(404);
        Status s = new Status(false,"album id not found!");
        res.getOutputStream().print(gson.toJson(s));
        res.getOutputStream().flush();
      }

    }catch (Exception e){
      res.setStatus(400);
      Status status = new Status(false,"invalid request");
      res.getOutputStream().print(gson.toJson(status));
      res.getOutputStream().flush();
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("application/json; charset=UTF-8");
    try {
      Part profilePart = req.getPart("profile");
      String profile_str = new String(profilePart.getInputStream().readAllBytes());
      Album album = gson.fromJson(profile_str, Album.class);
      Part imagePart = req.getPart("image");
      album.setImageBytes(imagePart.getInputStream().readAllBytes());
      long id = albumDAO.createAlbum(album);
      if(id!=-1){
        AlbumResponse albumResponse = new AlbumResponse(String.valueOf(id),String.valueOf(imagePart.getSize()));
        res.setStatus(200);
        res.getOutputStream().print(gson.toJson(albumResponse));
        res.getOutputStream().flush();
      }else{
        Status s = new Status(false,"failed to insert in database!");
        res.setStatus(400);
        res.getOutputStream().print(gson.toJson(s));
        res.getOutputStream().flush();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      Status status = new Status(false,"invalid request");
      res.setStatus(400);
      res.getOutputStream().print(gson.toJson(status));
      res.getOutputStream().flush();
    }
  }
}
