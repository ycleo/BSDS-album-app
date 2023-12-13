package Models;

import com.google.gson.annotations.Expose;

public class ReviewResponse {
  @Expose
  private String likes;
  @Expose
  private String dislikes;

  public ReviewResponse(String likes, String dislikes) {
    this.likes = likes;
    this.dislikes = dislikes;
  }

  public String getLikes() {
    return likes;
  }

  public void setLikes(String likes) {
    this.likes = likes;
  }

  public String getDislikes() {
    return dislikes;
  }

  public void setDislikes(String dislikes) {
    this.dislikes = dislikes;
  }
}
