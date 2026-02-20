package com.huffnest;

import com.huffnest.bytetree.ByteTree;
import com.huffnest.bytetree.ByteTree.TreePathDirection;
import com.huffnest.bytetree.ByteTreeFactory;
import com.huffnest.bytetree.ByteTreeSerializer;
import com.huffnest.io.BitReader;
import com.huffnest.io.BitWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HuffNestFile {

  public HuffNestFile(Path inputFilePath, Path outputFilePath) {
    this.inputFilePath = inputFilePath;
    this.outputFilePath = outputFilePath;
  }

  private Path inputFilePath;
  private Path outputFilePath;

  public void compress(int iterationCount) throws IOException {
    int iteration = 1;

    BitReader br = new BitReader(inputFilePath);

    ByteTreeFactory.NewByteTreeBuilder builder = ByteTreeFactory.New();

    while (br.hasNextByte()) {
      builder.appendNextByte(br.nextByte());
    }

    ByteTree tree = builder.build();

    BitWriter bw = new BitWriter(outputFilePath);

    byte[] serializedTree = new ByteTreeSerializer().serialize(tree);

    bw.pushInt(serializedTree.length);
    bw.pushInt(iteration);

    for (byte b : serializedTree) {
      bw.pushByte(b);
    }

    //room for future improvements:
    // bw.pushInt(0);
    // bw.pushInt(0);

    br = new BitReader(inputFilePath);

    while (br.hasNextByte()) {
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
  }

  public void decompress() throws IOException {
    BitReader br = new BitReader(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test2")
    );
    BitWriter bw = new BitWriter(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test3.txt")
    );

    int treeSize = br.nextInt();
    int iterationsLeft = br.nextInt();

    byte[] serializedTree = new byte[treeSize];
    for (int i = 0; i < treeSize; i++) {
      serializedTree[i] = br.nextByte();
    }

    ByteTree tree = new ByteTreeSerializer().deserialize(serializedTree);

    writeLoop: while (br.hasNextBit()) {
      List<TreePathDirection> path = new ArrayList<>();
      while (true) {
        if (!br.hasNextBit()) break writeLoop;
        path.add(
          br.nextBit() == 0 ? TreePathDirection.LEFT : TreePathDirection.RIGHT
        );
        if (!br.hasNextBit()) break writeLoop;
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
    bw.close();
  }
}
