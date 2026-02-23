using Util;

namespace IO;

public class BitWriter
{
    public BitWriter(string path)
    {
        writer = new(path);
        bitMerger = new();
    }

    private BufferedWriter writer;
    private BitMerger bitMerger;

    public async Task WriteBitAsync(Bit value)
    {
        bitMerger.PushBit(value);
        if (bitMerger.ByteReady)
        {
            await writer.Write(bitMerger.GetByte());
        }
    }

    public async Task WriteByteAsync(byte value)
    {
        bitMerger.PushByte(value);
        if (bitMerger.ByteReady)
        {
            var b = bitMerger.GetByte();
            await writer.Write(b);
        }
        await writer.Flush();
    }

    public async Task WriteIntAsync(int value)
    {
        var b = (byte)(value >>> 24);
        await writer.Write(b);
        b = (byte)(value >>> 16);
        await writer.Write(b);
        b = (byte)(value >>> 8);
        await writer.Write(b);
        b = (byte)value;
        await writer.Write(b);
    }

    public async Task<byte> CloseAsync()
    {
        byte pbc = 0;
        if (bitMerger.ByteInProgress)
        {
            while (!bitMerger.ByteReady)
            {
                pbc++;
                bitMerger.PushBit(new());
            }
            await writer.Write(bitMerger.GetByte());
        }
        await writer.CloseAsync();
        return pbc;
    }
}
