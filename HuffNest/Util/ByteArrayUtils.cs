namespace Util;

public static class ByteArrayUtils
{
    public static byte[] Subtract(byte[]? set, byte[]? subset)
    {
        if (set is null || set.Length == 0)
            return Array.Empty<byte>();

        // Count occurrences in subset
        Dictionary<byte, int>? subsetCount = null;

        if (subset is { Length: > 0 })
        {
            subsetCount = new Dictionary<byte, int>(subset.Length);

            foreach (var b in subset)
            {
                subsetCount.TryGetValue(b, out var count);
                subsetCount[b] = count + 1;
            }
        }

        // Build result
        var result = new List<byte>(set.Length);

        foreach (var b in set)
        {
            if (subsetCount != null && subsetCount.TryGetValue(b, out var count) && count > 0)
            {
                subsetCount[b] = count - 1; // consume
            }
            else
            {
                result.Add(b);
            }
        }

        return result.ToArray();
    }

    public static byte[] ClosestSubset(
        byte[]? x,
        IReadOnlyDictionary<byte, long> frequencies,
        long target
    )
    {
        if (x is null || x.Length == 0)
            return Array.Empty<byte>();

        // parentIndex[sum] -> index in x used to reach sum
        // parentSum[sum]   -> previous sum before adding that index
        var parentIndex = new Dictionary<long, int>();
        var parentSum = new Dictionary<long, long>();
        var sums = new HashSet<long> { 0L };
        parentIndex[0L] = -1;
        parentSum[0L] = -1L;

        for (int i = 0; i < x.Length; i++)
        {
            var b = x[i];
            frequencies.TryGetValue(b, out var weight);

            // snapshot to avoid modifying during enumeration
            var snapshot = new List<long>(sums);

            foreach (var s in snapshot)
            {
                var ns = s + weight;

                // record first time we reach this sum
                if (parentIndex.ContainsKey(ns))
                    continue;

                parentIndex[ns] = i;
                parentSum[ns] = s;
                sums.Add(ns);
            }
        }

        // find reachable sum closest to target
        long bestSum = 0;
        long bestDiff = long.MaxValue;

        foreach (var s in sums)
        {
            var diff = Math.Abs(s - target);

            if (diff < bestDiff || (diff == bestDiff && s < bestSum))
            {
                bestDiff = diff;
                bestSum = s;
            }
        }

        // reconstruct subset
        var chosen = new List<byte>();
        var cur = bestSum;

        while (cur != 0L && cur != -1L)
        {
            if (
                !parentIndex.TryGetValue(cur, out var idx)
                || !parentSum.TryGetValue(cur, out var prev)
            )
                break; // safety

            chosen.Add(x[idx]);
            cur = prev;
        }

        chosen.Reverse();
        return chosen.ToArray();
    }
}
