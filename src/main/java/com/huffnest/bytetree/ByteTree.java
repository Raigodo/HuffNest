package com.huffnest.bytetree;

import com.huffnest.bytetree.ByteTreePath.ByteTreePathIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ByteTree {

  public ByteTree(ByteTreeNode root) {
    this.root = root;
    buildNavigationMap(ByteTreePath.empty(), root);
  }

  private ByteTreeNode root;
  private Map<Byte, ByteTreePath> navigationMap = new HashMap<>();

  public ByteTreeIterator getIterator() {
    return new ByteTreeIterator(root);
  }

  public ControlledByteTreeIterator getControlledIterator() {
    return new ControlledByteTreeIterator(root);
  }

  public ByteTreePath getPathToByte(byte value) {
    return navigationMap.get(value);
  }

  public boolean hasByteAtPath(ByteTreePath path) {
    ByteTreeNode current = root;
    ByteTreePathIterator pathIterator = path.iterator();
    while (pathIterator.hasNext()) {
      if (pathIterator.nextIsLeft() && current.left != null) {
        current = current.left;
        continue;
      }
      if (current.right != null) {
        current = current.right;
        continue;
      }
      return false;
    }
    return current.left == null && current.right == null;
  }

  public byte getByteAtPath(ByteTreePath path) {
    ByteTreeNode current = root;
    ByteTreePathIterator pathIterator = path.iterator();
    while (pathIterator.hasNext()) {
      if (pathIterator.nextIsLeft() && current.left != null) {
        current = current.left;
      } else if (current.right != null) {
        current = current.right;
      } else {
        throw new RuntimeException("Invalid path: reached a null node");
      }
    }
    return current.value;
  }

  public void print() {
    for (Map.Entry<Byte, ByteTreePath> entry : navigationMap.entrySet()) {
      System.out.println(
        (char) entry.getKey().intValue() + ": " + entry.getValue().toString()
      );
    }
  }

  public void rebuildNavigationMap() {
    navigationMap.clear();
    buildNavigationMap(ByteTreePath.empty(), root);
  }

  private void buildNavigationMap(
    ByteTreePath currentPath,
    ByteTreeNode current
  ) {
    if (current.left == null || current.right == null) {
      navigationMap.put(current.value, currentPath);
    }

    if (current.left != null) {
      ByteTreePath childPath = currentPath.copy();
      childPath.pushLeft();
      buildNavigationMap(childPath, current.left);
    }
    if (current.right != null) {
      ByteTreePath childPath = currentPath.copy();
      childPath.pushRight();
      buildNavigationMap(childPath, current.right);
    }
  }

  public class ByteTreeIterator {

    public ByteTreeIterator(ByteTreeNode root) {
      previous = root;
      next = null;
    }

    private ByteTreeNode previous;
    private ByteTreeNode next;

    public boolean hasNext() {
      if (next == null) next = recursiveGetNextNode(previous);
      return next != null;
    }

    public ByteTreeNode nextNode() {
      if (next == null) {
        next = recursiveGetNextNode(previous);
      }
      if (next == null) throw new RuntimeException("No more nodes in tree");
      previous = next;
      next = null;
      return previous;
    }

    private ByteTreeNode recursiveGetNextNode(ByteTreeNode current) {
      if (current == null) return null;

      if (
        current != previous && current.right == null && current.left == null
      ) {
        return current;
      }

      if (current.left != null && current.left != previous) {
        return recursiveGetNextNode(current.left);
      }

      if (
        current == previous &&
        current.right == null &&
        current.left == null &&
        current.parent != null &&
        current.parent.right != current
      ) {
        return current.parent.right;
      }

      if (
        current.parent != null &&
        current.parent.parent != null &&
        root.right != current.parent
      ) {
        previous = current.parent;
        return recursiveGetNextNode(current.parent.parent);
      }

      if (current.right != null && current.right != previous) {
        return recursiveGetNextNode(current.right);
      }

      return null;
    }
  }

  public class ControlledByteTreeIterator {

    public ControlledByteTreeIterator(ByteTreeNode root) {
      this.currentLevelNodes = new ByteTreeNode[] { root };
    }

    private ByteTreeNode[] currentLevelNodes;
    private int index = 0;

    public boolean hasNext() {
      return (
        currentLevelNodes.length > 0 && index < currentLevelNodes.length - 1
      );
    }

    public ByteTreeNode currentNode() {
      return currentLevelNodes[index];
    }

    public ByteTreeNode nextNode() {
      if (!hasNext()) throw new RuntimeException("No more nodes at this level");
      return currentLevelNodes[index++];
    }

    public void nextLevel() {
      List<ByteTreeNode> nextLevelNodes = new ArrayList<>();

      for (ByteTreeNode node : currentLevelNodes) {
        if (node.left != null) nextLevelNodes.add(node.left);
        if (node.right != null) nextLevelNodes.add(node.right);
      }

      currentLevelNodes = nextLevelNodes.toArray(new ByteTreeNode[0]);
      index = 0;
    }
  }
}
