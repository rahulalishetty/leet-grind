class BottomUp {
  public int countSubstrings(String s) {
    int n = s.length();
    boolean[][] dp = new boolean[n][n];
    int count = 0;

    // length = 1 substrings
    for (int i = 0; i < n; i++) {
      dp[i][i] = true;
      count++;
    }

    // length >= 2
    for (int len = 2; len <= n; len++) {
      for (int i = 0; i + len - 1 < n; i++) {
        int j = i + len - 1;

        if (s.charAt(i) == s.charAt(j)) {
          if (len == 2) {
            dp[i][j] = true;
          } else {
            dp[i][j] = dp[i + 1][j - 1];
          }
        } else {
          dp[i][j] = false;
        }

        if (dp[i][j]) {
          count++;
        }
      }
    }

    return count;
  }
}
