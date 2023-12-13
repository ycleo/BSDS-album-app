package Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import cs6650_assignment.Models.ReviewResponse;
import cs6650_assignment.Models.Status;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ReviewServlet", value = "/review/*")
public class ReviewServlet extends HttpServlet {
  private final AlbumDAO albumDAO = AlbumDAO.getDao();
  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("application/json; charset=UTF-8");
    String urlPath = req.getPathInfo();
    try {
      if (urlPath == null || urlPath.length()==0) {
        throw new IllegalArgumentException();
      }
      String[] urlParts = urlPath.split("/");
      String likeOrNot = urlParts[1].toLowerCase();
      long album_id = Long.parseLong(urlParts[2]);
      boolean ret;
      if(likeOrNot.equals("like")){
        ret = writeToMQ(album_id,true);
      } else if (likeOrNot.equals("dislike")) {
        ret = writeToMQ(album_id,false);
      }else {
        throw new IllegalArgumentException();
      }
      if(ret){
        res.setStatus(201);
        res.getOutputStream().print("Write successful");
        res.getOutputStream().flush();
      }else{
        Status s = new Status(false,"Album not found");
        res.setStatus(404);
        res.getOutputStream().print(gson.toJson(s));
        res.getOutputStream().flush();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      Status status = new Status(false,"invalid inputs");
      res.setStatus(400);
      res.getOutputStream().print(gson.toJson(status));
      res.getOutputStream().flush();
    }
  }

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
      ReviewResponse ret = albumDAO.queryAlbumReview(album_id);
      if(ret!=null){
        res.setStatus(200);
        res.getOutputStream().print(gson.toJson(ret));
        res.getOutputStream().flush();
      }else{
        res.setStatus(404);
        Status s = new Status(false,"Album not found");
        res.getOutputStream().print(gson.toJson(s));
        res.getOutputStream().flush();
      }

    }catch (Exception e){
      res.setStatus(400);
      Status status = new Status(false,"invalid inputs");
      res.getOutputStream().print(gson.toJson(status));
      res.getOutputStream().flush();
    }
  }
  private boolean writeToMQ(long albumId, boolean like) {
    try {
      // channel per thread
      Channel channel = albumDAO.getChannelPool().borrowObject();
      channel.queueDeclare(albumDAO.getQueueName(), true, false, false, null);
      String message = albumId+"_"+like;
      channel.basicPublish("", albumDAO.getQueueName(), null, message.getBytes(StandardCharsets.UTF_8));
      albumDAO.getChannelPool().returnObject(channel);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
