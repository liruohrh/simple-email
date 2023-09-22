package com.liruo.email.nos;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

/**
 * @Author:liruo
 * @Date:2023-06-25-22:00:28
 * @Desc
 */
public class SpringResourceMechanismTest {
  @Test
  public void testUse(){
    ClassPathResource classPathResource = new ClassPathResource(
        "/email/verificationCodeDefault2.html");
    FileSystemResource fileSystemResource = new FileSystemResource("src/main/resources/email/verificationCodeDefault2.html");
    Assertions.assertTrue(classPathResource.exists());
    Assertions.assertTrue(fileSystemResource.exists());
  }

}
