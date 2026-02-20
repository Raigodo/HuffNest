package com.huffnest.bytetree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ByteTreeBuilder {

  private Map<Byte, Integer> frequencyTable = new HashMap<>();

  public void appendNextByte(byte b) {
    frequencyTable.put(b, frequencyTable.getOrDefault(b, 0) + 1);
  }

  public ByteTree build() {
    List<Byte> orderedKeys = frequencyTable
      .entrySet()
      .stream()
      .sorted(Map.Entry.<Byte, Integer>comparingByValue().reversed())
      .map(Map.Entry::getKey)
      .toList();

    int index = 0;

    ByteTreeNode root = new ByteTreeNode((byte) 0, null);

    for (int i = 0; i < orderedKeys.size(); i++) {
      root.distribute(orderedKeys.get(index++));
    }

    return new ByteTree(root);
  }
}
