package com.liruo.email.nos;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * @Author:liruo
 * @Date:2023-06-25-21:32:22
 * @Desc
 */
public class ThymeleafTest {
  @Test
  public void testClassLoaderResource(){
    TemplateEngine e = new TemplateEngine();
    ClassLoaderTemplateResolver tr = new ClassLoaderTemplateResolver(getClass().getClassLoader());
    tr.setPrefix("/email/");
    tr.setSuffix(".html");
    e.setTemplateResolver(tr);

    Context context = new Context();
    context.setVariable("a", "123");
    Assertions.assertNotNull(e.process("github/template", context));
  }
  @Test
  public void testText(){
    TemplateEngine e = new TemplateEngine();
    Context context = new Context();
    context.setVariable("a", "123");
    Assertions.assertEquals("<div>123</div>", e.process("<div>[[${a}]]</div>", context));
  }
}
