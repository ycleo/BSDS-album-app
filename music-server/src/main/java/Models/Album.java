package Models;

import com.google.gson.annotations.Expose;
import java.util.Arrays;

public class Album {
  private long id;
  @Expose
  private String artist;
  @Expose
  private String title;
  @Expose
  private String year;
  private String imagePath;
  private byte[] imageBytes;
  private int imageSize;

  public Album(String artist, String title, String year) {
    this.artist = artist;
    this.title = title;
    this.year = year;
  }

  public Album(long id, String artist, String title, String year, byte[] imageBytes) {
    this.id = id;
    this.artist = artist;
    this.title = title;
    this.year = year;
    this.imageBytes = imageBytes;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public int getImageSize() {
    return imageSize;
  }

  public void setImageSize(int imageSize) {
    this.imageSize = imageSize;
  }

  public byte[] getImageBytes() {
    return imageBytes;
  }

  public void setImageBytes(byte[] imageBytes) {
    this.imageBytes = imageBytes;
  }

  @Override
  public String toString() {
    return "Album{" +
        "id=" + id +
        ", artist='" + artist + '\'' +
        ", title='" + title + '\'' +
        ", year='" + year + '\'' +
        ", imagePath='" + imagePath + '\'' +
        ", imageBytes=" + Arrays.toString(imageBytes) +
        ", imageSize=" + imageSize +
        '}';
  }
}
