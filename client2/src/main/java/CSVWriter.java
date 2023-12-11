import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class CSVWriter implements Runnable {

  private final String output_filename;
  private final BlockingQueue<String> toWrite;

  private boolean running;

  public CSVWriter(String output_filename, BlockingQueue<String> toWrite) {
    this.output_filename = output_filename;
    this.toWrite = toWrite;
    this.running = true;
  }

  public boolean isRunning() {
    return running;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }

  @Override
  public void run() {
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(output_filename, false));
      while (running) {
        String line = toWrite.poll(3, TimeUnit.SECONDS);
        if (line != null)
          writer.write(line);
      }

      writer.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
