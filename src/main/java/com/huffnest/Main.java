package com.huffnest;

import com.huffnest.ByteTree.TreePathDirection;
import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {
    BitReader br = new BitReader(
      "E:\\Projects\\huffnest\\src\\main\\resources\\test.txt"
    );

    ByteTreeBuilder builder = new ByteTreeBuilder();

    while (!br.isClosed()) {
      builder.appendNextByte(br.nextByte());
    }

    ByteTree tree = builder.build();

    TreePathDirection[] path = tree.getPathToByte((byte) '4');

    for (TreePathDirection direction : path) {
      System.out.println(direction);
    }

    System.out.println((char) tree.getByteAtPath(path));
  }
}
