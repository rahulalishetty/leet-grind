import java.util.*;

class Solution {
  private static final int INF = 1_000_000_000;

  private String s1, s2;
  private int n, m;
  private int[][] memo;
  private boolean[][] seen;

  public String minWindow(String s1, String s2) {
    this.s1 = s1;
    this.s2 = s2;
    this.n = s1.length();
    this.m = s2.length();

    memo = new int[n + 1][m + 1];
    seen = new boolean[n + 1][m + 1];

    int bestLen = n + 1;
    int bestEnd = -1;

    // Same as your loop checking dp[i][m] for i=1..n
    for (int i = 1; i <= n; i++) {
      int len = dp(i, m);
      if (len < bestLen) {
        bestLen = len;
        bestEnd = i;
      }
    }

    return bestLen > n ? "" : s1.substring(bestEnd - bestLen, bestEnd);
  }

  // Top-down version of your dp[i][j]
  // dp(i,j) = 1 + (match ? dp(i-1,j-1) : dp(i-1,j))
  private int dp(int i, int j) {
    // Base cases (exactly like the bottom-up initialization)
    if (j == 0)
      return 0; // dp[i][0] = 0
    if (i == 0)
      return INF; // dp[0][j>0] = INF

    if (seen[i][j])
      return memo[i][j];
    seen[i][j] = true;

    char c1 = s1.charAt(i - 1);
    char c2 = s2.charAt(j - 1);

    int prev = (c1 == c2) ? dp(i - 1, j - 1) : dp(i - 1, j);

    // careful: INF + 1 still INF-ish
    int ans = (prev >= INF) ? INF : 1 + prev;

    memo[i][j] = ans;
    return ans;
  }
}
