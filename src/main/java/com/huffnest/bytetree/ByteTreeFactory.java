package com.huffnest.bytetree;

import com.huffnest.bytetree.ByteTreePath.ByteTreePathIterator;
import com.huffnest.util.ByteArrayUtils;
import java.util.Arrays;
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

    private Map<Byte, Long> frequencyTable = new HashMap<>();

    public void pushByte(byte b) {
      frequencyTable.put(b, frequencyTable.getOrDefault(b, 0L) + 1L);
    }

    public ByteTree build() {
      byte[] orderedKeys = listToArray(
        frequencyTable
          .entrySet()
          .stream()
          .sorted(Map.Entry.<Byte, Long>comparingByValue().reversed())
          .map(Map.Entry::getKey)
          .toList()
      );

      ByteTreeNode root = new ByteTreeNode((byte) 0, null, (byte) 0);
      craftBranch(root, orderedKeys);

      ByteTree tree = new ByteTree(root);

      return tree;
    }

    private void craftBranch(ByteTreeNode node, byte[] orderedKeys) {
      if (orderedKeys.length == 0) return;
      if (orderedKeys.length == 1) {
        node.value = orderedKeys[0];
        return;
      }

      byte[][] split = splitArray(orderedKeys);
      node.left = new ByteTreeNode((byte) 0, node, (byte) (node.level + 1));
      node.right = new ByteTreeNode((byte) 0, node, (byte) (node.level + 1));

      craftBranch(node.left, split[0]);
      craftBranch(node.right, split[1]);
    }

    private byte[][] splitArray(byte[] array) {
      if (array.length == 2) return new byte[][] {
        new byte[] { array[0] },
        new byte[] { array[1] },
      };
      long totalFrequency = countTotalFrequency(array);
      long perfectRightSum = 0;
      for (int i = array.length - 1; i >= 0; i--) {
        perfectRightSum += frequencyTable.getOrDefault(array[i], 0L);
        if (perfectRightSum >= totalFrequency / 2) break;
      }

      byte[] left = ByteArrayUtils.closestSubset(
        array,
        frequencyTable,
        perfectRightSum
      );
      byte[] right = ByteArrayUtils.subtract(array, left);

      return new byte[][] { left, right };
    }

    private long countTotalFrequency(byte[] bytes) {
      long total = 0;
      for (byte b : bytes) total += frequencyTable.getOrDefault(b, 0L);
      return total;
    }

    private static byte[] listToArray(List<Byte> list) {
      byte[] result = new byte[list.size()];
      for (int i = 0; i < list.size(); i++) {
        result[i] = list.get(i);
      }
      return result;
    }
  }

  public static class ByteTreeBuilder {

    public ByteTreeBuilder() {
      rootNode = new ByteTreeNode((byte) 0, null, (byte) 0);
      tree = new ByteTree(rootNode);
      lastPath = ByteTreePath.empty();
    }

    private ByteTreePath lastPath;
    private ByteTreeNode rootNode;
    private ByteTree tree;

    public void pushByte(byte b, byte level) {
      lastPath = recursiveGetPathToNextNode(lastPath, level);
      ByteTreeNode node = getOrCreateNode(lastPath);
      node.value = b;
    }

    private ByteTreePath recursiveGetPathToNextNode(
      ByteTreePath currentPath,
      int desiredLevel
    ) {
      if (currentPath == lastPath) currentPath = currentPath.copy();

      ByteTreeNode currentNode = getOrCreateNode(currentPath);
      ByteTreeNode lastNode = getOrCreateNode(currentPath);

      if (
        currentNode != lastNode && currentNode.level == desiredLevel
      ) return currentPath;

      if (desiredLevel > currentNode.level) {
        if (currentNode == rootNode && currentNode.left == null) {
          for (int i = currentPath.level(); i < desiredLevel; i++) {
            currentPath.pushLeft();
          }
          return currentPath;
        }
        if (currentNode == rootNode && currentNode.right == null) {
          currentPath.pushRight();
          for (int i = currentPath.level(); i < desiredLevel; i++) {
            currentPath.pushLeft();
          }
          return currentPath;
        }
        throw new RuntimeException("Invalid byte tree structure");
      } else {
        //desiredLevel <= currentNode.level
        if (currentNode.parent.right == null) {
          currentPath.popLastLevel();
          currentPath.pushRight();
          if (currentNode.level == desiredLevel) return currentPath;
          return recursiveGetPathToNextNode(currentPath, desiredLevel);
        }
        if (currentNode.parent.right == lastNode) {
          currentPath.popLastLevel();
          currentPath.popLastLevel();
          return recursiveGetPathToNextNode(currentPath, desiredLevel);
        }
        throw new RuntimeException("Invalid byte tree structure");
      }
    }

    private ByteTreeNode getOrCreateNode(ByteTreePath path) {
      ByteTreePathIterator iterator = path.iterator();
      ByteTreeNode current = rootNode;

      while (iterator.hasNext()) {
        if (iterator.nextIsLeft()) {
          if (current.left == null) {
            current.left = new ByteTreeNode(
              (byte) 0,
              current,
              (byte) (current.level + 1)
            );
          }
          current = current.left;
        } else {
          if (current.right == null) {
            current.right = new ByteTreeNode(
              (byte) 0,
              current,
              (byte) (current.level + 1)
            );
          }
          current = current.right;
        }
      }

      return current;
    }

    public ByteTree build() {
      tree.rebuildNavigationMap();
      return tree;
    }
  }
}
