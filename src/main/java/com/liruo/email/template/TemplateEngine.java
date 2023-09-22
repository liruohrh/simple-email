package com.liruo.email.template;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.liruo.email.SimpleEmailProperties;
import com.liruo.email.utli.CollectionUtil;
import com.liruo.email.utli.PathUtil;
import com.liruo.email.utli.RegexpUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import lombok.NoArgsConstructor;
import org.springframework.core.io.AbstractResource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @Author:liruo
 * @Date:2023-09-14-21:11:46
 * @Desc
 */
@NoArgsConstructor
public class TemplateEngine {

  private static final Pattern variablePattern = Pattern.compile("\\{\\{[\\w\\s]+?\\}\\}");
  private LoadingCache<String, TemplateData> templateCache;
  private static final String DEFAULT_CAFFEINE_SPEC = "maximumSize=20,expireAfterWrite=4h";
  private static final List<String> templateFileExtensions = Arrays.asList(".html", ".txt");
  ;
  private String templateRootPath;
  private ImmutableMap<String, String> globalVariables;
  private ImmutableMap<String, ImmutableMap<String, String>> localVariables;

  /**
   * @param filename 模板名
   * @param values   必须包含该模板的全部动态变量
   * @return rendered Content
   */
  public String render(String filename, Map<String, String> values) {
    TemplateData templateData = templateCache.get(filename);
    ImmutableList<String> contentParts = templateData.getContentParts();
    ImmutableList<String> variables = templateData.getVariables();

    if(variables == null){
      return String.join("", contentParts);
    }

    StringBuilder content = new StringBuilder(
        templateData.getStaticContentLength() + variables.size() * 10);
    Iterator<String> iContentPart = contentParts.iterator();
    for (String nextVar : variables) {
      String value = values.getOrDefault(nextVar, "");
      if (value.isEmpty()) {
        throw new IllegalArgumentException(
            "hasn't the var " + nextVar + "(need in " + filename + "), varList=" + new HashSet<>(variables));
      }
      content.append(iContentPart.next())
          .append(value);
    }
    if (iContentPart.hasNext()) {
      content.append(iContentPart.next());
    }
    return content.toString();
  }

  /**
   * @param key
   * @param filename has not extension
   * @return if has not return ""
   */
  public String getVar(String key, String filename) {
    //get from global or local
    String value = globalVariables.get(key);
    if (!StringUtils.hasText(value)) {
      ImmutableMap<String, String> map = localVariables.get(filename);
      if (CollectionUtils.isEmpty(map)) {
        value = "";
      } else {
        value = map.getOrDefault(key, "");
      }
    }
    return value;
  }

  public void init(SimpleEmailProperties config) {
    //variables
    Map<String, List<String>> variables = config.getVariables();
    this.globalVariables = CollectionUtil.toImmutableMap(variables.get("global"));

    Builder<String, ImmutableMap<String, String>> builder = ImmutableMap.builder();
    for (Entry<String, List<String>> entry : variables.entrySet()) {
      if (entry.getKey().equals("global")) {
        continue;
      }
      builder.put(entry.getKey(), CollectionUtil.toImmutableMap(entry.getValue()));
    }
    this.localVariables = builder.build();

    this.templateRootPath = config.getTemplateRootPath();
    //cache
    String caffeineSpec = config.getCaffeineSpec();
    if (!StringUtils.hasText(caffeineSpec)) {
      caffeineSpec = DEFAULT_CAFFEINE_SPEC;
    }
    Caffeine<Object, Object> caffeineBuilder = Caffeine.from(caffeineSpec);
    this.templateCache = caffeineBuilder.build(filename -> {
      AbstractResource resource = PathUtil.getSpringLikeResource(
          PathUtil.join(this.templateRootPath, filename));
      return parse(resource.getFile().toPath());
    });

    //cache init data
    if (!StringUtils.hasText(this.templateRootPath)) {
      throw new IllegalArgumentException("has not templateRootPath");
    }
    AbstractResource templateRes = PathUtil.getSpringLikeResource(this.templateRootPath);
    try {
      File templateRoot = templateRes.getFile();
      if (!templateRoot.exists()) {
        throw new IllegalArgumentException("has not the dir of " + this.templateRootPath);
      } else if (!templateRoot.isDirectory()) {
        throw new IllegalArgumentException(this.templateRootPath + " is not a dir");
      }

      File[] files = templateRoot.listFiles();
      if (files == null || files.length == 0) {
        throw new IllegalArgumentException(this.templateRootPath + " has not template files");
      }

      HashMap<String, TemplateData> cache = new HashMap<>(files.length);
      for (File file : files) {
        if (file.isDirectory() || !PathUtil.isExt(file.getName(), templateFileExtensions)) {
          continue;
        }
        //separate template and variable
        cache.put(file.getName(), parse(file.toPath()));
      }
      templateCache.putAll(cache);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private TemplateData parse(Path templateP) {
    //read file and parse to contentPart and variables
    byte[] bytes;
    try {
      bytes = Files.readAllBytes(templateP);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    List<String>[] lists = RegexpUtil.splitAndSaveSeparator(
        variablePattern,
        new String(bytes, StandardCharsets.UTF_8)
    );
    List<String> parsedContentParts = lists[0];
    List<String> parsedVariables = lists[1];
    if(parsedVariables.isEmpty()){
      int staticContentLength = (int)parsedContentParts.stream().mapToLong(String::length).sum();
      return new TemplateData(
          staticContentLength,
          ImmutableList.copyOf(parsedContentParts),
          null
      );
    }

    for (int i = 0; i < parsedVariables.size(); i++) {
      String v = parsedVariables.get(i);
      parsedVariables.set(i, v.substring(2, v.length() - 2));
    }

    //set default variable value

    String baseFilename = PathUtil.baseFilename(templateP.getFileName().toString());
    //set to contentPart and remove from variables. for ArrayList will move by copy when remove, so make new list
    int staticContentLength = 0;
    boolean hasPreJoin = false;

    List<String> newContentPartList = new ArrayList<>(parsedContentParts.size());
    List<String> newVarList = new ArrayList<>(parsedVariables.size());
    Iterator<String> iContentPart = parsedContentParts.iterator();
    for (String nextVar : parsedVariables) {
      String defaultValue = getVar(nextVar, baseFilename);
      //is non static, just simply add var and add contentPart
      if (defaultValue.isEmpty()) {
        newVarList.add(nextVar);
        if (!hasPreJoin && iContentPart.hasNext()) {
          String contentPart = iContentPart.next();
          staticContentLength += contentPart.length();
          newContentPartList.add(contentPart);
        }
        hasPreJoin = false;
        continue;
      }
      //is static, join contentPart and variable
      String preContentPart;
      if(hasPreJoin){
        preContentPart = newContentPartList.get(newContentPartList.size()-1);
      }else{
        preContentPart = iContentPart.hasNext() ? iContentPart.next() : "";
      }
      String backContentPart = iContentPart.hasNext() ? iContentPart.next() : "";

      String contentPart = preContentPart + defaultValue + backContentPart;

      if(hasPreJoin){
        staticContentLength += contentPart.length() - preContentPart.length();
        newContentPartList.set(newContentPartList.size()-1, contentPart);
      }else{
        staticContentLength += contentPart.length();
        newContentPartList.add(contentPart);
      }
      hasPreJoin=true;
    }
    if (iContentPart.hasNext()) {
      String contentPart = iContentPart.next();
      newContentPartList.add(contentPart);
      staticContentLength += contentPart.length();
    }

    return new TemplateData(
        staticContentLength,
        ImmutableList.copyOf(newContentPartList),
        ImmutableList.copyOf(newVarList)
    );
  }
}
