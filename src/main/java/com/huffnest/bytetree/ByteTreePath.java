package com.huffnest.bytetree;

import java.util.ArrayList;
import java.util.List;

public class ByteTreePath {

  private ByteTreePath() {
    path = new ArrayList<>();
  }

  private static ByteTreePath empty = new ByteTreePath();
  private List<Direction> path;

  public static ByteTreePath empty() {
    return empty.copy();
  }

  public ByteTreePathIterator iterator() {
    return new ByteTreePathIterator(this);
  }

  public int level() {
    return path.size();
  }

  public void pushDirectionBit(byte directionBit) {
    if (directionBit == 0) pushLeft();
    else pushRight();
  }

  public void pushLeft() {
    path.add(Direction.LEFT);
  }

  public void pushRight() {
    path.add(Direction.RIGHT);
  }

  public void popLastLevel() {
    path.remove(path.size() - 1);
  }

  public ByteTreePath copy() {
    ByteTreePath copy = new ByteTreePath();
    copy.path = new ArrayList<>(this.path);
    return copy;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Direction d : path) {
      if (d == Direction.LEFT) sb.append("L");
      else sb.append("R");
    }
    return sb.toString();
  }

  public static enum Direction {
    LEFT,
    RIGHT,
  }

  public class ByteTreePathIterator {

    public ByteTreePathIterator(ByteTreePath path) {
      this.currentIndex = 0;
    }

    private int currentIndex;

    public boolean hasNext() {
      return path.size() > 0 && currentIndex < path.size();
    }

    public Direction next() {
      return path.get(currentIndex++);
    }

    public boolean nextIsLeft() {
      return path.get(currentIndex++) == Direction.LEFT;
    }

    public boolean nextIsRight() {
      return path.get(currentIndex++) == Direction.RIGHT;
    }
  }
}
