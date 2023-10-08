import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsProfile;
import io.swagger.client.model.ImageMetaData;
import java.io.File;
import java.util.concurrent.CountDownLatch;

public class AlbumThread implements Runnable {
  private static final String http = "http://";
  private static final String port = ":1234";
  private final DefaultApi apiInstance;
  private final int numRequests;
  private final CountDownLatch latch;
  private final String albumID;
  private final File image;
  private final AlbumsProfile profile;

  public AlbumThread(String ipAddr, int numRequests, CountDownLatch latch) {
    this.numRequests = numRequests;
    this.latch = latch;

    this.apiInstance = new DefaultApi();
    this.apiInstance.getApiClient().setBasePath(http + ipAddr + port);
    this.albumID = "albumID_example";
    this.image = new File("./image/nmtb.png");
    this.profile = new AlbumsProfile();
  }

  @Override
  public void run() {
    for (int j = 0; j < numRequests; j++) {
      httpPostAlbum(apiInstance, image, profile);
      httpGetAlbum(apiInstance, albumID);
    }

    // Count down the latch when the thread is done
    latch.countDown();
  }

  private void httpGetAlbum(DefaultApi apiInstance, String albumID) {
    try {
      AlbumInfo result = apiInstance.getAlbumByKey(albumID);
//      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#getAlbumByKey");
      e.printStackTrace();
    }
  }

  private void httpPostAlbum(DefaultApi apiInstance, File image, AlbumsProfile profile) {
    try {
      ImageMetaData result = apiInstance.newAlbum(image, profile);
//      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#newAlbum");
      e.printStackTrace();
    }
  }
}

