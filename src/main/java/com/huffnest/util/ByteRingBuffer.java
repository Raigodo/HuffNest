package com.huffnest.util;

public class ByteRingBuffer {

  public ByteRingBuffer(int capacity) {
    buffer = new byte[capacity];
  }

  private byte[] buffer;
  private int head = 0;
  private int tail = 0;
  private int size = 0;

  public int size() {
    return size;
  }

  public void add(byte value) {
    buffer[tail] = value;
    tail = (tail + 1) % buffer.length; //make it circular
    size++;
  }

  public byte next() {
    if (size == 0) {
      throw new IllegalStateException("Empty queue buffer");
    }

    byte val = buffer[head];
    head = (head + 1) % buffer.length;
    size--;
    return val;
  }
}
