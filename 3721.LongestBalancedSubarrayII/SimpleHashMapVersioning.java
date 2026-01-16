public class SimpleHashMapVersioning {
  public int longestBalancedSubarray(int[] nums) {
    int n = nums.length;
    if (n == 0)
      return 0;

    HashMap<Integer, Integer> lastSeenVersion = new HashMap<>(n * 2);
    int ans = 0;

    for (int l = 0; l < n; l++) {
      int version = l + 1;
      int distinctEven = 0, distinctOdd = 0;

      for (int r = l; r < n; r++) {
        int v = nums[r];

        Integer ver = lastSeenVersion.get(v);
        if (ver == null || ver != version) {
          lastSeenVersion.put(v, version);
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
