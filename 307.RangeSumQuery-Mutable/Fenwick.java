class Fenwick {
  private int[] bit; // Binary Indexed Tree
  private int[] nums; // Original array
  private int n;

  public NumArray(int[] nums) {
    this.n = nums.length;
    this.nums = new int[n];
    this.bit = new int[n + 1];

    for (int i = 0; i < n; i++) {
      update(i, nums[i]); // reuses update() to build
    }
  }

  private void add(int index, int delta) {
    index += 1; // BIT is 1-based
    while (index <= n) {
      bit[index] += delta;
      index += index & -index; // move to next responsible node
    }
  }

  private int prefixSum(int index) {
    index += 1; // BIT is 1-based
    int sum = 0;
    while (index > 0) {
      sum += bit[index];
      index -= index & -index; // move to parent
    }
    return sum;
  }

  public void update(int index, int val) {
    int delta = val - nums[index];
    nums[index] = val;
    add(index, delta);
  }

  public int sumRange(int left, int right) {
    return prefixSum(right) - prefixSum(left - 1);
  }
}
