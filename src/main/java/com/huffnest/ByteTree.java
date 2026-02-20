package com.huffnest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ByteTree {

  public ByteTree(ByteTreeNode root) {
    this.root = root;
    buildNavigationMap(new TreePathDirection[0], root);
  }

  private ByteTreeNode root;
  private Map<Byte, TreePathDirection[]> navigationMap = new HashMap<>();

  public TreePathDirection[] getPathToByte(byte value) {
    return navigationMap.get(value);
  }

  public byte getByteAtPath(TreePathDirection[] path) {
    ByteTreeNode current = root;

    for (TreePathDirection direction : path) {
      if (direction == TreePathDirection.LEFT) current = current.left;
      else current = current.right;
    }

    return current.value;
  }

  private void buildNavigationMap(
    TreePathDirection[] currentPath,
    ByteTreeNode current
  ) {
    if (currentPath.length > 0) {
      navigationMap.put(current.value, currentPath);
    }

    if (current.left != null) {
      TreePathDirection[] childPath = Arrays.copyOf(
        currentPath,
        currentPath.length + 1
      );
      childPath[childPath.length - 1] = TreePathDirection.LEFT;
      buildNavigationMap(childPath, current.left);
    }
    if (current.right != null) {
      TreePathDirection[] childPath = Arrays.copyOf(
        currentPath,
        currentPath.length + 1
      );
      childPath[childPath.length - 1] = TreePathDirection.RIGHT;
      buildNavigationMap(childPath, current.right);
    }
  }

  public enum TreePathDirection {
    LEFT,
    RIGHT,
  }
}
