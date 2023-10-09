class LatencyRecord {
  private final double startTime;
  private final String requestType;
  private final double latency;
  private final int responseCode;

  public LatencyRecord(double startTime, String requestType, double latency, int responseCode) {
    this.startTime = startTime;
    this.requestType = requestType;
    this.latency = latency;
    this.responseCode = responseCode;
  }

  public double getStartTime() {
    return startTime;
  }

  public String getRequestType() {
    return requestType;
  }

  public double getLatency() {
    return latency;
  }

  public int getResponseCode() {
    return responseCode;
  }
}