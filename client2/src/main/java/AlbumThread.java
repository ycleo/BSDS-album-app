import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsProfile;
import io.swagger.client.model.ImageMetaData;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AlbumThread implements Runnable {
  private static final String http = "http://";
  private static final String port = ":8080/music_server_war_exploded"; // Java Server URL
//  private static final String port = ":1234"; // Go URL
  private final DefaultApi apiInstance;
  private final int numRequests;
  private final CountDownLatch latch;
  private final String albumID;
  private final File image;
  private final AlbumsProfile profile;
  private List<LatencyRecord> latencyRecords;

  public AlbumThread(String ipAddr, int numRequests, CountDownLatch latch) {
    this.numRequests = numRequests;
    this.latch = latch;

    this.apiInstance = new DefaultApi();
    this.apiInstance.getApiClient().setBasePath(http + ipAddr + port);
    this.albumID = "albumID_example";
    this.image = new File("./image/nmtb.png");
    this.profile = new AlbumsProfile("artistName", "albumTitle", "2024");
    this.latencyRecords = new ArrayList<>();
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
    double startTime = System.currentTimeMillis();
    int responseCode;
    try {
      AlbumInfo result = apiInstance.getAlbumByKey(albumID);
      responseCode = 200;
//      System.out.println(result);
    } catch (ApiException e) {
      responseCode = 404;
      System.err.println("Exception when calling DefaultApi#getAlbumByKey");
      e.printStackTrace();
    }
    double endTime = System.currentTimeMillis();
    recordLatency("GET", startTime, endTime, responseCode);
  }

  private void httpPostAlbum(DefaultApi apiInstance, File image, AlbumsProfile profile) {
    double startTime = System.currentTimeMillis();
    int responseCode;
    try {
      ImageMetaData result = apiInstance.newAlbum(image, profile);
      responseCode = 201;
    } catch (ApiException e) {
      responseCode = 400;
      System.err.println("Exception when calling DefaultApi#newAlbum");
      e.printStackTrace();
    }
    double endTime = System.currentTimeMillis();
    recordLatency("POST", startTime, endTime, responseCode);
  }

  private void recordLatency(String requestType, double startTime, double endTime, int responseCode) {
    double latency = endTime - startTime;
    LatencyRecord record = new LatencyRecord(startTime, requestType, latency, responseCode);
    latencyRecords.add(record);
  }
  public List<LatencyRecord> getLatencyRecords() {
    return latencyRecords;
  }
}

