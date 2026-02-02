class SolutionBrute {
  public long countValidSubarrays(int[] nums) {
    int n = nums.length;
    long ans = 0;
    for (int l = 0; l < n; l++) {
      int base = nums[l];
      for (int r = l; r < n; r++) {
        if (nums[r] < base)
          break; // once smaller appears, longer ones are invalid too
        ans++;
      }
    }
    return ans;
  }
}
