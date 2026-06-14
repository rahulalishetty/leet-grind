class TopDownDP {
  private String s;
  private int[] memo;

  public int longestValidParentheses(String s) {
    this.s = s;
    int n = s.length();
    this.memo = new int[n];

    for (int i = 0; i < n; i++) {
      memo[i] = -1;
    }

    int ans = 0;
    for (int i = 0; i < n; i++) {
      ans = Math.max(ans, solve(i));
    }
    return ans;
  }

  private int solve(int i) {
    if (i < 0)
      return 0;
    if (memo[i] != -1)
      return memo[i];

    int res = 0;

    // A valid parentheses substring ending at i must end with ')'
    if (s.charAt(i) == ')') {
      // Case 1: ...()
      if (i - 1 >= 0 && s.charAt(i - 1) == '(') {
        res = 2 + solve(i - 2);
      }
      // Case 2: ...))
      else if (i - 1 >= 0 && s.charAt(i - 1) == ')') {
        int prevLen = solve(i - 1);
        int j = i - prevLen - 1;

        if (j >= 0 && s.charAt(j) == '(') {
          res = prevLen + 2 + solve(j - 1);
        }
      }
    }

    memo[i] = res;
    return res;
  }
}
