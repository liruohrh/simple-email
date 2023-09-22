package com.liruo.email.nos;

import com.liruo.email.utli.RegexpUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @Author:liruo
 * @Date:2023-09-13-22:44:31
 * @Desc
 */
public class TemplateVariableTest {

  @Test
  public void testFindVariable() {
    String templateRoot = "src\\main\\resources\\email\\github";
    try {
      byte[] allBytes = Files.readAllBytes(Path.of(templateRoot, "template.html"));
      Pattern pattern = Pattern.compile("\\{\\{[\\w\\s]+?\\}\\}");
      String htmlStr = new String(allBytes, StandardCharsets.UTF_8);
      List[] lists = RegexpUtil.splitAndSaveSeparator(pattern, htmlStr);
      Assertions.assertEquals(18, lists[0].size());
      Assertions.assertEquals(18, lists[1].size());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
