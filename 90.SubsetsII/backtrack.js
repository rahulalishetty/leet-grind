/**
 * @param {number[]} nums
 * @return {number[][]}
 */
var subsetsWithDup = function (nums) {
  const n = nums.length,
    superset = [],
    subset = [],
    cache = new Set();
  nums.sort((a, b) => a - b);

  function generateSubsets(i) {
    if (i === n) {
      if (!cache.has(JSON.stringify(subset))) {
        superset.push(Array.from(subset, JSON.parse));
        cache.add(JSON.stringify(subset));
      }
      return;
    }
    generateSubsets(i + 1);

    subset.push(nums[i]);
    generateSubsets(i + 1);
    subset.pop();
  }

  generateSubsets(0);
  return superset;
};
