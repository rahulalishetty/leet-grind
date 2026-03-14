class Iterative {
  private int[] tree;
  private int n;

  public NumArray(int[] nums) {
    this.n = nums.length;
    this.tree = new int[2 * n];

    // Build leaf nodes
    for (int i = 0; i < n; i++) {
      tree[n + i] = nums[i];
    }

    // Build internal nodes
    for (int i = n - 1; i > 0; i--) {
      tree[i] = tree[2 * i] + tree[2 * i + 1];
    }
  }

  public void update(int index, int val) {
    int pos = index + n;
    tree[pos] = val;

    while (pos > 1) {
      pos /= 2;
      tree[pos] = tree[2 * pos] + tree[2 * pos + 1];
    }
  }

  public int sumRange(int left, int right) {
    int l = left + n;
    int r = right + n;
    int sum = 0;

    while (l <= r) {
      if ((l % 2) == 1)
        sum += tree[l++];
      if ((r % 2) == 0)
        sum += tree[r--];
      l /= 2;
      r /= 2;
    }

    return sum;
  }
}
