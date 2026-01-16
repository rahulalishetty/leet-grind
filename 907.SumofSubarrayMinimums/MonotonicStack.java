public class MonotonicStack {
  public int sumSubarrayMins(int[] arr) {
    int n = arr.length;
    long MOD = 1_000_000_007L;

    long[] dp = new long[n];
    Deque<Integer> stack = new ArrayDeque<>();

    long ans = 0;

    for (int i = 0; i < n; i++) {
      // Maintain a strictly increasing stack by value
      while (!stack.isEmpty() && arr[stack.peek()] >= arr[i]) {
        stack.pop();
      }

      if (stack.isEmpty()) {
        dp[i] = (long) arr[i] * (i + 1);
      } else {
        int prev = stack.peek();
        dp[i] = dp[prev] + (long) arr[i] * (i - prev);
      }

      dp[i] %= MOD;
      ans = (ans + dp[i]) % MOD;
      stack.push(i);
    }

    return (int) ans;
  }
}
