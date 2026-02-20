package com.huffnest.util;

public class BitSpliter {

  private ByteRingBuffer buffer = new ByteRingBuffer(2);

  private byte currentByte = 0;
  private int bitIndex = 0;
  private boolean isCurrentByteActive = false;

  public boolean hasNextBit() {
    return isCurrentByteActive || buffer.size() > 0;
  }

  public boolean hasNextByte() {
    return (isCurrentByteActive && bitIndex == 0) || buffer.size() > 0;
  }

  public boolean hasNextInt() {
    return (
      (isCurrentByteActive && bitIndex == 0 && buffer.size() >= 3) ||
      buffer.size() >= 4
    );
  }

  public void pushByte(byte value) {
    buffer.add(value);
  }

  public byte nextBit() {
    if (!isCurrentByteActive) {
      if (buffer.size() > 0) pullNextByte();
      else throw new IllegalStateException("No more bits available");
    }

    byte bit = (byte) ((currentByte >> (7 - bitIndex)) & 1);
    bitIndex++;
    if (bitIndex == 8) {
      pullNextByte();
    }
    return bit;
  }

  public byte nextByte() {
    if (!isCurrentByteActive) {
      if (buffer.size() > 0) pullNextByte();
      else throw new IllegalStateException("No more bits available");
    }

    if (bitIndex == 0) {
      byte result = currentByte;
      pullNextByte();
      return result;
    } else {
      if (buffer.size() <= 0) throw new IllegalStateException(
        "No more bits available"
      );

      byte result = (byte) ((currentByte << bitIndex) & 0xFF);
      byte nextByte = buffer.next();
      result |= (nextByte >> (8 - bitIndex)) & 0xFF;
      currentByte = nextByte;

      return result;
    }
  }

  private void pullNextByte() {
    if (buffer.size() > 0) {
      currentByte = buffer.next();
      bitIndex = 0;
      isCurrentByteActive = true;
    } else {
      isCurrentByteActive = false;
    }
  }
}
