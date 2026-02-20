package com.huffnest;

import com.huffnest.bytetree.ByteTree;
import com.huffnest.bytetree.ByteTree.TreePathDirection;
import com.huffnest.bytetree.ByteTreeBuilder;
import com.huffnest.io.BitReader;
import com.huffnest.io.BitWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) throws IOException {
    BitReader br = new BitReader(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test.txt")
    );

    ByteTreeBuilder builder = new ByteTreeBuilder();

    while (br.hasMoreBytes()) {
      builder.appendNextByte(br.nextByte());
    }

    ByteTree tree = builder.build();

    BitWriter bw = new BitWriter(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test2.txt")
    );

    br = new BitReader(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test.txt")
    );

    while (br.hasMoreBytes()) {
      byte b = br.nextByte();
      TreePathDirection[] path = tree.getPathToByte(b);

      for (int i = 0; i < path.length; i++) {
        bw.pushBit((byte) (path[i] == TreePathDirection.LEFT ? 0 : 1));
        if (i == path.length - 1) {
          bw.pushBit((byte) 1);
        } else {
          bw.pushBit((byte) 0);
        }
      }
    }
    bw.close();

    //retrieve

    br = new BitReader(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test2.txt")
    );
    bw = new BitWriter(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test3.txt")
    );

    writeLoop: while (br.hasMoreBits()) {
      List<TreePathDirection> path = new ArrayList<>();
      while (true) {
        if (!br.hasMoreBits()) break writeLoop;
        path.add(
          br.nextBit() == 0 ? TreePathDirection.LEFT : TreePathDirection.RIGHT
        );
        if (!br.hasMoreBits()) break writeLoop;
        boolean isEndBit = br.nextBit() == 1 ? true : false;
        if (isEndBit) {
          break;
        }
      }
      byte value = tree.getByteAtPath(
        (TreePathDirection[]) path.toArray(new TreePathDirection[0])
      );
      bw.pushByte(value);
    }
  }
}
