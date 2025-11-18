package com.codereview.runners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileCollector {

  public static List<File> collectJavaFiles(File root){
    List<File> files = new ArrayList<>();
    collect(root, files);
    return files;
  }

  private static void collect(File dir,List<File>out){
    if(dir == null || !dir.exists())
      return;
    if(dir.isDirectory()){
      String name = dir.getName();
      if(name.equals("target") || name .equals(".git"))
        return;
      File[] children = dir.listFiles();
      if(children == null)
        return;
      for(File child : children){
        collect(child, out);
      }
    }else if(dir.isFile() && dir.getName().endsWith(".java")){
      out.add(dir);
    }
  }
}
