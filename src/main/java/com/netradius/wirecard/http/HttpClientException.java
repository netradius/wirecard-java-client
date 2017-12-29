package com.netradius.wirecard.http;

import java.io.IOException;

/**
 * Thrown when an error occurs talking to Wirecard.
 *
 * @author Erik R. Jensen
 */
public class HttpClientException extends IOException {

  private int httpStatus;

  public HttpClientException(int httpStatus, String msg, Throwable t) {
    super(msg, t);
    this.httpStatus = httpStatus;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

}
