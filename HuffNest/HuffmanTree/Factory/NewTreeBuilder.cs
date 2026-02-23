using System.Collections;
using Util;

namespace HuffmanTree.Factory;

public class NewTreeBuilder
{
    private Dictionary<byte, long> frequencyMap = new();

    public void PushByte(byte value)
    {
        frequencyMap[value] = frequencyMap.GetValueOrDefault(value, 0) + 1;
    }

    public Tree Build()
    {
        var keys = frequencyMap.OrderByDescending(x => x.Value).Select(x => x.Key).ToArray();
        if (keys == null)
            throw new Exception("Empty tree makes no sense");

        VirtualNode virtualRoot = new VirtualNode()
        {
            Value = 0,
            Level = 0,
            Steps = new BitArray(0),
        };

        CraftVirtualBranch(virtualRoot, keys);

        Node root = virtualRoot.Materialize();

        return new Tree(root);
    }

    private void CraftVirtualBranch(VirtualNode node, byte[] orderedKeys)
    {
        if (orderedKeys.Length == 0)
            return;
        if (orderedKeys.Length == 1)
        {
            node.Value = orderedKeys[0];
            return;
        }

        BitArray leftSteps = new BitArray(node.Steps.Length + 1);
        for (int i = 0; i < node.Steps.Length; i++)
        {
            leftSteps.Set(i, node.Steps.Get(i));
        }
        leftSteps.Set(leftSteps.Length - 1, false);

        BitArray rightSteps = new BitArray(node.Steps.Length + 1);
        for (int i = 0; i < node.Steps.Length; i++)
        {
            rightSteps.Set(i, node.Steps.Get(i));
        }
        rightSteps.Set(rightSteps.Length - 1, true);

        byte[][] split = SplitArray(orderedKeys);
        byte level = (byte)(node.Level + 1);
        node.Left = new VirtualNode()
        {
            Parent = node,
            Value = 0,
            Level = level,
            Steps = leftSteps,
        };
        node.Right = new VirtualNode()
        {
            Parent = node,
            Value = 0,
            Level = level,
            Steps = rightSteps,
        };

        CraftVirtualBranch(node.Left, split[0]);
        CraftVirtualBranch(node.Right, split[1]);
    }

    private byte[][] SplitArray(byte[] array)
    {
        if (array.Length == 2)
            return
            [
                [array[0]],
                [array[1]],
            ];
        long totalFrequency = countTotalFrequency(array);
        long perfectRightSum = totalFrequency / 2;

        byte[] left = ByteArrayUtils.ClosestSubset(array, frequencyMap, perfectRightSum);
        byte[] right = ByteArrayUtils.Subtract(array, left);

        return [left, right];
    }

    private long countTotalFrequency(byte[] bytes)
    {
        long total = 0;
        foreach (var b in bytes)
            total += frequencyMap.GetValueOrDefault(b);
        return total;
    }
}
