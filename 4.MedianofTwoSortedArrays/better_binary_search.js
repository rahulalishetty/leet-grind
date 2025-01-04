var findMedianSortedArrays = function (nums1, nums2) {
  if (nums1.length > nums2.length) {
    let temp = nums1;
    nums1 = nums2;
    nums2 = temp;
  }

  let m = nums1.length,
    n = nums2.length;
  let left = 0,
    right = m;

  while (left <= right) {
    let partitionA = Math.floor((left + right) / 2);
    let partitionB = Math.floor((m + n + 1) / 2 - partitionA);

    let maxLeftA =
      partitionA == 0 ? Number.MIN_SAFE_INTEGER : nums1[partitionA - 1];
    let minRightA =
      partitionA == m ? Number.MAX_SAFE_INTEGER : nums1[partitionA];
    let maxLeftB =
      partitionB == 0 ? Number.MIN_SAFE_INTEGER : nums2[partitionB - 1];
    let minRightB =
      partitionB == n ? Number.MAX_SAFE_INTEGER : nums2[partitionB];

    if (maxLeftA <= minRightB && maxLeftB <= minRightA) {
      if ((m + n) % 2 == 0) {
        return (
          (Math.max(maxLeftA, maxLeftB) + Math.min(minRightA, minRightB)) / 2.0
        );
      } else {
        return Math.max(maxLeftA, maxLeftB);
      }
    } else if (maxLeftA > minRightB) {
      right = partitionA - 1;
    } else {
      left = partitionA + 1;
    }
  }
  return 0.0;
};
