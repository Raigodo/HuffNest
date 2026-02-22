namespace HuffmanTree;

public class Serializer
{
    public byte[] Serialize()
    {
        return [];
    }

    public Tree Deserialize()
    {
        return new Tree() { Root = new Node(0, 0) };
    }
}
