namespace HuffmanTree;

public class Tree
{
    public required Node Root { get; init; }

    public Iterator GetIterator() => new Iterator(Root);

    public class Iterator(Node root)
    {
        public Node nextLeaf()
        {
            return root;
        }
    }
}
