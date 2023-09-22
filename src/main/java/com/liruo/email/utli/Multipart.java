package com.liruo.email.utli;

import lombok.AllArgsConstructor;

/**
 * @Author:liruo
 * @Date:2023-09-16-17:18:15
 * @Desc
 */
@AllArgsConstructor
public enum Multipart {
  MIXED("mixed"),
  ALTERNATIVE("alternative"),
  RELATED("related"),
  ;
  public final String value;
}
