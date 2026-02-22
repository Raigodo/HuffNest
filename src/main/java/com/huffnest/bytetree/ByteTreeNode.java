package com.huffnest.bytetree;

public class ByteTreeNode {

  public ByteTreeNode(byte value, ByteTreeNode parent, byte level) {
    this.value = value;
    this.parent = parent;
    this.level = level;
  }

  public byte value;
  public ByteTreeNode parent;
  public ByteTreeNode left;
  public byte level;
  public ByteTreeNode right;
}
