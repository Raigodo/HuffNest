namespace HuffmanTree;

public partial class Tree
{
    public Tree(Node root)
    {
        byteAtPathMap = new();
        pathToByteMap = new();
        this.root = root;
        ReevaluateMaps();
    }

    private Node root;
    private Dictionary<TreePath.PathId, byte> byteAtPathMap;
    private Dictionary<byte, TreePath> pathToByteMap;

    public void ReevaluateMaps()
    {
        pathToByteMap.Clear();
        byteAtPathMap.Clear();

        var iterator = GetIterator();
        byte i = 1;
        foreach (Node node in iterator)
        {
            if (node.Left == null)
            {
                var pathId = new TreePath.PathId();
                pathToByteMap[node.Value] = new TreePath(pathId, node.Steps);
                byteAtPathMap[pathId] = node.Value;
            }
        }
    }

    public bool TryGetPathToByte(byte value, out TreePath path)
    {
        return pathToByteMap.TryGetValue(value, out path);
    }

    public bool TryGetByteAtPath(TreePath path, out byte value)
    {
        return byteAtPathMap.TryGetValue(path.Id, out value);
    }

    public bool TryGetByteAtPath(UnknownPath path, out byte value)
    {
        Node currentNode = root;
        foreach (var bit in path.GetEnumerator())
        {
            if (currentNode.Left == null)
                throw new Exception("Invalid tree structure");
            if (bit.IsZero && currentNode.Left != null)
            {
                currentNode = currentNode.Left;
            }
            if (bit.IsOne && currentNode.Right != null)
            {
                currentNode = currentNode.Right;
            }
        }

        if (currentNode.Left == null)
        {
            value = currentNode.Value;
            return true;
        }
        value = default;
        return false;
    }

    public IEnumerable<Node> GetIterator() => TraverseInOrder(root);

    private static IEnumerable<Node> TraverseInOrder(Node? root)
    {
        if (root is null)
            yield break;

        if (root.Left == null)
            yield return root;

        foreach (var n in TraverseInOrder(root.Left))
        {
            yield return n;
        }

        foreach (var n in TraverseInOrder(root.Right))
        {
            yield return n;
        }
    }
}
