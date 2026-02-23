using System.Collections;

namespace HuffmanTree.Factory;

public class VirtualNode
{
    public required byte Level { get; set; }
    public required byte Value { get; set; }
    public VirtualNode? Parent { get; set; }
    public VirtualNode? Left { get; set; }
    public VirtualNode? Right { get; set; }
    public required BitArray Steps { get; set; }

    public Node Materialize()
    {
        if (Left == null && Right == null)
        {
            return new Node(Value, Level, Steps);
        }

        Node materializedLeft = Left!.Materialize();
        Node materializedRight = Right!.Materialize();
        return new Node(Value, Level, Steps) { Left = materializedLeft, Right = materializedRight };
    }
}
