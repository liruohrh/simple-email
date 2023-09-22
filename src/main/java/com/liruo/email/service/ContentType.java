package com.liruo.email.service;

import lombok.AllArgsConstructor;

/**
 * @Author:liruo
 * @Date:2023-09-15-21:11:51
 * @Desc
 */
@AllArgsConstructor
public enum ContentType {
  TEXT_PLAIN("text/plain"),
  TEXT_HTML("text/html");
  public final String value;
  public String withCharset(String charset){
    return value + ";charset=" + charset;
  }
}
