class BruteForce {
  public int[] findErrorNums(int[] nums) {
    int n = nums.length;
    int sum = n * (n + 1) / 2, runingSum = 0, extra = -1;
    Set<Integer> unique = new HashSet<>();

    for (int num : nums) {
      if (unique.add(num)) {
        runingSum += num;
      } else {
        extra = num;
      }
    }

    return new int[] { extra, sum - runingSum };
  }
}
