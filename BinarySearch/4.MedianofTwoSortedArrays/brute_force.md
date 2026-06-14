```js
/**
 * @param {number[]} nums1
 * @param {number[]} nums2
 * @return {number}
 */
var findMedianSortedArrays = function (nums1, nums2) {
  const merged = nums1.concat(nums2);
  merged.sort((a, b) => a - b);
  const l = merged.length;

  if (l & 1) {
    return merged[Math.floor(l / 2)];
  } else {
    const mid = Math.floor(l / 2);
    return (merged[mid] + merged[mid - 1]) / 2;
  }
};
```

```java
import java.util.Arrays;

public class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int n1 = nums1.length;
        int n2 = nums2.length;
        int[] merged = new int[n1 + n2];

        System.arraycopy(nums1, 0, merged, 0, n1);
        System.arraycopy(nums2, 0, merged, n1, n2);

        Arrays.sort(merged);

        int l = merged.length;
        if ((l & 1) == 1) { // odd
            return merged[l / 2];
        } else { // even
            int mid = l / 2;
            return (merged[mid] + merged[mid - 1]) / 2.0;
        }
    }
}
```
