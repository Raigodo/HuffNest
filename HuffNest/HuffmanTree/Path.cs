using System.Collections;
using System.Text;
using Util;

namespace HuffmanTree;

public record struct TreePath
{
    public TreePath(PathId pathId, BitArray steps)
    {
        Id = pathId;
        this.steps = steps;
    }

    public PathId Id { get; init; }
    private BitArray steps;

    public IEnumerable<Bit> GetEnumerator()
    {
        for (int i = 0; i < steps.Count; i++)
        {
            yield return steps.Get(i) ? new Bit(1) : new Bit(0);
        }
    }

    public record struct PathId(byte Value);

    public override string ToString()
    {
        StringBuilder sb = new();
        for (int i = 0; i < steps.Count; i++)
        {
            sb.Append(steps.Get(i) ? "R" : "L");
        }
        return sb.ToString();
    }
}
