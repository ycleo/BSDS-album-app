public class Album {
  private int albumID;
  private Float imageSize;
  private byte[] image;
  private String artist;
  private String title;
  private int year;

  public Album() {}

  public Album(int albumID, Float imageSize, byte[] image, String artist, String title, int year) {
    this.albumID = albumID;
    this.imageSize = imageSize;
    this.image = image;
    this.artist = artist;
    this.title = title;
    this.year = year;
  }

  public int getAlbumID() {
    return albumID;
  }

  public void setAlbumID(int albumID) {
    this.albumID = albumID;
  }

  public Float getImageSize() {
    return imageSize;
  }

  public void setImageSize(Float imageSize) {
    this.imageSize = imageSize;
  }

  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
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

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }



}
