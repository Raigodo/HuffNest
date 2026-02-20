package com.huffnest;

import com.huffnest.ByteTree.TreePathDirection;
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

    while (!br.isClosed()) {
      builder.appendNextByte(br.nextByte());
    }

    ByteTree tree = builder.build();

    BitWriter bw = new BitWriter(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test2.txt")
    );

    BitReader br2 = new BitReader(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test.txt")
    );

    while (!br2.isClosed()) {
      byte b = br2.nextByte();
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

    BitReader br3 = new BitReader(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test2.txt")
    );
    BitWriter bw2 = new BitWriter(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test3.txt")
    );

    writeLoop: while (!br3.isClosed()) {
      List<TreePathDirection> path = new ArrayList<>();
      while (true) {
        if (br3.isClosed()) break writeLoop;
        path.add(
          br3.nextBit() == 0 ? TreePathDirection.LEFT : TreePathDirection.RIGHT
        );
        boolean isEndBit = br3.nextBit() == 1 ? true : false;
        if (isEndBit) {
          break;
        }
      }
      byte value = tree.getByteAtPath(
        (TreePathDirection[]) path.toArray(new TreePathDirection[0])
      );
      bw2.pushByte(value);
    }
  }
}
