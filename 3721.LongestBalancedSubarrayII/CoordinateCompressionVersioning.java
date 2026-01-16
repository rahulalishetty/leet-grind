class CoordinateCompressionVersioning {
  public int longestBalancedSubarray(int[] nums) {
    int n = nums.length;
    if (n == 0)
      return 0;

    // Coordinate compression: value -> id [0..m-1]
    int[] sorted = nums.clone();
    Arrays.sort(sorted);
    int m = 0;
    for (int i = 0; i < n; i++) {
      if (i == 0 || sorted[i] != sorted[i - 1])
        sorted[m++] = sorted[i];
    }
    HashMap<Integer, Integer> idMap = new HashMap<>(m * 2);
    for (int i = 0; i < m; i++)
      idMap.put(sorted[i], i);

    int[] seen = new int[m]; // seen[id] == version means present in current subarray
    int ans = 0;

    for (int l = 0; l < n; l++) {
      int version = l + 1;
      int distinctEven = 0, distinctOdd = 0;

      for (int r = l; r < n; r++) {
        int v = nums[r];
        int id = idMap.get(v);

        if (seen[id] != version) {
          seen[id] = version;
          if ((v & 1) == 0)
            distinctEven++;
          else
            distinctOdd++;
        }
        if (distinctEven == distinctOdd)
          ans = Math.max(ans, r - l + 1);
      }
    }
    return ans;
  }
}
