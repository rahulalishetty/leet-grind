/**
 * @param {number[]} nums
 * @return {number[][]}
 */
var subsetsWithDup = function (nums) {
  const n = nums.length,
    superset = [],
    subset = [];
  nums.sort((a, b) => a - b);

  function generateSubsets(i) {
    superset.push([...subset]);

    for (let j = i; j < n; j++) {
      if (i !== j && nums[j] === nums[j - 1]) {
        continue;
      }

      subset.push(nums[j]);
      generateSubsets(j + 1);
      subset.pop();
    }
  }

  generateSubsets(0);
  return superset;
};
