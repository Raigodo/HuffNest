namespace Util;

public class BitSplitter
{
    private Queue<byte> queue = new(5);
    private byte currentByte = 0;
    private byte bitIndex = 0;
    private bool isCurrentByteActive = false;

    public int WholeByteCount
    {
        get => queue.Count + (bitIndex == 0 ? 1 : 0);
    }

    public bool HasNextBit
    {
        get => isCurrentByteActive || queue.Count > 0;
    }

    public bool HasNextByte
    {
        get => (isCurrentByteActive && bitIndex == 0) || queue.Count > 0;
    }

    public bool HasNextInt
    {
        get => (isCurrentByteActive && bitIndex == 0 && queue.Count >= 3) || queue.Count >= 4;
    }

    public void PushByte(byte value) => queue.Enqueue(value);

    public Bit NextBit()
    {
        if (!isCurrentByteActive)
        {
            pullNextByte();
        }
        if (!isCurrentByteActive)
        {
            throw new Exception("No more bits available");
        }

        byte value = (byte)((currentByte >> (7 - bitIndex)) & 1);
        bitIndex++;
        if (bitIndex == 8)
        {
            pullNextByte();
        }

        return new Bit(value);
    }

    public byte NextByte()
    {
        if (!isCurrentByteActive)
        {
            pullNextByte();
        }
        if (!isCurrentByteActive)
        {
            throw new Exception("No more bits available");
        }

        if (bitIndex == 0)
        {
            byte result = currentByte;
            pullNextByte();
            return result;
        }
        else
        {
            if (queue.Count <= 0)
            {
                throw new Exception("No more bits available");
            }

            byte result = (byte)(currentByte << bitIndex);
            byte nextByte = queue.Dequeue();
            result |= (byte)(nextByte >> (8 - bitIndex));
            currentByte = nextByte;
            return result;
        }
    }

    private void pullNextByte()
    {
        if (queue.Count > 0)
        {
            currentByte = queue.Dequeue();
            bitIndex = 0;
            isCurrentByteActive = true;
        }
        else
        {
            isCurrentByteActive = false;
        }
    }
}
