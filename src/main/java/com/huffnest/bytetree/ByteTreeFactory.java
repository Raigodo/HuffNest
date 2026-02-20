package com.huffnest.bytetree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ByteTreeFactory {

  public static NewByteTreeBuilder New() {
    return new NewByteTreeBuilder();
  }

  public static ByteTreeBuilder Existing() {
    return new ByteTreeBuilder();
  }

  public static class NewByteTreeBuilder {

    private Map<Byte, Integer> frequencyTable = new HashMap<>();

    public void pushByte(byte b) {
      frequencyTable.put(b, frequencyTable.getOrDefault(b, 0) + 1);
    }

    public ByteTree build() {
      List<Byte> orderedKeys = frequencyTable
        .entrySet()
        .stream()
        .sorted(Map.Entry.<Byte, Integer>comparingByValue().reversed())
        .map(Map.Entry::getKey)
        .toList();

      ByteTreeBuilder byteTreeBuilder = new ByteTreeBuilder();

      for (Byte b : orderedKeys) {
        byteTreeBuilder.pushByte(b);
      }

      return byteTreeBuilder.build();
    }
  }

  public static class ByteTreeBuilder {

    private ByteTree tree = new ByteTree(new ByteTreeNode((byte) 0, null));
    private ByteTree.ManualBreadthFirstByteTreeIterator iterator =
      tree.getManualIterator();

    public void pushByte(byte b) {
      ByteTreeNode node = iterator.currentNode();
      boolean byteConsumed = tryFeedByteToNode(node, b);
      if (byteConsumed) return;

      if (!iterator.hasNext()) {
        iterator.nextLevel();
      } else byteConsumed = tryFeedByteToNode(iterator.nextNode(), b);

      if (!byteConsumed) {
        pushByte(b);
      }
    }

    private boolean tryFeedByteToNode(ByteTreeNode node, byte b) {
      if (node.left == null) node.left = new ByteTreeNode(b, node);
      else if (node.right == null) node.right = new ByteTreeNode(b, node);
      else return false;
      return true;
    }

    public ByteTree build() {
      tree.rebuildnavigationMap();
      return tree;
    }
  }
}
