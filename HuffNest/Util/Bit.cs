namespace Util;

public record struct Bit
{
    public Bit(byte value)
    {
        Value = (byte)(value & 1);
    }

    public bool IsZero => Value == 0;
    public bool IsOne => Value == 1;

    public byte Value { get; init; }
}
