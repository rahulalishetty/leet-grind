public class RunningMinimum {
  public int sumSubarrayMinsQuadratic(int[] arr) {
    int n = arr.length;
    long MOD = 1_000_000_007L;
    long ans = 0;

    for (int i = 0; i < n; i++) {
      int currentMin = Integer.MAX_VALUE;
      for (int j = i; j < n; j++) {
        currentMin = Math.min(currentMin, arr[j]);
        ans = (ans + currentMin) % MOD;
      }
    }

    return (int) ans;
  }
}
