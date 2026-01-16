public class BruteForce {
  public int sumSubarrayMinsBruteForce(int[] arr) {
    int n = arr.length;
    long MOD = 1_000_000_007L;
    long ans = 0;

    for (int i = 0; i < n; i++) {
      for (int j = i; j < n; j++) {
        int minVal = Integer.MAX_VALUE;
        for (int k = i; k <= j; k++) {
          minVal = Math.min(minVal, arr[k]);
        }
        ans = (ans + minVal) % MOD;
      }
    }

    return (int) ans;
  }
}
