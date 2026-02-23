namespace HuffmanTree.Factory;

public static class TreeFactory
{
    public static NewTreeBuilder Create() => new NewTreeBuilder();

    public static OldTreeBuilder Recreate() => new OldTreeBuilder();
}
