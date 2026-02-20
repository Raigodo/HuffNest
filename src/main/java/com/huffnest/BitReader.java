package com.huffnest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class BitReader {

  public BitReader(Path path) throws IOException {
    stream = Files.newInputStream(path);
    fillBuffer();
    currentByte = queue.next();
    isCurrentByteActive = true;
  }

  private InputStream stream;
  private boolean closed = false;

  private static final int BUFFER_SIZE = 2;
  private static final int BUFFER_QUEUE_SIZE = BUFFER_SIZE + 1;
  private byte[] _buffer = new byte[BUFFER_SIZE];
  private byte[] _bufferTemp = new byte[1];
  private ByteRingBuffer queue = new ByteRingBuffer(BUFFER_QUEUE_SIZE);
  private byte currentByte = 0;
  private byte bitIndex = 0;
  private boolean isCurrentByteActive = false;

  public boolean isClosed() {
    return closed && queue.size() == 0 && !isCurrentByteActive;
  }

  public byte nextBit() throws IOException {
    if (isClosed()) throw new IOException("Stream is closed");
    byte bit = (byte) ((currentByte >> (7 - bitIndex)) & 1);
    bitIndex = (byte) ((bitIndex + 1) % 8);
    if (bitIndex == 0) {
      if (queue.size() > 0) {
        currentByte = queue.next();
        isCurrentByteActive = true;
      } else {
        isCurrentByteActive = false;
      }
    }
    if (queue.size() == 0 && !closed) {
      fillBuffer();
    }

    return bit;
  }

  public byte nextByte() throws IOException {
    if (isClosed()) throw new IOException("Stream is closed");
    if (bitIndex > 0 && queue.size() <= 0) {
      throw new IOException("Stream is closed");
    }

    if (queue.size() <= 0 && !closed) {
      fillBuffer();
    }

    if (isCurrentByteActive && queue.size() == 0) {
      isCurrentByteActive = false;
      return currentByte;
    }

    byte nextByte = queue.next();

    if (bitIndex == 0) {
      byte toReturn = currentByte;
      currentByte = nextByte;
      return toReturn;
    }

    byte forgedByte = (byte) (currentByte << bitIndex);
    byte nextByteShifted = (byte) (nextByte >> (8 - bitIndex));
    forgedByte |= nextByteShifted;

    return forgedByte;
  }

  private void fillBuffer() throws IOException {
    if (isClosed()) throw new IOException("Stream is closed");
    int read = stream.read(_buffer, 0, BUFFER_SIZE - 1);
    for (int i = 0; i < read; i++) queue.add(_buffer[i]);
    if (read < BUFFER_SIZE - 1) {
      close();
      return;
    }
    read = stream.read(_bufferTemp, 0, 1);
    if (read <= 0) {
      close();
      return;
    }
    queue.add(_bufferTemp[0]);
  }

  public void close() throws IOException {
    closed = true;
    stream.close();
  }
}
