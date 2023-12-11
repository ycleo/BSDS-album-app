import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.AccountNotFoundException;

public class Main {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    System.out.print("Input threadGroupSize:");
    int threadGroupSize = input.nextInt();
    System.out.print("Input numThreadGroups:");
    int numThreadGroups = input.nextInt();
    System.out.print("Input delay:");
    int delay = input.nextInt();
    String url;
    while ((url = input.nextLine()).isEmpty()) {
      System.out.print("Input server uri:");
    }
    if (url.endsWith("/"))
      url = url.substring(0, url.length() - 1);

    // first 10*100 requests
    CountDownLatch firstStep = new CountDownLatch(10);
    for (int i = 0; i < 10; i++) {
      new Thread(
          new Client(url, 100, firstStep, null)).start();
    }
    firstStep.await();
    System.out.println("First 10*100 requests done!");

    Analyzer analyzer = new Analyzer("output.csv");

    // Once all 10 threads have completed, startup threadGroupSize threads, each of
    // which sends 400 POST APIs
    long startTime = System.currentTimeMillis();
    CountDownLatch total = new CountDownLatch(threadGroupSize * numThreadGroups);
    for (int i = 0; i < numThreadGroups; i++) {
      for (int j = 0; j < threadGroupSize; j++) {
        new Thread(
            new Client(url, 100, total, analyzer)).start();
      }
      if ((i + 1) % 10 == 0) {
        System.out.println("First " + (i + 1) + " Groups sent!");
      }
      Thread.sleep(delay * 1000L);
    }
    total.await();

    long endTime = System.currentTimeMillis();
    analyzer.end();
    System.out.println("Wall Time: " + (endTime - startTime) + "ms");
    System.out.println(
        "Throughput: " + (1000.0 * 400 * threadGroupSize * numThreadGroups) / (endTime - startTime)
            + "/s");
    System.out.println("Mean Response Time: " + analyzer.getPostMeanResponseTime());
    System.out.println("Median Response Time: " + analyzer.getPostMedianResponseTime());
    System.out.println("P99 Response Time: " + analyzer.getPostP99ResponseTime());
    System.out.println("Min Response Time: " + analyzer.getPostMinResponseTime());
    System.out.println("Max Response Time: " + analyzer.getPostMaxResponseTime());
    System.out.println("Success/Fail Requests: " + analyzer.successCounter.get() + "/"
        + analyzer.failCounter.get());

  }

}