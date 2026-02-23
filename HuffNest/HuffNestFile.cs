using HuffmanTree;
using HuffmanTree.Factory;
using IO;
using Util;

public class HuffNestFile(string inputFilePath, string outputFilePath)
{
    public async Task Compress(int iterationCount)
    {
        int iteration = 1;

        BitReader br = new BitReader(inputFilePath);

        var treeBuilder = TreeFactory.New();

        Console.WriteLine("Building tree...");

        while (br.HasNextByte)
        {
            treeBuilder.PushByte(await br.GetNextByteAsync());
        }
        br.Close();

        Tree tree = treeBuilder.Build();

        byte x = (byte)'1';
        for (int i = 0; i < 6; i++)
        {
            tree.TryGetPathToByte(x, out var pth);
            tree.TryGetByteAtPath(pth, out var b);
            x++;
            Console.WriteLine((char)b + "/" + b + " - " + pth);
        }

        Console.WriteLine("Tree Built!");

        BitWriter bw = new BitWriter(outputFilePath);

        Console.WriteLine("Serializing Tree...");

        byte[] serializedTree = TreeSerializer.Serialize(tree);
        Tree tree2 = TreeSerializer.Deserialize(serializedTree);

        Console.WriteLine("Tree Serialized!");

        await bw.WriteIntAsync(serializedTree.Length);
        await bw.WriteIntAsync(iteration);
        await bw.WriteByteAsync(0); //space for byte that describes padding bit count at the end of file

        foreach (var b in serializedTree)
        {
            await bw.WriteByteAsync(b);
        }

        //room for future improvements:
        // bw.pushInt(0);
        // bw.pushInt(0);

        Console.WriteLine("Compressing file...");
        br = new BitReader(inputFilePath);

        while (br.HasNextByte)
        {
            byte b = await br.GetNextByteAsync();
            if (tree.TryGetPathToByte(b, out TreePath path))
            {
                Console.WriteLine("compress " + (char)b + "/" + b + " to " + path);
                foreach (Bit bit in path.GetEnumerator())
                {
                    await bw.WriteBitAsync(bit);
                }
            }
            else
            {
                throw new Exception("failed to get path to byte");
            }
        }
        br.Close();

        byte pbc = await bw.CloseAsync();

        if (pbc > 0)
        {
            using (
                var fs = new FileStream(
                    outputFilePath,
                    FileMode.Open,
                    FileAccess.Write,
                    FileShare.None
                )
            )
            {
                fs.Seek(8, SeekOrigin.Begin);
                fs.WriteByte(pbc);
                fs.Close();
            }
        }

        Console.WriteLine("File compressed!");
    }

    public async Task Decompress()
    {
        BitReader br = new BitReader(inputFilePath);
        BitWriter bw = new BitWriter(outputFilePath);

        int treeSize = await br.NextIntAsync();
        int iterationsLeft = await br.NextIntAsync();

        byte[] serializedTree = new byte[treeSize];

        byte pbc = await br.GetNextByteAsync();

        for (int i = 0; i < treeSize; i++)
        {
            serializedTree[i] = await br.GetNextByteAsync();
        }

        Tree tree = TreeSerializer.Deserialize(serializedTree);

        Console.WriteLine("Decompressing file...");
        UnknownPath path = new();
        while (br.HasNextBit)
        {
            path.PushBit(await br.GetNextBitAsync());
            if (tree.TryGetByteAtPath(path, out byte value))
            {
                await bw.WriteByteAsync(value);
                path.Reset();
            }
        }
        br.Close();
        await bw.CloseAsync();

        //clean up messed up file end

        path.Reset();
        byte paddingByteCount = 0;

        for (int i = 0; i < pbc; i++)
        {
            path.PushBit(new Bit());
            if (tree.TryGetByteAtPath(path, out var _))
            {
                paddingByteCount++;
                path.Reset();
            }
        }

        if (paddingByteCount > 0)
        {
            using (
                var fs = new FileStream(
                    outputFilePath,
                    FileMode.Open,
                    FileAccess.Write,
                    FileShare.None
                )
            )
            {
                long newLength = Math.Max(0, fs.Length - paddingByteCount);
                fs.SetLength(newLength);
                fs.Close();
            }
        }
    }
}
