using HuffmanTree;
using HuffmanTree.Factory;
using IO;

var br = new BitReader("E:\\Projects\\huffnest\\test.txt");
var treeBuilder = TreeFactory.Create();

while (br.HasNextByte)
{
    treeBuilder.PushByte(await br.GetNextByteAsync());
}

br.Close();
Tree tree = treeBuilder.Build();

byte x = (byte)'1';

for (int i = 0; i < 4; i++)
{
    var ok1 = tree.TryGetPathToByte(x, out TreePath path);
    var ok2 = tree.TryGetByteAtPath(path, out byte value);
    Console.WriteLine($"{(char)value}/{value} at {path}");
    x++;
}

var serializedTree = TreeSerializer.Serialize(tree);
var tree2 = TreeSerializer.Deserialize(serializedTree);

x = (byte)'1';

for (int i = 0; i < 4; i++)
{
    var ok1 = tree2.TryGetPathToByte(x, out TreePath path);
    var ok2 = tree2.TryGetByteAtPath(path, out byte value);
    Console.WriteLine($"{(char)value}/{value} at {path}");
    x++;
}
