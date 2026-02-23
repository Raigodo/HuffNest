namespace IO;

public class BufferedReader
{
    public BufferedReader(string path)
    {
        br = new BinaryReader(File.Open(path, FileMode.Open));
        queue = new(BufferSize);
        readBuffer = new byte[BufferSize / 2];
    }

    private const int BufferSize = 1024;

    private BinaryReader br;
    private byte[] readBuffer;
    private Queue<byte> queue = new();
    private CancellationTokenSource cts = new();

    public long Count
    {
        get => queue.Count + br.BaseStream.Length - br.BaseStream.Position;
    }

    public bool IsEmpty
    {
        get => br.BaseStream.Position >= br.BaseStream.Length && queue.Count <= 0;
    }

    public async Task<byte> ReadNext()
    {
        if (IsEmpty)
            throw new Exception("no more bytes");

        if (queue.Count <= 0)
        {
            await FillQueueAsync(cts.Token);
        }
        return queue.Dequeue();
    }

    public async Task<byte[]> ReadNext(long count)
    {
        if (IsEmpty)
            throw new Exception("no more bytes");

        if (Count < count)
            throw new Exception("not enough bytes");

        byte[] result = new byte[count];
        for (int i = 0; i < count; i++)
        {
            if (queue.Count <= 0)
            {
                await FillQueueAsync(cts.Token);
            }
            result[i] = queue.Dequeue();
        }
        return result;
    }

    private async Task FillQueueAsync(CancellationToken ct)
    {
        if (br.BaseStream.Position >= br.BaseStream.Length)
        {
            return;
        }

        int bytesRead = await br.BaseStream.ReadAsync(readBuffer, 0, readBuffer.Length, ct);
        for (int i = 0; i < bytesRead; i++)
        {
            queue.Enqueue(readBuffer[i]);
        }

        if (!cts.TryReset())
        {
            cts = new();
        }
    }

    public void Close()
    {
        br.Close();
        br.Dispose();
    }
}
