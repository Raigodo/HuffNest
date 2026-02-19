package com.huffnest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BitWriter {

  public BitWriter() throws IOException {
    os = Files.newOutputStream(
      Paths.get("E:\\Projects\\huffnest\\src\\main\\resources\\test2.txt")
    );
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

    System.out.println(currentByte);
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
