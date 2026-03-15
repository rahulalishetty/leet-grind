class Solution {

  public int longestRepeatingSubstring(String s) {
    char[] characters = s.toCharArray();
    int start = 1, end = characters.length - 1;

    while (start <= end) {
      int mid = (start + end) / 2;
      // Check if there's a repeating substring of length mid
      if (hasRepeatingSubstring(characters, mid)) {
        start = mid + 1;
      } else {
        end = mid - 1;
      }
    }
    return start - 1;
  }

  private boolean hasRepeatingSubstring(char[] characters, int length) {
    Set<String> seenSubstrings = new HashSet<>();
    // Check for repeating substrings of given length
    for (int i = 0; i <= characters.length - length; i++) {
      String substring = new String(characters, i, length);
      if (!seenSubstrings.add(substring)) {
        return true;
      }
    }
    return false;
  }
}
