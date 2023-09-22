package com.liruo.email.template;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author:liruo
 * @Date:2023-09-14-21:36:58
 * @Desc
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TemplateData {

  /**
   * when render to init StringBuilder
   */
  private int staticContentLength;
  private ImmutableList<String> contentParts;
  private ImmutableList<String> variables;
}
