public class OptimalSlidingWindow {
  public int lengthOfLongestSubstring(String s) {
    Integer[] chars = new Integer[128];
    int maxLength = 0, left = 0, right = 0;

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);

      Integer index = chars[c];
      if (index != null && index >= left && index < right) {
        left = index + 1;
      }

      maxLength = Math.max(maxLength, right - left + 1);
      chars[c] = right++;
    }

    return maxLength;
  }
}
