class Fenwick {
  public int findNumberOfLIS(int[] nums) {
    // Step 1: Coordinate compression
    TreeSet<Integer> set = new TreeSet<>();
    for (int num : nums)
      set.add(num);
    Map<Integer, Integer> index = new HashMap<>();
    int rank = 1;
    for (int val : set)
      index.put(val, rank++);

    // Step 2: BIT where each node stores (maxLength, count)
    FenwickTree tree = new FenwickTree(rank + 2); // 1-based indexing

    for (int num : nums) {
      int i = index.get(num);
      // Query [1, i-1] — all values less than num
      Pair res = tree.query(i - 1);
      int len = res.length + 1;
      int cnt = res.count == 0 ? 1 : res.count;

      // Update at index i with (len, cnt)
      tree.update(i, new Pair(len, cnt));
    }

    Pair result = tree.query(rank + 1);
    return result.count;
  }

  // Helper class for (length, count)
  static class Pair {
    int length, count;

    Pair(int l, int c) {
      length = l;
      count = c;
    }
  }

  static class FenwickTree {
    Pair[] bit;
    int n;

    FenwickTree(int size) {
      n = size;
      bit = new Pair[n];
      for (int i = 0; i < n; i++) {
        bit[i] = new Pair(0, 0);
      }
    }

    void update(int i, Pair p) {
      while (i < n) {
        if (bit[i].length < p.length) {
          bit[i].length = p.length;
          bit[i].count = p.count;
        } else if (bit[i].length == p.length) {
          bit[i].count += p.count;
        }
        i += i & -i;
      }
    }

    Pair query(int i) {
      int maxLen = 0, totalCount = 0;
      while (i > 0) {
        if (bit[i].length > maxLen) {
          maxLen = bit[i].length;
          totalCount = bit[i].count;
        } else if (bit[i].length == maxLen) {
          totalCount += bit[i].count;
        }
        i -= i & -i;
      }
      return new Pair(maxLen, totalCount);
    }
  }
}
