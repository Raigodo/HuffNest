package com.huffnest;

import com.huffnest.bytetree.ByteTree;
import com.huffnest.bytetree.ByteTreeFactory;
import com.huffnest.bytetree.ByteTreeSerializer;
import com.huffnest.io.BitReader;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {

  public static void main(String[] args) throws IOException {
    BitReader br = new BitReader(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test.txt")
    );

    ByteTreeFactory.NewByteTreeBuilder builder = ByteTreeFactory.New();

    while (br.hasMoreBytes()) {
      builder.appendNextByte(br.nextByte());
    }

    ByteTree tree = builder.build();

    System.out.println("1: " + tree.getPathToByte((byte) '1').length);
    System.out.println("2: " + tree.getPathToByte((byte) '2').length);
    System.out.println("3: " + tree.getPathToByte((byte) '3').length);
    System.out.println("4: " + tree.getPathToByte((byte) '4').length);
    System.out.println("0: " + tree.getPathToByte((byte) '0'));

    ByteTreeSerializer serializer = new ByteTreeSerializer();

    byte[] serialized = serializer.serialize(tree);

    ByteTree deserializedTree = serializer.deserialize(serialized);

    System.out.println(
      "1: " + deserializedTree.getPathToByte((byte) '1').length
    );
    System.out.println(
      "2: " + deserializedTree.getPathToByte((byte) '2').length
    );
    System.out.println(
      "3: " + deserializedTree.getPathToByte((byte) '3').length
    );
    System.out.println(
      "4: " + deserializedTree.getPathToByte((byte) '4').length
    );
    System.out.println("0: " + deserializedTree.getPathToByte((byte) '0'));

    // BitWriter bw = new BitWriter(
    //   Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test2.txt")
    // );

    // br = new BitReader(
    //   Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test.txt")
    // );

    // while (br.hasMoreBytes()) {
    //   byte b = br.nextByte();
    //   TreePathDirection[] path = tree.getPathToByte(b);

    //   for (int i = 0; i < path.length; i++) {
    //     bw.pushBit((byte) (path[i] == TreePathDirection.LEFT ? 0 : 1));
    //     if (i == path.length - 1) {
    //       bw.pushBit((byte) 1);
    //     } else {
    //       bw.pushBit((byte) 0);
    //     }
    //   }
    // }
    // bw.close();

    // //retrieve

    // br = new BitReader(
    //   Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test2.txt")
    // );
    // bw = new BitWriter(
    //   Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test3.txt")
    // );

    // writeLoop: while (br.hasMoreBits()) {
    //   List<TreePathDirection> path = new ArrayList<>();
    //   while (true) {
    //     if (!br.hasMoreBits()) break writeLoop;
    //     path.add(
    //       br.nextBit() == 0 ? TreePathDirection.LEFT : TreePathDirection.RIGHT
    //     );
    //     if (!br.hasMoreBits()) break writeLoop;
    //     boolean isEndBit = br.nextBit() == 1 ? true : false;
    //     if (isEndBit) {
    //       break;
    //     }
    //   }
    //   byte value = tree.getByteAtPath(
    //     (TreePathDirection[]) path.toArray(new TreePathDirection[0])
    //   );
    //   bw.pushByte(value);
    // }
  }
}
