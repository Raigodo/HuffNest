package com.huffnest;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

  public static void main(String[] args) throws IOException {
    new HuffNestFile(
      Paths.get("C:\\Users\\valte\\Desktop\\test1\\test2.zip"),
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\temp")
    ).compress(1);

    new HuffNestFile(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\temp"),
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test2.zip")
    ).decompress();
  }
}
