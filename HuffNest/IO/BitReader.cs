using System.Security.Cryptography.X509Certificates;
using Util;

namespace IO;

public class BitReader
{
    public BitReader(string path)
    {
        reader = new(path);
    }

    private BufferedReader reader;
    private BitSplitter bitSplitter = new();

    public bool IsEmpty
    {
        get => reader.IsEmpty && bitSplitter.HasNextBit;
    }

    public bool HasNextBit
    {
        get => bitSplitter.HasNextBit || bitSplitter.WholeByteCount + reader.Count >= 1;
    }
    public bool HasNextByte
    {
        get => bitSplitter.HasNextByte || bitSplitter.WholeByteCount + reader.Count >= 1;
    }
    public bool HasNextInt
    {
        get => bitSplitter.HasNextInt || bitSplitter.WholeByteCount + reader.Count >= 4;
    }

    public async Task<Bit> GetNextBitAsync()
    {
        if (!bitSplitter.HasNextBit)
        {
            await FillBitSplitterQueue(1 - bitSplitter.WholeByteCount);
            if (!bitSplitter.HasNextByte)
            {
                throw new Exception("no more bits");
            }
        }
        return bitSplitter.NextBit();
    }

    public async Task<byte> GetNextByteAsync()
    {
        if (!bitSplitter.HasNextByte)
        {
            await FillBitSplitterQueue(1 - bitSplitter.WholeByteCount);
            if (!bitSplitter.HasNextByte)
            {
                throw new Exception("no more bytes");
            }
        }
        return bitSplitter.NextByte();
    }

    public async Task<int> NextIntAsync()
    {
        if (!bitSplitter.HasNextInt)
        {
            await FillBitSplitterQueue(4 - bitSplitter.WholeByteCount);
            if (!bitSplitter.HasNextByte)
            {
                throw new Exception("no more integers");
            }
        }

        int b1 = bitSplitter.NextByte();
        int b2 = bitSplitter.NextByte();
        int b3 = bitSplitter.NextByte();
        int b4 = bitSplitter.NextByte();

        return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }

    private async Task FillBitSplitterQueue(int count)
    {
        if (count <= 0)
            throw new Exception("count can not be less than 1");

        if (reader.Count < count)
        {
            throw new Exception("can not fill bit splitter");
        }

        if (count == 1)
        {
            bitSplitter.PushByte(await reader.ReadNext());
            return;
        }

        byte[] bytes = await reader.ReadNext(count);
        for (int i = 0; i < count; i++)
        {
            bitSplitter.PushByte(bytes[i]);
        }
    }

    public void Close()
    {
        reader.Close();
    }
}
