package Models;

import com.google.gson.annotations.Expose;

public class Status {
  @Expose
  private boolean success;
  @Expose
  private String msg;

  public Status(boolean success, String msg) {
    this.success = success;
    this.msg = msg;
  }

  public Status() {

  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
