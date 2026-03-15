class Solution {

  public int longestRepeatingSubstring(String s) {
    int length = s.length();
    String[] suffixes = new String[length];

    // Create suffix array
    for (int i = 0; i < length; i++) {
      suffixes[i] = s.substring(i);
    }
    // Sort the suffixes
    Arrays.sort(suffixes);

    int maxLength = 0;
    // Find the longest common prefix between consecutive sorted suffixes
    for (int i = 1; i < length; i++) {
      int j = 0;
      while (j < Math.min(suffixes[i].length(), suffixes[i - 1].length()) &&
          suffixes[i].charAt(j) == suffixes[i - 1].charAt(j)) {
        j++;
      }
      maxLength = Math.max(maxLength, j);
    }
    return maxLength;
  }
}
