class Solution {
  public String encode(String s) {
    int n = s.length();
    String[][] dp = new String[n][n];

    for (int len = 1; len <= n; len++) {
      for (int i = 0; i + len - 1 < n; i++) {
        int j = i + len - 1;
        String sub = s.substring(i, j + 1);
        dp[i][j] = sub; // default: no encoding

        // Try splitting into two parts
        for (int k = i; k < j; k++) {
          String cand = dp[i][k] + dp[k + 1][j];
          if (cand.length() < dp[i][j].length())
            dp[i][j] = cand;
        }

        // Try compressing by repetition: m[pattern]
        String rep = compressIfRepeated(sub, dp, i, j);
        if (rep.length() < dp[i][j].length())
          dp[i][j] = rep;
      }
    }

    return dp[0][n - 1];
  }

  // If sub is made by repeating a smaller pattern, return m[encodedPattern], else
  // return sub.
  private String compressIfRepeated(String sub, String[][] dp, int i, int j) {
    int L = sub.length();

    // Find smallest repeating unit using (sub + sub).indexOf(sub, 1)
    // If sub is periodic, it will appear again before position L.
    String doubled = sub + sub;
    int pos = doubled.indexOf(sub, 1);
    if (pos >= L)
      return sub; // not periodic

    int unitLen = pos;
    if (L % unitLen != 0)
      return sub;

    int times = L / unitLen;
    // pattern is sub[0..unitLen-1], but we want its best encoding from dp
    String encodedUnit = dp[i][i + unitLen - 1];
    String cand = times + "[" + encodedUnit + "]";
    return cand.length() < sub.length() ? cand : sub;
  }
}
