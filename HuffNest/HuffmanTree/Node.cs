using System.Collections;

namespace HuffmanTree;

public class Node
{
    public Node(byte value, int level, BitArray steps)
    {
        Value = value;
        Level = level;
        Steps = steps;
    }

    public byte Value { get; private set; }
    public int Level { get; }
    public Node? Left { get; init; }
    public Node? Right { get; init; }
    public BitArray Steps { get; set; }
}
