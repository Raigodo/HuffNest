package com.huffnest.bytetree;

import com.huffnest.bytetree.ByteTree.ByteTreeIterator;
import com.huffnest.bytetree.ByteTreeFactory.ByteTreeBuilder;
import java.io.ByteArrayOutputStream;

public class ByteTreeSerializer {

  public byte[] serialize(ByteTree tree) {
    ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
    ByteTreeIterator iterator = tree.getIterator();

    while (iterator.hasNext()) {
      ByteTreeNode node = iterator.nextNode();
      byteArrayStream.write(node.value);
      byteArrayStream.write(node.level);
    }

    return byteArrayStream.toByteArray();
  }

  public ByteTree deserialize(byte[] data) {
    ByteTreeBuilder builder = ByteTreeFactory.Existing();

    for (int i = 0; i < data.length; i += 2) {
      byte value = data[i];
      byte level = data[i + 1];
      builder.pushByte(value, level);
    }

    return builder.build();
  }
}
