public class BruteForce {
  public int trapBruteForce(int[] height) {
    int n = height.length;
    int water = 0;

    for (int i = 0; i < n; i++) {
      int maxLeft = 0;
      for (int j = 0; j <= i; j++) {
        maxLeft = Math.max(maxLeft, height[j]);
      }

      int maxRight = 0;
      for (int j = i; j < n; j++) {
        maxRight = Math.max(maxRight, height[j]);
      }

      int level = Math.min(maxLeft, maxRight);
      water += Math.max(0, level - height[i]);
    }

    return water;
  }
}
