class Fenwick {
  public int[] smallerNumbersThanCurrent(int[] nums) {
    int[] ans = new int[nums.length];
    Fenwick tree = new Fenwick(101);

    // Count frequencies
    for (int x : nums)
      tree.update(x, 1);

    // For each number: query how many < it
    for (int i = 0; i < nums.length; i++) {
      ans[i] = tree.sum(nums[i] - 1);
    }

    return ans;
  }
}

class Fenwick {
  int[] bit;

  Fenwick(int n) {
    bit = new int[n + 1];
  }

  void update(int idx, int val) {
    for (idx++; idx < bit.length; idx += idx & -idx) {
      bit[idx] += val;
    }
  }

  int sum(int idx) {
    int s = 0;
    for (idx++; idx > 0; idx -= idx & -idx) {
      s += bit[idx];
    }
    return s;
  }
}
