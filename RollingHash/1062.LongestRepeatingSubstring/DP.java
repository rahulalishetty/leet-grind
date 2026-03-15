class Solution {

  public int longestRepeatingSubstring(String s) {
    int length = s.length();
    int[][] dp = new int[length + 1][length + 1];
    int maxLength = 0;

    // Use DP to find the longest common substring
    for (int i = 1; i <= length; i++) {
      for (int j = i + 1; j <= length; j++) {
        // If characters match, extend the length of
        // the common substring
        if (s.charAt(i - 1) == s.charAt(j - 1)) {
          dp[i][j] = dp[i - 1][j - 1] + 1;
          maxLength = Math.max(maxLength, dp[i][j]);
        }
      }
    }
    return maxLength;
  }
}
