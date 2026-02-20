package com.huffnest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class BitWriter {

  public BitWriter(Path path) throws IOException {
    os = Files.newOutputStream(path);
  }

  private OutputStream os;
  private byte currentByte = 0;
  private int bitIndex = 0;

  public void pushBit(byte bit) throws IOException {
    currentByte <<= 1;
    currentByte += bit;
    bitIndex++;

    if (bitIndex == 8) {
      writeBuffer(currentByte);
      currentByte = 0;
      bitIndex = 0;
    }
  }

  public void pushByte(byte b) throws IOException {
    if (bitIndex == 0) {
      writeBuffer(b);
    } else {
      for (int i = 7; i >= 0; i--) {
        pushBit((byte) ((b >> i) & 1));
      }
    }
  }

  public void close() throws IOException {
    if (bitIndex > 0) {
      currentByte <<= (8 - bitIndex);
      writeBuffer(currentByte);
    }
    os.close();
  }

  private void writeBuffer(byte b) throws IOException {
    os.write(new byte[] { b });
  }
}
