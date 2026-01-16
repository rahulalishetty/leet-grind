import java.util.*;

public class BruteForce {
  public long countSubarrays(int[] nums, int target) {
    int n = nums.length;
    int[] prefTarget = new int[n + 1];
    for (int i = 0; i < n; i++) {
      prefTarget[i + 1] = prefTarget[i] + (nums[i] == target ? 1 : 0);
    }

    long ans = 0;
    for (int l = 0; l < n; l++) {
      for (int r = l; r < n; r++) {
        int len = r - l + 1;
        int t = prefTarget[r + 1] - prefTarget[l];
        if (t * 2 > len)
          ans++;
      }
    }
    return ans;
  }
}
