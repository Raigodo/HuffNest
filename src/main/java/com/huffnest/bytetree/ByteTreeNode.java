package com.huffnest.bytetree;

public class ByteTreeNode {

  public ByteTreeNode(byte value, ByteTreeNode parent) {
    this.value = value;
    this.parent = parent;
  }

  public byte value;
  public ByteTreeNode parent;
  public ByteTreeNode left;
  public ByteTreeNode right;
  private DistributionDirection nextDistributionDirection =
    DistributionDirection.LEFT;

  public void distribute(byte value) {
    if (nextDistributionDirection == DistributionDirection.LEFT) {
      if (left == null) left = new ByteTreeNode(value, this);
      else left.distribute(value);
      nextDistributionDirection = DistributionDirection.RIGHT;
    } else {
      if (right == null) right = new ByteTreeNode(value, this);
      else right.distribute(value);
      nextDistributionDirection = DistributionDirection.LEFT;
    }
  }

  enum DistributionDirection {
    LEFT,
    RIGHT,
  }
}
