using System.Collections;
using System.Text;
using Util;

namespace HuffmanTree;

public record struct UnknownPath
{
    public UnknownPath()
    {
        Id = new(0);
        steps = new(255);
        index = 0;
    }

    public PathId Id { get; init; }
    private BitArray steps;
    private int index;

    public void PushBit(Bit bit)
    {
        steps.Set(index++, bit.Value == 0 ? false : true);
    }

    public void Reset()
    {
        index = 0;
    }

    public IEnumerable<Bit> GetEnumerator()
    {
        for (int i = 0; i < index; i++)
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
