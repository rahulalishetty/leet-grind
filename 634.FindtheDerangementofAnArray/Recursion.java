public class Recursion {
  public int findDerangement(int n) {
    Integer[] memo = new Integer[n + 1];
    return find(n, memo);
  }

  public int find(int n, Integer[] memo) {
    if (n == 0)
      return 1;
    if (n == 1)
      return 0;
    if (memo[n] != null)
      return memo[n];
    memo[n] = (int) (((n - 1L) * (find(n - 1, memo) + find(n - 2, memo))) % 1000000007);
    return memo[n];
  }
}
