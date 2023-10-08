import java.util.concurrent.CountDownLatch;
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

    double startTime = System.currentTimeMillis();

    // Initial phase with 10 threads
    CountDownLatch initialLatch = runThreads(INITIAL_THREAD_COUNT, ipAddr, 100);

    try {
      // Wait for all initial threads to complete
      initialLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // Thread groups
    for (int group = 0; group < numThreadGroups; group++) {
      CountDownLatch groupLatch = runThreads(threadGroupSize, ipAddr, 1000);
      try {
        // Wait for all threads in the current group to start
//        groupLatch.await();
        Thread.sleep(delay * 1000L); // Convert to milliseconds
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    double endTime = System.currentTimeMillis();
    // Calculate and output results
    int requestCount = (INITIAL_THREAD_COUNT * 100 + threadGroupSize * numThreadGroups * 1000) * 2;
    double wallTime = (endTime - startTime) / 1000;
    System.out.println("Request Count: " + requestCount + " requests (GET+POST).");
    System.out.println("Wall Time: " + wallTime + " seconds");
    System.out.println("Throughput: " + (double)requestCount / wallTime + " requests/second");
  }


  private static CountDownLatch runThreads(int numThreads, String ipAddr, int numRequests) {
    CountDownLatch latch = new CountDownLatch(numThreads);

    for (int i = 0; i < numThreads; i++) {
      AlbumThread albumThread = new AlbumThread(ipAddr, numRequests, latch);
      albumThread.run();
    }

    return latch;
  }

}

