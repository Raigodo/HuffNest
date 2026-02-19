package com.huffnest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ByteReader {

  public ByteReader(String path) throws IOException {
    stream = Files.newInputStream(Paths.get(path));
    slideBufferedWindow();
  }

  private static final int BUFFER_SIZE = 3;
  private byte[] buffer = new byte[BUFFER_SIZE];
  private int index = 0;
  private int bufferEndIndex = 0;
  private InputStream stream;
  private boolean closed = false;

  public boolean isClosed() {
    return closed;
  }

  public byte nextByte() throws IOException {
    if (closed) throw new IOException("Stream is closed");
    byte value = buffer[index++];
    if (index - 1 >= bufferEndIndex) slideBufferedWindow();

    return value;
  }

  private void slideBufferedWindow() throws IOException {
    int read = stream.read(buffer, 0, BUFFER_SIZE);
    bufferEndIndex = read - 1;
    index = 0;

    if (read <= 0) {
      close();
      return;
    }
  }

  public void close() throws IOException {
    closed = true;
    stream.close();
  }
}
