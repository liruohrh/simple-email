package com.liruo.email;

import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author:liruo
 * @Date:2023-09-14-23:45:19
 * @Desc
 */
@SpringBootTest(classes = EmailAutoConfiguration.class)
public class PropertiesSetterTest {
  @Resource
  SimpleEmailProperties simpleEmailProperties;
  @Test
  public void test(){
    System.out.println(simpleEmailProperties);
  }
}
