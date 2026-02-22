namespace IO;

public class BufferedWriter
{
    public BufferedWriter(string path)
    {
        writer = new(path);
        buffer = new byte[WriteBufferSize];
    }

    private const int WriteBufferSize = 1024;

    private StreamWriter writer;
    private byte[] buffer;
    private int index = 0;
    private CancellationTokenSource cts = new();

    public long Size
    {
        get => WriteBufferSize;
    }
    public long Count
    {
        get => index;
    }

    public async Task Write(byte value)
    {
        if (index >= WriteBufferSize)
            throw new Exception("buffer writer buffer overflow");

        buffer[index++] = value;

        if (index == buffer.Length)
        {
            if (index != 0)
                await Flush(cts.Token);
        }
    }

    public async Task Flush(CancellationToken ct = default)
    {
        for (int i = 0; i < index; i++)
        {
            if (ct.IsCancellationRequested)
                break;
            writer.Write((char)buffer[i]);
        }
        index = 0;
    }

    public async Task CloseAsync()
    {
        await Flush();
        writer.Close();
        writer.Dispose();
    }
}
