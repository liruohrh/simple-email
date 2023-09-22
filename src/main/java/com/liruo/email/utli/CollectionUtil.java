package com.liruo.email.utli;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.List;
import org.springframework.util.CollectionUtils;

/**
 * @Author:liruo
 * @Date:2023-09-14-23:34:22
 * @Desc
 */
public class CollectionUtil {

  /**
   * 
   * @param listOrNull value like k=v
   */
  public static ImmutableMap<String,String> toImmutableMap(List<String> listOrNull) {
    if(CollectionUtils.isEmpty(listOrNull)){
      return ImmutableMap.of();
    }
    Builder<String, String> builder = ImmutableMap.<String, String>builder();
    for (String v : listOrNull) {
      int equalMarkIndex = v.indexOf("=");
      builder.put(v.substring(0, equalMarkIndex), v.substring(equalMarkIndex+1));
    }
    return builder.build();
  }

}
