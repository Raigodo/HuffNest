namespace Util;

public class BitMerger
{
    private Queue<byte> readyQueue = new(4);
    private byte buffer = 0;
    private byte index = 0;

    public bool ByteInProgress
    {
        get => index > 0;
    }

    public bool ByteReady
    {
        get => readyQueue.Count > 0;
    }

    public void PushBit(Bit value)
    {
        buffer <<= 1;
        buffer += value.Value;
        index++;
        if (index >= 8)
        {
            readyQueue.Enqueue(buffer);
            buffer = 0;
            index = 0;
        }
    }

    public void PushByte(byte value)
    {
        if (index == 0)
        {
            readyQueue.Enqueue(value);
        }
        else
        {
            for (int i = 7; i >= 0; i--)
            {
                var bit = value >> i;
                PushBit(new((byte)bit));
            }
        }
    }

    public byte GetByte() => readyQueue.Dequeue();
}
