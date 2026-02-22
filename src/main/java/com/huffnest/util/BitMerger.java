package com.huffnest.util;

public class BitMerger {

  private ByteRingBuffer buffer = new ByteRingBuffer(2);
  private byte currentByte = 0;
  private int bitIndex = 0;

  public byte getPaddingBitCount() {
    return (byte) (8 - bitIndex);
  }

  public boolean isBuildInProgress() {
    return bitIndex == 0;
  }

  public boolean hasByte() {
    return buffer.size() > 0;
  }

  public byte getByte() {
    if (!hasByte()) {
      throw new IllegalStateException("No more bits to merge");
    }
    return buffer.next();
  }

  public void pushBit(byte bit) {
    currentByte <<= 1;
    currentByte += bit;
    bitIndex++;

    if (bitIndex == 8) {
      buffer.add(currentByte);
      currentByte = 0;
      bitIndex = 0;
    }
  }

  public void pushByte(byte value) {
    if (bitIndex == 0) {
      buffer.add(value);
    } else {
      for (int i = 7; i >= 0; i--) {
        pushBit((byte) ((value >> i) & 1));
      }
    }
  }
}
