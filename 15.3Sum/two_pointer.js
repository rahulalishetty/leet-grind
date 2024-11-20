/**
 * @param {number[]} nums
 * @return {number[][]}
 */
var threeSum = function (nums) {
  const n = nums.length,
    ans = new Set(),
    aq = [];
  nums.sort((a, b) => a - b);

  for (let i = 0; i < n - 2; i++) {
    let left = i + 1,
      right = n - 1;

    while (left < right) {
      const sum = nums[i] + nums[left] + nums[right];
      if (sum === 0) {
        const triplets = [nums[i], nums[left], nums[right]];
        ans.add(JSON.stringify(triplets));
        left++;
        right--;
      } else if (sum > 0) {
        right--;
      } else {
        left++;
      }
    }
  }

  return Array.from(ans, JSON.parse);
};
