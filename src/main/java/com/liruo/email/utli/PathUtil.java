package com.liruo.email.utli;

import java.util.List;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

/**
 * @Author:liruo
 * @Date:2023-09-14-23:10:34
 * @Desc
 */
public class PathUtil {

  private static final String CLASSPATH = "classpath:";
  private static final String FILE = "file:";

  public static AbstractResource getSpringLikeResource(String path) {
    if (path.startsWith(CLASSPATH)) {
      return new ClassPathResource(path.substring(CLASSPATH.length()).trim());
    } else if (path.startsWith(FILE)) {
      return new FileSystemResource(path.substring(FILE.length()).trim());
    } else {
      throw new IllegalArgumentException("must has a prefix like classpath: or file:");
    }
  }

  public static String toSlash(String raw) {
    return raw.replaceAll("\\\\", "/");
  }

  public static String join(String root, String append) {
    return root + "/" + append;
  }

  public static boolean isExt(String filename, List<String> exts) {
    final String fileExt = filename.substring(filename.lastIndexOf("."));
    return exts.stream().anyMatch(fileExt::equals);
  }

  public static String baseFilename(String filename) {
    return filename.substring(0, filename.lastIndexOf("."));
  }
  public static String ext(String filename) {
    return filename.substring(filename.lastIndexOf("."));
  }
}
