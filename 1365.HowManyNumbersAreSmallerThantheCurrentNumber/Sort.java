import java.util.*;

class Sort {
  public int[] smallerNumbersThanCurrent(int[] nums) {
    int n = nums.length;
    int[][] arr = new int[n][2];

    for (int i = 0; i < n; i++) {
      arr[i][0] = nums[i];
      arr[i][1] = i;
    }

    Arrays.sort(arr, (a, b) -> Integer.compare(a[0], b[0]));

    int[] ans = new int[n];
    int prevCount = 0;

    for (int i = 0; i < n; i++) {
      if (i == 0 || arr[i][0] != arr[i - 1][0]) {
        prevCount = i;
      }
      ans[arr[i][1]] = prevCount;
    }

    return ans;
  }
}
