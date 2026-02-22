namespace IO;

public class BufferedReader
{
    public BufferedReader(string path)
    {
        fs = new FileStream(path, FileMode.Open);
        queue = new(BufferSize);
        readBuffer = new byte[BufferSize / 2];
    }

    private const int BufferSize = 1024;

    private FileStream fs;
    private byte[] readBuffer;
    private Queue<byte> queue = new();
    private CancellationTokenSource cts = new();

    public long Count
    {
        get => queue.Count + fs.Length - fs.Position;
    }

    public bool IsEmpty
    {
        get => fs.Position >= fs.Length && queue.Count <= 0;
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
        if (fs.Position >= fs.Length)
        {
            return;
        }

        int bytesRead = await fs.ReadAsync(readBuffer, 0, readBuffer.Length, ct);
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
        fs.Close();
        fs.Dispose();
    }
}
