package com.huffnest.io;

import com.huffnest.util.BitSpliter;
import com.huffnest.util.ByteRingBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class BitReader {

  public BitReader(Path path) throws IOException {
    stream = Files.newInputStream(path);
    fillBuffer();
    bitSpliter.pushByte(queue.next());
    bitSpliter.pushByte(queue.next());
  }

  private InputStream stream;
  private BitSpliter bitSpliter = new BitSpliter();

  private static final int BUFFER_SIZE = 10;
  private static final int BUFFER_QUEUE_SIZE = BUFFER_SIZE;
  private byte[] _buffer = new byte[BUFFER_SIZE - 1];
  private byte[] _bufferTemp = null;
  private ByteRingBuffer queue = new ByteRingBuffer(BUFFER_QUEUE_SIZE);
  private boolean isStreamClosed = false;

  public boolean hasNextBit() {
    return !isStreamClosed || bitSpliter.hasNextBit();
  }

  public boolean hasNextByte() {
    return !isStreamClosed || bitSpliter.hasNextByte();
  }

  public boolean hasNextInt() {
    return !isStreamClosed || bitSpliter.hasNextInt();
  }

  public byte nextBit() throws IOException {
    if (!hasNextBit()) throw new IOException("Stream is closed");
    byte value = bitSpliter.nextBit();

    if (!bitSpliter.hasNextByte()) {
      if (shouldFillBuffer()) {
        fillBuffer();
      }
      if (queue.size() > 0) {
        bitSpliter.pushByte(queue.next());
      }
    }

    return value;
  }

  public byte nextByte() throws IOException {
    if (!hasNextByte()) throw new IOException("Stream is closed");
    byte value = bitSpliter.nextByte();

    if (shouldFillBuffer()) fillBuffer();
    if (queue.size() > 0) bitSpliter.pushByte(queue.next());

    return value;
  }

  public int nextInt() throws IOException {
    int b1 = nextByte() & 0xFF;
    int b2 = nextByte() & 0xFF;
    int b3 = nextByte() & 0xFF;
    int b4 = nextByte() & 0xFF;

    return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
  }

  private boolean shouldFillBuffer() {
    return queue.size() <= 0 && !isStreamClosed;
  }

  private void fillBuffer() throws IOException {
    if (isStreamClosed) throw new IOException("Stream is closed");

    if (_bufferTemp != null) {
      queue.add(_bufferTemp[0]);
    } else _bufferTemp = new byte[1];

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
  }

  public void close() throws IOException {
    isStreamClosed = true;
    stream.close();
  }
}
