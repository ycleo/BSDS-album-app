import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DefaultApi;

import java.io.File;
import java.util.*;

public class DefaultApiExample {

  public static void main(String[] args) {

    DefaultApi apiInstance = new DefaultApi();
    String albumID = "albumID_example"; // String | path  parameter is album key to retrieve
    try {
      AlbumInfo result = apiInstance.getAlbumByKey(albumID);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#getAlbumByKey");
      e.printStackTrace();
    }

    File image = new File("./image/nmtb.png"); // File |
    AlbumsProfile profile = new AlbumsProfile(); // AlbumsProfile |
    try {
      ImageMetaData result = apiInstance.newAlbum(image, profile);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#newAlbum");
      e.printStackTrace();
    }
  }
}

