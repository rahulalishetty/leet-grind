public class Contribution {
  public int sumSubarrayMins(int[] arr) {
    int n = arr.length;
    long MOD = 1_000_000_007L;

    int[] prevLess = new int[n];
    int[] nextLess = new int[n];

    Deque<Integer> stack = new ArrayDeque<>();

    // Previous less (strictly <
    for (int i = 0; i < n; i++) {
      while (!stack.isEmpty() && arr[stack.peek()] > arr[i]) {
        stack.pop();
      }
      prevLess[i] = stack.isEmpty() ? -1 : stack.peek();
      stack.push(i);
    }

    stack.clear();

    // Next less-or-equal (<=)
    for (int i = n - 1; i >= 0; i--) {
      while (!stack.isEmpty() && arr[stack.peek()] >= arr[i]) {
        stack.pop();
      }
      nextLess[i] = stack.isEmpty() ? n : stack.peek();
      stack.push(i);
    }

    long ans = 0;
    for (int i = 0; i < n; i++) {
      long left = i - prevLess[i];
      long right = nextLess[i] - i;
      long contrib = (long) arr[i] * left * right;
      ans = (ans + contrib) % MOD;
    }

    return (int) ans;
  }
}
