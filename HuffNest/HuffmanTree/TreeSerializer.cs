using HuffmanTree.Factory;

namespace HuffmanTree;

public static class TreeSerializer
{
    public static byte[] Serialize(Tree tree)
    {
        List<byte> bytes = new();
        var itrerator = tree.GetIterator();

        foreach (var node in itrerator)
        {
            if (node.Left == null)
            {
                bytes.Add(node.Value);
                bytes.Add((byte)node.Level);
            }
        }

        return bytes.ToArray();
    }

    public static Tree Deserialize(byte[] bytes)
    {
        var builder = TreeFactory.Recreate();
        builder.PushBytes(bytes);
        return builder.Build();
    }
}
