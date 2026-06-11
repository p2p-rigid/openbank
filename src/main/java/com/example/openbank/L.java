package com.example.openbank;

public final class L {

  private L() {}

  public static final class Logging {
    public static final String TRACE_ID = "trace_id";
    public static final String PARENT_SPAN_ID = "parent_span_id";
    public static final String SPAN_ID = "span_id";
    public static final String CLIENT_ID = "clientId";
    public static final String DEFAULT_SPAN_ID = "0000000000000000";

    private Logging() {}
  }
}
