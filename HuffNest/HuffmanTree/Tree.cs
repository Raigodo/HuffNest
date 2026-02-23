namespace HuffmanTree;

public partial class Tree
{
    public Tree(Node root)
    {
        byteAtPathMap = new();
        pathToByteMap = new();
        Root = root;
        ReevaluateMaps();
    }

    public Node Root { get; init; }
    private Dictionary<TreePath.PathId, byte> byteAtPathMap;
    private Dictionary<byte, TreePath> pathToByteMap;

    public void ReevaluateMaps()
    {
        pathToByteMap.Clear();
        byteAtPathMap.Clear();

        var iterator = GetIterator();
        foreach (Node node in iterator)
        {
            if (node.Left == null)
            {
                TreePath.PathId pathId = new TreePath.PathId(node.Value);
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

    public IEnumerable<Node> GetIterator() => TraverseInOrder(Root);

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
