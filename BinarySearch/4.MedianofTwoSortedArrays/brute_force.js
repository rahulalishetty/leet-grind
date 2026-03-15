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
