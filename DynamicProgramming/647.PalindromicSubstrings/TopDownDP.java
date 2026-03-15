class TopDownDP {
  public int countSubstrings(String s) {
    int n = s.length();
    // dp[i][j] = number of palindromic substrings in s[i..j]
    int[][] dp = new int[n][n];
    // -1 = unknown; otherwise exact count
    for (int i = 0; i < n; i++) {
      Arrays.fill(dp[i], -1);
    }

    // pal[i][j]: -1 unknown, 0 false, 1 true
    int[][] pal = new int[n][n];
    for (int i = 0; i < n; i++) {
      Arrays.fill(pal[i], -1);
    }

    return countPalindromes(s, 0, n - 1, dp, pal);
  }

  // Top-down DP: number of palindromic substrings in s[i..j]
  private int countPalindromes(String s, int i, int j,
      int[][] dp, int[][] pal) {
    if (i > j) {
      return 0;
    }
    if (dp[i][j] != -1) {
      return dp[i][j];
    }

    if (i == j) {
      // Single character: always one palindrome
      dp[i][j] = 1;
      pal[i][j] = 1; // also mark as palindrome
      return 1;
    }

    int res = 0;

    // inclusion-exclusion on 2D interval
    res += countPalindromes(s, i + 1, j, dp, pal);
    res += countPalindromes(s, i, j - 1, dp, pal);
    res -= countPalindromes(s, i + 1, j - 1, dp, pal);

    // If s[i..j] itself is palindrome, add 1
    if (isPal(s, i, j, pal)) {
      res += 1;
    }

    dp[i][j] = res;
    return res;
  }

  // Top-down palindrome DP
  private boolean isPal(String s, int i, int j, int[][] pal) {
    if (pal[i][j] != -1) {
      return pal[i][j] == 1;
    }

    if (s.charAt(i) != s.charAt(j)) {
      pal[i][j] = 0;
      return false;
    }

    if (j - i <= 1) {
      // length 1 or 2, ends equal ⇒ palindrome
      pal[i][j] = 1;
      return true;
    }

    boolean inner = isPal(s, i + 1, j - 1, pal);
    pal[i][j] = inner ? 1 : 0;
    return inner;
  }
}
