/**
 * @param {number[]} nums
 * @return {number[][]}
 */
var permute = function (nums) {
  let ans = [];

  function backtrack(cur) {
    if (cur.length === nums.length) {
      ans.push([...cur]);
    }

    for (let i in nums) {
      if (!cur.includes(nums[i])) {
        cur.push(nums[i]);
        backtrack(cur);
        cur.splice(-1);
      }
    }
  }

  backtrack([]);
  return ans;
};
