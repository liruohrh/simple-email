package com.liruo.email;

import com.liruo.email.service.Encode;
import com.liruo.email.utli.PathUtil;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author:liruo
 * @Date:2023-09-14-22:22:49
 * @Desc
 */
@ConfigurationProperties(prefix = "simple-email")
@Getter
@Setter
public class SimpleEmailProperties {
  /**
   * <p>
   *   a dir like classpath:xxxx or file:xxxx.
   *   template file must has extension of .html or .txt.
   * </p>
   * <p>
   *   default templates is in classpath:/email.
   *   if set  {@link SimpleEmailProperties#templateRootPath} will give up to use default.
   * </p>
   */
  private String templateRootPath = "classpath:/email";
  /**
   * default encode is base64.
   * if want to set charset, please set spring.mail.default-encoding(if not set, default UTF-8)
   */
  private Encode encode = Encode.BASE64;

  public void setTemplateRootPath(String templateRootPath) {
    this.templateRootPath = PathUtil.toSlash(templateRootPath);
  }

  /**
   * key is
   *  global(shared to all template)
   *  or
   *  template filename without extension(if some name, will be shared, like some filename but file type is different)
   */
  private Map<String, List<String>> variables;
  private String caffeineSpec;
}
