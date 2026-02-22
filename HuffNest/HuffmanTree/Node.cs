namespace HuffmanTree;

public class Node
{
    public Node(byte value, int level)
    {
        Value = value;
        Level = level;
    }

    public byte Value { get; private set; }
    public int Level { get; }
}
