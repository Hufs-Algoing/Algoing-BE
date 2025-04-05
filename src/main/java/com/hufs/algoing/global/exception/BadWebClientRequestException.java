package com.hufs.algoing.global.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class BadWebClientRequestException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = -4175247870639746104L;

  private final int statusCode;

  public BadWebClientRequestException(int statusCode, String msg) {
    super(msg);
    this.statusCode = statusCode;
  }

}
