import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsProfile;
import io.swagger.client.model.ImageMetaData;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
  private static final String http = "http://";
  private static final String port = ":8080/music-server_war";
  private static final int INITIAL_THREAD_COUNT = 10;
  private static String albumID; // String | path  parameter is album key to retrieve
  private static File image; // File |
  private static AlbumsProfile profile; // AlbumsProfile |

  public static void main(String[] args) {
    if (args.length != 4) {
      System.out.println("Missing Parameters: <threadGroupSize> <numThreadGroups> <delay> <IPAddr>");
      System.exit(1);
    }

    int threadGroupSize = Integer.parseInt(args[0]);
    int numThreadGroups = Integer.parseInt(args[1]);
    int delay = Integer.parseInt(args[2]);
    String ipAddr = args[3];

    albumID = "albumID_example"; // String | path  parameter is album key to retrieve
    image = new File("./image/nmtb.png"); // File |
    profile = new AlbumsProfile(); // AlbumsProfile |

    double startTime = System.currentTimeMillis();

    // Initial phase with 10 threads
    List<Thread> initialThreads = runThreads(INITIAL_THREAD_COUNT, ipAddr, 100);
    // Wait for the initial threads to complete
    waitForThreadsCompletion(initialThreads);
    // Thread groups
    List<List<Thread>> allThreadGroups = new ArrayList<>();

    // Thread groups
    for (int group = 0; group < numThreadGroups; group++) {
      // Thread group phase with specified size
      List<Thread> groupThreads = runThreads(threadGroupSize, ipAddr, 1000);
      allThreadGroups.add(groupThreads);

      // Delay before starting the next thread group
      try {
        Thread.sleep(delay * 1000L); // Convert seconds to milliseconds
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    // Wait for all threads to complete after all thread groups
    for (List<Thread> groupThreads : allThreadGroups) {
      waitForThreadsCompletion(groupThreads);
    }

    double endTime = System.currentTimeMillis();
    // Calculate and output results
    int totalThreadCount = allThreadGroups.size() * allThreadGroups.get(0).size();
    int requestCount = (INITIAL_THREAD_COUNT * 100 + totalThreadCount * 1000) * 2;
//    System.out.println(INITIAL_THREAD_COUNT + totalThreadCount);
    double wallTime = (endTime - startTime) / 1000;
    System.out.println("Request Count: " + requestCount + " requests (GET+POST).");
    System.out.println("Wall Time: " + wallTime + " seconds");
    System.out.println("Throughput: " + (double)requestCount / wallTime + " requests/second");
  }


    private static List<Thread> runThreads(int numThreads, String ipAddr, int numRequests) {
      List<Thread> threads = new ArrayList<>();

      for (int i = 0; i < numThreads; i++) {
        Thread thread = new Thread(() -> {
          for (int j = 0; j < numRequests; j++) {
            DefaultApi apiInstance = new DefaultApi();
            ApiClient apiClient = apiInstance.getApiClient();
            String basePath = http + ipAddr + port;
            apiClient.setBasePath(basePath);
            // Make POST API call
            httpPostAlbum(apiInstance, image, profile);
            // Make GET API call
            httpGetAlbum(apiInstance, albumID);
          }
        });
        threads.add(thread);
      }

      // Start threads
      for (Thread thread : threads) {
        thread.start();
      }

      return threads;
    }

  public static void httpGetAlbum(DefaultApi apiInstance, String albumID) {
    try {
      AlbumInfo result = apiInstance.getAlbumByKey(albumID);
//      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#getAlbumByKey");
      e.printStackTrace();
    }
  }

  public static void httpPostAlbum(DefaultApi apiInstance, File image, AlbumsProfile profile) {
    try {
      ImageMetaData result = apiInstance.newAlbum(image, profile);
//      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#newAlbum");
      e.printStackTrace();
    }
  }

  private static void waitForThreadsCompletion(List<Thread> threads) {
    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}

