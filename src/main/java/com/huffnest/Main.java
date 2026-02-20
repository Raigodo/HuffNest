package com.huffnest;

import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {
    BitReader br = new BitReader(
      "E:\\Projects\\huffnest\\src\\main\\resources\\test.txt"
    );
    for (int i = 0; i < 8; i++) {
      System.out.println("!: " + br.nextBit());
    }
    while (!br.isClosed()) {
      System.out.println("!: " + (char) br.nextByte());
    }
  }
}
