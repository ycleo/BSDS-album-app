package Models;

import com.google.gson.annotations.Expose;

public class AlbumResponse {
  @Expose
  private String albumID;
  @Expose
  private String imageSize;

  public AlbumResponse(String albumID, String imageSize) {
    this.albumID = albumID;
    this.imageSize = imageSize;
  }

  public String getAlbumID() {
    return albumID;
  }

  public void setAlbumID(String albumID) {
    this.albumID = albumID;
  }

  public String getImageSize() {
    return imageSize;
  }

  public void setImageSize(String imageSize) {
    this.imageSize = imageSize;
  }
}
