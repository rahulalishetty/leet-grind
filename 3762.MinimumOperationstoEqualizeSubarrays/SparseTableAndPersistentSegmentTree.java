import java.util.*;

public class SparseTableAndPersistentSegmentTree {

  // -------------------- Range Min/Max (Sparse Table) --------------------
  static final class RangeMinMaxSparseTable {
    private final int n;
    private final int[] log2;
    private final int[][] minTable;
    private final int[][] maxTable;

    RangeMinMaxSparseTable(int[] values) {
      this.n = values.length;

      this.log2 = new int[n + 1];
      for (int i = 2; i <= n; i++)
        log2[i] = log2[i / 2] + 1;

      int levels = log2[n] + 1;
      this.minTable = new int[levels][n];
      this.maxTable = new int[levels][n];

      System.arraycopy(values, 0, minTable[0], 0, n);
      System.arraycopy(values, 0, maxTable[0], 0, n);

      for (int level = 1; level < levels; level++) {
        int len = 1 << level;
        int half = len >>> 1;
        for (int i = 0; i + len <= n; i++) {
          minTable[level][i] = Math.min(minTable[level - 1][i], minTable[level - 1][i + half]);
          maxTable[level][i] = Math.max(maxTable[level - 1][i], maxTable[level - 1][i + half]);
        }
      }
    }

    int queryMin(int left, int right) {
      int len = right - left + 1;
      int level = log2[len];
      return Math.min(minTable[level][left], minTable[level][right - (1 << level) + 1]);
    }

    int queryMax(int left, int right) {
      int len = right - left + 1;
      int level = log2[len];
      return Math.max(maxTable[level][left], maxTable[level][right - (1 << level) + 1]);
    }
  }

  // -------------------- Persistent Segment Tree --------------------
  static final class PersistentOrderStatisticTree {

    static final class Node {
      final Node left;
      final Node right;
      final int count; // how many elements in this segment
      final long sum; // sum of original values in this segment

      Node(Node left, Node right, int count, long sum) {
        this.left = left;
        this.right = right;
        this.count = count;
        this.sum = sum;
      }
    }

    private static final Node EMPTY = new Node(null, null, 0, 0);

    private final long[] coordinate; // sorted unique values of x
    private final int coordSize;
    private final Node[] prefixRoots; // prefixRoots[i] = tree built from x[0..i-1]

    PersistentOrderStatisticTree(long[] xValues) {
      long[] coord = xValues.clone();
      Arrays.sort(coord);

      int unique = 0;
      for (int i = 0; i < coord.length; i++) {
        if (i == 0 || coord[i] != coord[i - 1])
          coord[unique++] = coord[i];
      }
      this.coordinate = Arrays.copyOf(coord, unique);
      this.coordSize = unique;

      int n = xValues.length;
      this.prefixRoots = new Node[n + 1];
      prefixRoots[0] = EMPTY;

      for (int i = 0; i < n; i++) {
        int pos = Arrays.binarySearch(coordinate, xValues[i]);
        prefixRoots[i + 1] = add(prefixRoots[i], 0, coordSize - 1, pos, xValues[i]);
      }
    }

    Node rootAtPrefix(int prefixLength) {
      return prefixRoots[prefixLength];
    }

    // Build a new version with +1 count and +value at position pos
    private static Node add(Node prev, int segL, int segR, int pos, long value) {
      if (segL == segR) {
        return new Node(null, null, prev.count + 1, prev.sum + value);
      }
      int mid = (segL + segR) >>> 1;

      Node prevLeft = (prev.left == null) ? EMPTY : prev.left;
      Node prevRight = (prev.right == null) ? EMPTY : prev.right;

      if (pos <= mid) {
        Node newLeft = add(prevLeft, segL, mid, pos, value);
        return new Node(newLeft, prevRight, prev.count + 1, prev.sum + value);
      } else {
        Node newRight = add(prevRight, mid + 1, segR, pos, value);
        return new Node(prevLeft, newRight, prev.count + 1, prev.sum + value);
      }
    }

    // kth smallest (1-indexed) in (rightRoot - leftRoot)
    long kthSmallest(Node leftRoot, Node rightRoot, int k) {
      return kthSmallest(leftRoot, rightRoot, 0, coordSize - 1, k);
    }

    private long kthSmallest(Node leftRoot, Node rightRoot, int segL, int segR, int k) {
      if (segL == segR)
        return coordinate[segL];

      int mid = (segL + segR) >>> 1;

      Node leftLeft = (leftRoot.left == null) ? EMPTY : leftRoot.left;
      Node rightLeft = (rightRoot.left == null) ? EMPTY : rightRoot.left;

      int countInLeftHalf = rightLeft.count - leftLeft.count;
      if (k <= countInLeftHalf) {
        return kthSmallest(leftLeft, rightLeft, segL, mid, k);
      }

      Node leftRight = (leftRoot.right == null) ? EMPTY : leftRoot.right;
      Node rightRight = (rightRoot.right == null) ? EMPTY : rightRoot.right;
      return kthSmallest(leftRight, rightRight, mid + 1, segR, k - countInLeftHalf);
    }

    // returns [count, sum] for values with coordinate index <= maxIndex within
    // (rightRoot - leftRoot)
    long[] queryPrefixByIndex(Node leftRoot, Node rightRoot, int maxIndex) {
      return queryPrefixByIndex(leftRoot, rightRoot, 0, coordSize - 1, maxIndex);
    }

    private long[] queryPrefixByIndex(Node leftRoot, Node rightRoot, int segL, int segR, int maxIndex) {
      if (maxIndex < segL)
        return new long[] { 0, 0 };
      if (segR <= maxIndex) {
        return new long[] { rightRoot.count - leftRoot.count, rightRoot.sum - leftRoot.sum };
      }

      int mid = (segL + segR) >>> 1;
      Node leftLeft = (leftRoot.left == null) ? EMPTY : leftRoot.left;
      Node rightLeft = (rightRoot.left == null) ? EMPTY : rightRoot.left;

      Node leftRight = (leftRoot.right == null) ? EMPTY : leftRoot.right;
      Node rightRight = (rightRoot.right == null) ? EMPTY : rightRoot.right;

      long[] a = queryPrefixByIndex(leftLeft, rightLeft, segL, mid, maxIndex);
      long[] b = queryPrefixByIndex(leftRight, rightRight, mid + 1, segR, maxIndex);
      return new long[] { a[0] + b[0], a[1] + b[1] };
    }

    int indexOfValue(long value) {
      return Arrays.binarySearch(coordinate, value);
    }
  }

  // -------------------- Main API --------------------
  public long[] minOperations(int[] nums, int step, int[][] queries) {
    int n = nums.length;

    // Invariant under +/- step: residue mod step never changes
    int[] residueModStep = new int[n];
    long[] normalized = new long[n]; // x[i] = (nums[i] - residue) / step

    for (int i = 0; i < n; i++) {
      int residue = Math.floorMod(nums[i], step);
      residueModStep[i] = residue;
      normalized[i] = (nums[i] - (long) residue) / (long) step;
    }

    RangeMinMaxSparseTable residueRange = new RangeMinMaxSparseTable(residueModStep);
    PersistentOrderStatisticTree orderStats = new PersistentOrderStatisticTree(normalized);

    long[] answer = new long[queries.length];

    for (int qi = 0; qi < queries.length; qi++) {
      int left = queries[qi][0];
      int right = queries[qi][1];

      // Feasibility: all residues must match
      int minResidue = residueRange.queryMin(left, right);
      int maxResidue = residueRange.queryMax(left, right);
      if (minResidue != maxResidue) {
        answer[qi] = -1;
        continue;
      }

      int length = right - left + 1;
      int medianRank = (length + 1) / 2; // lower median

      PersistentOrderStatisticTree.Node beforeLeft = orderStats.rootAtPrefix(left);
      PersistentOrderStatisticTree.Node beforeRightPlus1 = orderStats.rootAtPrefix(right + 1);

      long median = orderStats.kthSmallest(beforeLeft, beforeRightPlus1, medianRank);
      int medianIndex = orderStats.indexOfValue(median);

      long[] leftCountSum = orderStats.queryPrefixByIndex(beforeLeft, beforeRightPlus1, medianIndex);
      long countLE = leftCountSum[0];
      long sumLE = leftCountSum[1];

      long totalCount = length;
      long totalSum = beforeRightPlus1.sum - beforeLeft.sum;

      long countGT = totalCount - countLE;
      long sumGT = totalSum - sumLE;

      long ops = (median * countLE - sumLE) + (sumGT - median * countGT);
      answer[qi] = ops;
    }

    return answer;
  }
}
