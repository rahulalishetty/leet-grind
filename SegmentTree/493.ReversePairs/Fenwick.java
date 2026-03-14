class Fenwick {
  public int reversePairs(int[] nums) {
    List<Long> allValues = new ArrayList<>();
    for (int num : nums) {
      allValues.add((long) num);
      allValues.add((long) num * 2);
    }

    // Coordinate compression
    Set<Long> set = new HashSet<>(allValues);
    List<Long> sorted = new ArrayList<>(set);
    Collections.sort(sorted);
    Map<Long, Integer> index = new HashMap<>();
    for (int i = 0; i < sorted.size(); i++) {
      index.put(sorted.get(i), i + 1); // 1-based indexing for BIT
    }

    FenwickTree tree = new FenwickTree(sorted.size());
    int count = 0;

    // Traverse from right to left
    for (int i = nums.length - 1; i >= 0; i--) {
      long half = nums[i];
      // Query how many nums[j] < nums[i]/2, i.e., nums[j] <= nums[i] / 2
      count += tree.query(index.get((long) nums[i]) - 1);
      // Insert 2 * nums[i]
      tree.update(index.get((long) nums[i] * 2), 1);
    }

    return count;
  }

  class FenwickTree {
    int[] bit;
    int n;

    public FenwickTree(int size) {
      n = size + 2;
      bit = new int[n];
    }

    void update(int i, int delta) {
      while (i < n) {
        bit[i] += delta;
        i += i & -i;
      }
    }

    int query(int i) {
      int sum = 0;
      while (i > 0) {
        sum += bit[i];
        i -= i & -i;
      }
      return sum;
    }
  }
}
