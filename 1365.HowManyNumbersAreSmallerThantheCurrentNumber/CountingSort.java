class CountingSort {
  public int[] smallerNumbersThanCurrent(int[] nums) {
    int[] freq = new int[101];

    // Count occurrences
    for (int n : nums)
      freq[n]++;

    // Prefix sums: prefix[i] = how many numbers < i
    for (int i = 1; i <= 100; i++) {
      freq[i] += freq[i - 1];
    }

    int n = nums.length;
    int[] ans = new int[n];
    for (int i = 0; i < n; i++) {
      int val = nums[i];
      ans[i] = val == 0 ? 0 : freq[val - 1];
    }

    return ans;
  }
}
