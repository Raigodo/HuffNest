package com.huffnest;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

  public static void main(String[] args) throws IOException {
    new HuffNestFile(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test.txt"),
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test2")
    ).compress(1);

    new HuffNestFile(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test2"),
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test3.txt")
    ).decompress();
  }
}
