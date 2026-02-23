using System.Collections;

namespace HuffmanTree.Factory;

public class OldTreeBuilder
{
    public OldTreeBuilder()
    {
        bufer = new();
        root = new()
        {
            Value = 0,
            Level = 0,
            Steps = new BitArray(0),
        };
        populator = new(root);
    }

    private Queue<byte> bufer;
    private VirtualNode root;
    private LazyTreePopulator populator;

    public void PushBytes(byte[] bytes)
    {
        for (int i = 0; i < bytes.Length; i++)
        {
            bufer.Enqueue(bytes[i]);
        }
        while (bufer.Count >= 2)
        {
            populator.PopulateNextNode(bufer.Dequeue(), bufer.Dequeue());
        }
    }

    public void PushByte(byte value)
    {
        bufer.Enqueue(value);
        if (bufer.Count >= 2)
        {
            populator.PopulateNextNode(bufer.Dequeue(), bufer.Dequeue());
        }
    }

    public Tree Build()
    {
        return new Tree(root.Materialize());
    }

    private class LazyTreePopulator
    {
        public LazyTreePopulator(VirtualNode root)
        {
            previousNode = root;
        }

        private VirtualNode previousNode;

        public void PopulateNextNode(byte value, byte level)
        {
            var node = GetNextNode(previousNode, previousNode, level);
            if (node == null)
                throw new Exception("Tree is full yet new bytes are incomming");
            previousNode = node;
            node.Value = value;
        }

        private VirtualNode? GetNextNode(VirtualNode previous, VirtualNode current, byte nextLevel)
        {
            if (current.Left == null && (previous != current || previous.Level == 0))
            {
                for (int i = current.Level; i < nextLevel; i++)
                {
                    InflateNode(current!);
                    current = current.Left!;
                }
                return current;
            }

            if (current.Parent != null && current.Parent.Right != current)
            {
                current = current.Parent.Right!;
                if (nextLevel == current.Level)
                {
                    return current;
                }
                return GetNextNode(previous, current, nextLevel);
            }

            if (current.Parent != null)
            {
                previous = current.Parent;
                current = current.Parent;
                return GetNextNode(previous, current, nextLevel);
            }

            return null;
        }

        private void InflateNode(VirtualNode node)
        {
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
        }
    }
}
