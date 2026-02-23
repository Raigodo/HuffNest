namespace HuffmanTree.Factory;

public static class TreeFactory
{
    public static NewTreeBuilder New() => new NewTreeBuilder();

    public static OldTreeBuilder Recreate() => new OldTreeBuilder();
}
