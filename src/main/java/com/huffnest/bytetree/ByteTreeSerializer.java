package com.huffnest.bytetree;

import com.huffnest.bytetree.ByteTreeFactory.ByteTreeBuilder;
import com.huffnest.util.BitMerger;
import java.io.ByteArrayOutputStream;

public class ByteTreeSerializer {

  private BitMerger bitMerger = new BitMerger();

  public byte[] serialize(ByteTree tree) {
    ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
    ByteTree.BreadthFirstByteTreeIterator iterator = tree.getIterator();

    while (iterator.hasNext()) {
      byte value = iterator.nextValue();
      bitMerger.pushByte(value);
      byteArrayStream.write(bitMerger.getByte());
    }

    return byteArrayStream.toByteArray();
  }

  public ByteTree deserialize(byte[] data) {
    ByteTreeBuilder builder = ByteTreeFactory.Existing();

    for (byte b : data) {
      builder.pushByte(b);
    }

    return builder.build();
  }
}
