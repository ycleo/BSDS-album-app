import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.AccountNotFoundException;

public class Main {
  private static final int INITIAL_THREAD_COUNT = 10;

  public static void main(String[] args) {
    if (args.length != 4) {
      System.out.println("Missing Parameters: <threadGroupSize> <numThreadGroups> <delay> <IPAddr>");
      System.exit(1);
    }

    int threadGroupSize = Integer.parseInt(args[0]);
    int numThreadGroups = Integer.parseInt(args[1]);
    int delay = Integer.parseInt(args[2]);
    String ipAddr = args[3];

    // Initial phase with 10 threads
    CountDownLatch initialLatch = new CountDownLatch(INITIAL_THREAD_COUNT);
    runThreads(INITIAL_THREAD_COUNT, ipAddr, 100, initialLatch);
    double startTime = System.currentTimeMillis();

    try {
      initialLatch.await(); // Wait for all initial threads to complete
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("Initial phase done");

    // Thread groups
    CountDownLatch groupLatch = new CountDownLatch(threadGroupSize * numThreadGroups);
    for (int group = 0; group < numThreadGroups; group++) {
      runThreads(threadGroupSize, ipAddr, 1000, groupLatch);
      try {
        Thread.sleep(delay * 1000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    try {
      groupLatch.await(); // Wait for all threads to complete
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    double endTime = System.currentTimeMillis();
    // Calculate and output results
    int requestCount = (INITIAL_THREAD_COUNT * 100 + threadGroupSize * numThreadGroups * 1000) * 2;
    double wallTime = (endTime - startTime) / 1000;
    System.out.println("Request Count: " + requestCount + " requests (GET+POST).");
    System.out.println("Wall Time: " + wallTime + " seconds");
    System.out.println("Throughput: " + (double)requestCount / wallTime + " requests/second");
  }


  private static void runThreads(int numThreads, String ipAddr, int numRequests, CountDownLatch latch) {
    for (int i = 0; i < numThreads; i++) {
      Thread albumThread = new Thread(new AlbumThread(ipAddr, numRequests, latch));
      albumThread.start();
    }
  }

}

