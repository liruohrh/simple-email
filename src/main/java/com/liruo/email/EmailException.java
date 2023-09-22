package com.liruo.email;

/**
 * @Author:liruo
 * @Date:2023-06-30-13:54:34
 * @Desc
 */
public class EmailException extends RuntimeException{
  public EmailException(String message) {
    super(message);
  }

  public EmailException(String message, Throwable cause) {
    super(message, cause);
  }
}
