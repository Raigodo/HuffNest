package com.huffnest.io;

import com.huffnest.util.BitMerger;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class BitWriter {

  public BitWriter(Path path) throws IOException {
    os = Files.newOutputStream(path);
  }

  private OutputStream os;
  private BitMerger bitMerger = new BitMerger();

  public void pushBit(byte bit) throws IOException {
    bitMerger.pushBit(bit);

    if (bitMerger.hasByte()) {
      writeBuffer(bitMerger.getByte());
    }
  }

  public void pushByte(byte b) throws IOException {
    bitMerger.pushByte(b);
    writeBuffer(bitMerger.getByte());
  }

  public void pushInt(int value) throws IOException {
    pushByte((byte) (value >>> 24));
    pushByte((byte) (value >>> 16));
    pushByte((byte) (value >>> 8));
    pushByte((byte) value);
  }

  public void close() throws IOException {
    while (!bitMerger.isBuildInProgress()) bitMerger.pushBit((byte) 0);
    while (bitMerger.hasByte()) writeBuffer(bitMerger.getByte());
    os.close();
  }

  private void writeBuffer(byte b) throws IOException {
    os.write(new byte[] { b });
  }
}
