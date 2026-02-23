using System.Collections;
using System.Text;

namespace HuffmanTree;

public record struct TreePath
{
    public TreePath(PathId pathId, BitArray steps)
    {
        Id = pathId;
        Steps = steps;
    }

    public PathId Id { get; init; }
    public BitArray Steps { get; init; }

    public record struct PathId(byte Value);

    public override string ToString()
    {
        StringBuilder sb = new();
        for (int i = 0; i < Steps.Length; i++)
        {
            sb.Append(Steps.Get(i) ? "R" : "L");
        }
        return sb.ToString();
    }
}
