package com.liruo.email.utli;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author:liruo
 * @Date:2023-09-14-19:00:06
 * @Desc
 */
public class RegexpUtil {

  /**
   * save separator when split
   * mock {@link java.util.regex.Pattern#splitAsStream(java.lang.CharSequence)}
   *
   * @param pattern
   * @param input
   * @return [0]=contentPart, [1]=separator
   */
  @SuppressWarnings("unchecked")
  public static List<String>[] splitAndSaveSeparator(Pattern pattern, String input) {
    Matcher matcher = pattern.matcher(input);
    List<String> results = new ArrayList<>();
    List<String> separators = new ArrayList<>();
    int resultStart = 0, separatorStart = 0, separatorEnd = 0;
    while (matcher.find()) {
      separatorStart = matcher.start();
      separatorEnd = matcher.end();
      results.add(input.substring(resultStart, separatorStart));
      separators.add(input.substring(separatorStart, separatorEnd));
      resultStart = separatorEnd;
    }
    //consume the last result
    results.add(input.substring(separatorEnd));
    return new List[]{results, separators};
  }

}
