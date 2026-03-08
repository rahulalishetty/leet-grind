class Solution {

  public int longestRepeatingSubstring(String s) {
    int length = s.length(), maxLength = 0;
    Set<String> seenSubstrings = new HashSet<>();

    for (int start = 0; start < length; start++) {
      int end = start;
      // Stop if it's not possible to find a longer repeating substring
      if (end + maxLength >= length) {
        return maxLength;
      }
      // Generate substrings of length maxLength + 1
      String currentSubstring = s.substring(end, end + maxLength + 1);
      // If a repeating substring is found, increase maxLength and restart
      if (!seenSubstrings.add(currentSubstring)) {
        start = -1; // Restart search for new length
        seenSubstrings.clear();
        maxLength++;
      }
    }
    return maxLength;
  }
}
