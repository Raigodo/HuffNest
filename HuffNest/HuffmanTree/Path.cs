namespace HuffmanTree;

public record Path
{
    public required Direction[] MyProperty { get; init; }

    public enum Direction : byte
    {
        Left = 0,
        Right = 1,
    }
}
