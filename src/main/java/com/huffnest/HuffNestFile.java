package com.huffnest;

import com.huffnest.bytetree.ByteTree;
import com.huffnest.bytetree.ByteTreeFactory;
import com.huffnest.bytetree.ByteTreePath;
import com.huffnest.bytetree.ByteTreePath.ByteTreePathIterator;
import com.huffnest.bytetree.ByteTreeSerializer;
import com.huffnest.io.BitReader;
import com.huffnest.io.BitWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

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

    System.out.println("Building tree...");

    while (br.hasNextByte()) {
      builder.pushByte(br.nextByte());
    }

    ByteTree tree = builder.build();

    System.out.println("Tree Built!");

    BitWriter bw = new BitWriter(outputFilePath);

    System.out.println("Serializing Tree...");

    byte[] serializedTree = new ByteTreeSerializer().serialize(tree);

    System.out.println("Tree Serialized!");

    bw.pushInt(serializedTree.length);
    bw.pushInt(iteration);

    for (byte b : serializedTree) {
      bw.pushByte(b);
    }

    //room for future improvements:
    // bw.pushInt(0);
    // bw.pushInt(0);

    br = new BitReader(inputFilePath);
    System.out.println("Compressing file...");

    while (br.hasNextByte()) {
      byte b = br.nextByte();
      ByteTreePathIterator pathIterator = tree.getPathToByte(b).iterator();

      while (pathIterator.hasNext()) {
        bw.pushBit((byte) (pathIterator.nextIsLeft() ? 0 : 1));
      }
    }

    byte pbc = bw.close();

    System.out.println("File compressed!");
  }

  public void decompress() throws IOException {
    BitReader br = new BitReader(inputFilePath);
    BitWriter bw = new BitWriter(outputFilePath);

    int treeSize = br.nextInt();
    int iterationsLeft = br.nextInt();

    byte[] serializedTree = new byte[treeSize];

    for (int i = 0; i < treeSize; i++) {
      serializedTree[i] = br.nextByte();
    }

    ByteTree tree = new ByteTreeSerializer().deserialize(serializedTree);

    System.out.println("Decompressing file...");
    ByteTreePath path = ByteTreePath.empty();
    while (br.hasNextBit()) {
      if (!br.hasNextBit()) break;
      path.pushDirectionBit(br.nextBit());
      if (tree.hasByteAtPath(path)) {
        bw.pushByte(tree.getByteAtPath(path));
        path = ByteTreePath.empty();
      }
    }
    bw.close();

    //clean up messed up file end

    path = ByteTreePath.empty();
    byte paddingByteCount = 0;
    for (int i = 0; i < 6; i++) {
      path.pushDirectionBit((byte) 0);
      if (tree.hasByteAtPath(path)) {
        path = ByteTreePath.empty();
        paddingByteCount++;
      }
    }

    RandomAccessFile raf = new RandomAccessFile(outputFilePath.toFile(), "rw");
    long length = raf.length();

    long newLength = Math.max(0, length - paddingByteCount);
    raf.setLength(newLength);
    raf.close();
  }
}
