class ExpandCenters {
  public int countSubstrings(String s) {
    int n = s.length();
    int count = 0;

    for (int center = 0; center < n; center++) {
      // Odd-length palindromes, center at (center, center)
      count += expandFromCenter(s, center, center);
      // Even-length palindromes, center at (center, center+1)
      count += expandFromCenter(s, center, center + 1);
    }

    return count;
  }

  private int expandFromCenter(String s, int left, int right) {
    int n = s.length();
    int localCount = 0;

    while (left >= 0 && right < n && s.charAt(left) == s.charAt(right)) {
      localCount++;
      left--;
      right++;
    }

    return localCount;
  }
}
