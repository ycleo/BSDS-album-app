import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

public class Client implements Runnable {

  private final String url;
  private final int times;
  private final CountDownLatch latch;

  private final Analyzer analyzer;
  private Gson gson;

  public Client(String url, int times, CountDownLatch latch, Analyzer analyzer) {
    this.url = url;
    this.times = times;
    this.latch = latch;
    this.analyzer = analyzer;
    this.gson = new Gson();
  }

  public PostMethod generateAlbumPostMethod(String url) {
    PostMethod postMethod = new PostMethod(url);
    FilePart filePart = null;
    try {
      File imageFile = new File(
          Objects.requireNonNull(Client.class.getClassLoader().getResource("nmtb.png")).getPath());
      filePart = new FilePart("image", imageFile);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    String profile_str = "{\"artist\":\"aa\",\"title\":\"tt\",\"year\":\"2000\"}";
    StringPart profilePart = new StringPart("profile", profile_str, "utf-8");
    Part[] parts = { profilePart, filePart };

    MultipartRequestEntity multipartRequestEntity = new MultipartRequestEntity(parts,
        postMethod.getParams());
    postMethod.setRequestEntity(multipartRequestEntity);
    return postMethod;
  }
  public int executeWithRetry(HttpClient httpClient, HttpMethod method) throws IOException {
    int statusCode = httpClient.executeMethod(method);
    int retryCount = 1;
    while (statusCode != HttpStatus.SC_OK && statusCode!= HttpStatus.SC_CREATED) {
      if (retryCount == 5) {
        System.err.println("Method failed: " + method.getStatusLine());
        break;
      }
      statusCode = httpClient.executeMethod(method);
      retryCount += 1;
    }
    return statusCode;
  }

  public void run() {
    // System.out.println(Thread.currentThread() + "is running");
    HttpClient httpClient = new HttpClient();
    PostMethod postMethod = generateAlbumPostMethod(url + "/albums");

    for (int i = 0; i < times; i++) {

      try {
        long postStart = System.currentTimeMillis();
        int statusCode = executeWithRetry(httpClient, postMethod);
        long postDone = System.currentTimeMillis();
        if (analyzer != null) {
          analyzer.addRecord(postStart, "POST", postDone - postStart, statusCode);
        }
        String res_str = new String(postMethod.getResponseBodyAsStream().readAllBytes());
        AlbumResponse ar = gson.fromJson(res_str, AlbumResponse.class);
        String generatedKey = ar.getAlbumID();

        PostMethod likeMethod = new PostMethod(url+"/review/like/"+generatedKey);
        PostMethod dislikeMethod = new PostMethod(url+"/review/dislike/"+generatedKey);

        for(int j=0;j<2;j++){
          postStart = System.currentTimeMillis();
          statusCode = executeWithRetry(httpClient, likeMethod);
          postDone = System.currentTimeMillis();
          if (analyzer != null) {
            analyzer.addRecord(postStart, "POST", postDone - postStart, statusCode);
          }
        }

        postStart = System.currentTimeMillis();
        statusCode = executeWithRetry(httpClient, dislikeMethod);
        postDone = System.currentTimeMillis();
        if (analyzer != null) {
          analyzer.addRecord(postStart, "POST", postDone - postStart, statusCode);
        }


      } catch (Exception e) {
        System.err.println("Fatal protocol violation: " + e.getMessage());
        e.printStackTrace();
      }
    }

    // System.out.println(Thread.currentThread() + "is done");
    latch.countDown();
  }


  public static void main(String[] args) throws InterruptedException {


}
