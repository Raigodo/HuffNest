package com.huffnest.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ByteArrayUtils {

  /**
   * Returns elements from 'set' that are NOT consumed by 'subset'.
   * Multiset-aware (handles duplicates correctly).
   */
  public static byte[] subtract(byte[] set, byte[] subset) {
    if (set == null || set.length == 0) {
      return new byte[0];
    }

    // Count occurrences in subset
    Map<Byte, Integer> subsetCount = new HashMap<>();
    if (subset != null) {
      for (byte b : subset) {
        subsetCount.merge(b, 1, Integer::sum);
      }
    }

    // Build result
    List<Byte> result = new ArrayList<>();

    for (byte b : set) {
      int count = subsetCount.getOrDefault(b, 0);
      if (count > 0) {
        // consume one occurrence
        subsetCount.put(b, count - 1);
      } else {
        result.add(b);
      }
    }

    // convert to primitive array
    byte[] out = new byte[result.size()];
    for (int i = 0; i < result.size(); i++) {
      out[i] = result.get(i);
    }

    return out;
  }

  /**
   * Find subset of x (each element used at most once) whose sum of frequencies
   * (from the map) is closest to the target.
   *
   * @param x           array of byte items (may contain duplicates)
   * @param frequencies map from Byte -> Long weight/frequency (missing keys treated as 0L)
   * @param target      target sum to approach
   * @return subset of x as a byte[] whose frequency-sum is closest to target
   */
  public static byte[] closestSubset(
    byte[] x,
    Map<Byte, Long> frequencies,
    long target
  ) {
    if (x == null || x.length == 0) return new byte[0];

    // reachable sums and parent pointers for reconstruction:
    // parentIndex.get(sum) -> index in x that was used to reach this sum
    // parentSum.get(sum) -> previous sum before adding that index
    Map<Long, Integer> parentIndex = new HashMap<>();
    Map<Long, Long> parentSum = new HashMap<>();
    Set<Long> sums = new HashSet<>();

    sums.add(0L);
    parentIndex.put(0L, -1); // root
    parentSum.put(0L, -1L);

    for (int i = 0; i < x.length; i++) {
      long w = frequencies.getOrDefault(x[i], 0L);

      // snapshot current sums to iterate (avoid concurrent modification)
      List<Long> snapshot = new ArrayList<>(sums);

      for (long s : snapshot) {
        long ns = s + w;
        // record first time we see this new sum (keeps the earliest subset that achieves ns)
        if (!parentIndex.containsKey(ns)) {
          parentIndex.put(ns, i);
          parentSum.put(ns, s);
          sums.add(ns);
        }
      }
    }

    // find the reachable sum closest to target
    long bestSum = Long.MIN_VALUE;
    long bestDiff = Long.MAX_VALUE;
    for (long s : sums) {
      long diff = Math.abs(s - target);
      if (diff < bestDiff) {
        bestDiff = diff;
        bestSum = s;
      } else if (diff == bestDiff) {
        // tie-break: prefer the smaller sum (change if you prefer larger)
        if (s < bestSum) bestSum = s;
      }
    }

    // reconstruct subset from bestSum using parent maps
    List<Byte> chosen = new ArrayList<>();
    long cur = bestSum;
    while (cur != 0L && cur != -1L) {
      Integer idx = parentIndex.get(cur);
      Long prev = parentSum.get(cur);
      if (idx == null || prev == null) break; // safety
      chosen.add(x[idx]);
      cur = prev;
    }

    // reverse order (optional) and to primitive array
    Collections.reverse(chosen);
    byte[] result = new byte[chosen.size()];
    for (int i = 0; i < chosen.size(); i++) result[i] = chosen.get(i);
    return result;
  }
}
