import com.google.gson.Gson;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "AlbumServlet", value = "/AlbumServlet")
public class AlbumServlet extends HttpServlet {

  private final Gson gson = new Gson();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("text/plain");

    AlbumInfo albumInfo = new AlbumInfo("Sex Pistols", "Never Mind The Bollocks!", "1977");
    res.setStatus(HttpServletResponse.SC_OK);
    sendJsonResponse(res, albumInfo);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("text/plain");

    ImageMetaData imageMetaData = new ImageMetaData("fixed-album-ID", "fixed-image-size");
    res.setStatus(HttpServletResponse.SC_OK);
    sendJsonResponse(res, imageMetaData);
  }

  private void sendJsonResponse(HttpServletResponse res, Object data) throws IOException {
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    PrintWriter out = res.getWriter();
    out.print(this.gson.toJson(data));
    out.flush();
  }

}
