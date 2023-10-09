import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.AccountNotFoundException;

public class Main {
  private static final int INITIAL_THREAD_COUNT = 10;
  private static final String CSV_FILE_PATH = "./plot.csv";
  private static final double INTERVAL = 1;

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
    List<AlbumThread> initialThreads = runThreads(INITIAL_THREAD_COUNT, ipAddr, 100, initialLatch);
    List<LatencyRecord> allLatencyRecords = new ArrayList<>(collectLatencyRecords(initialThreads));

    double startTime = System.currentTimeMillis();

    try {
      initialLatch.await(); // Wait for all initial threads to complete
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("Initial phase done");

    // Thread groups
    CountDownLatch groupLatch = new CountDownLatch(threadGroupSize * numThreadGroups);
    List<AlbumThread> groupThreads = new ArrayList<>();

    for (int group = 0; group < numThreadGroups; group++) {
      List<AlbumThread> threads = runThreads(threadGroupSize, ipAddr, 1000, groupLatch);
      groupThreads.addAll(threads);
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

    // After all threads are completed, collect and process latency records
    for (AlbumThread albumThread : groupThreads) {
      allLatencyRecords.addAll(albumThread.getLatencyRecords());
    }

    // Calculate and display latency statistics
    calculateAndDisplayStatistics(allLatencyRecords);

    // Calculate and export throughput data to CSV
    calculateAndExportThroughputCSV(allLatencyRecords, wallTime);
  }


  private static List<AlbumThread> runThreads(int numThreads, String ipAddr, int numRequests, CountDownLatch latch) {
    List<AlbumThread> threads = new ArrayList<>();
    for (int i = 0; i < numThreads; i++) {
      AlbumThread albumThread = new AlbumThread(ipAddr, numRequests, latch);
      threads.add(albumThread);
      Thread thread = new Thread(albumThread);
      thread.start();
    }
    return threads;
  }

  private static List<LatencyRecord> collectLatencyRecords(List<AlbumThread> threads) {
    List<LatencyRecord> records = new ArrayList<>();
    for (AlbumThread thread : threads) {
      records.addAll(thread.getLatencyRecords());
    }
    return records;
  }

  private static void calculateAndDisplayStatistics(List<LatencyRecord> latencyRecords) {
    // Calculate and display statistics here
    System.out.println("Mean Response Time: " + calculateMean(latencyRecords) + " milliseconds");
    System.out.println("Median Response Time: " + calculateMedian(latencyRecords) + " milliseconds");
    System.out.println("P99 Response Time: " + calculatePercentile(latencyRecords, 99) + " milliseconds");
    System.out.println("Min Response Time: " + calculateMin(latencyRecords) + " milliseconds");
    System.out.println("Max Response Time: " + calculateMax(latencyRecords) + " milliseconds");
  }

  private static long calculateMean(List<LatencyRecord> latencyRecords) {
    if (latencyRecords.isEmpty()) {
      return 0;
    }

    long sum = 0;
    for (LatencyRecord record : latencyRecords) {
      sum += record.getLatency();
    }

    return sum / latencyRecords.size();
  }

  private static double calculateMedian(List<LatencyRecord> latencyRecords) {
    if (latencyRecords.isEmpty()) {
      return 0;
    }

    List<Double> sortedLatencies = new ArrayList<>();
    for (LatencyRecord record : latencyRecords) {
      sortedLatencies.add(record.getLatency());
    }

    Collections.sort(sortedLatencies);

    int size = sortedLatencies.size();
    if (size % 2 == 0) {
      // Even number of elements, take the average of middle two
      return (sortedLatencies.get(size / 2 - 1) + sortedLatencies.get(size / 2)) / 2;
    } else {
      // Odd number of elements, take the middle one
      return sortedLatencies.get(size / 2);
    }
  }

  private static double calculatePercentile(List<LatencyRecord> latencyRecords, int percentile) {
    if (latencyRecords.isEmpty()) {
      return 0;
    }

    List<Double> sortedLatencies = new ArrayList<>();
    for (LatencyRecord record : latencyRecords) {
      sortedLatencies.add(record.getLatency());
    }

    Collections.sort(sortedLatencies);

    int index = (int) Math.ceil((percentile / 100.0) * sortedLatencies.size()) - 1;
    return sortedLatencies.get(index);
  }

  private static double calculateMin(List<LatencyRecord> latencyRecords) {
    if (latencyRecords.isEmpty()) {
      return 0;
    }

    return Collections.min(latencyRecords, Comparator.comparing(LatencyRecord::getLatency)).getLatency();
  }

  private static double calculateMax(List<LatencyRecord> latencyRecords) {
    if (latencyRecords.isEmpty()) {
      return 0;
    }

    return Collections.max(latencyRecords, Comparator.comparing(LatencyRecord::getLatency)).getLatency();
  }

  private static void calculateAndExportThroughputCSV(List<LatencyRecord> latencyRecords, double wallTime) {
    try {
      FileWriter csvWriter = new FileWriter("./throughput.csv");
      csvWriter.append("Time (s),Throughput (req/s)\n");

      double startTime = latencyRecords.get(0).getStartTime();
      double endTime = latencyRecords.get(latencyRecords.size() - 1).getStartTime();

      int timeInterval = 1000; // 1 second interval
      int currentIndex = 0;
      int requestCount = 0;

      for (double currentTime = startTime; currentTime <= endTime; currentTime += timeInterval) {
        while (currentIndex < latencyRecords.size() &&
            latencyRecords.get(currentIndex).getStartTime() <= currentTime) {
          requestCount++;
          currentIndex++;
        }

        double timeInSeconds = (currentTime - startTime) / 1000.0;
        double throughput = (double) requestCount / timeInSeconds;

        csvWriter.append(String.valueOf(timeInSeconds)).append(",")
            .append(String.valueOf(throughput)).append("\n");
      }

      csvWriter.flush();
      csvWriter.close();

      System.out.println("Throughput data exported to throughput.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}

