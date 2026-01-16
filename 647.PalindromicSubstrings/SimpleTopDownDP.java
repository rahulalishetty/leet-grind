class SimpleTopDownDP {
  public int countSubstrings(String s) {
    int n = s.length();
    // -1 = unknown, 0 = false, 1 = true
    int[][] memo = new int[n][n];
    for (int i = 0; i < n; i++) {
      java.util.Arrays.fill(memo[i], -1);
    }

    int count = 0;

    // Check every possible substring s[i..j]
    for (int i = 0; i < n; i++) {
      for (int j = i; j < n; j++) {
        if (isPalindrome(s, i, j, memo)) {
          count++;
        }
      }
    }

    return count;
  }

  // Top-down DP: returns true if s[i..j] is a palindrome
  private boolean isPalindrome(String s, int i, int j, int[][] memo) {
    if (memo[i][j] != -1) {
      return memo[i][j] == 1;
    }

    if (s.charAt(i) != s.charAt(j)) {
      memo[i][j] = 0;
      return false;
    }

    // If length 1 or 2, and ends match, it's a palindrome
    if (j - i <= 1) {
      memo[i][j] = 1;
      return true;
    }

    // Otherwise depends on the inner substring
    boolean inner = isPalindrome(s, i + 1, j - 1, memo);
    memo[i][j] = inner ? 1 : 0;
    return inner;
  }
}
