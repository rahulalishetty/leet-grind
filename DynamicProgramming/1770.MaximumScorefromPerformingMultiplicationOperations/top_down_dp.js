/**
 * @param {number[]} nums
 * @param {number[]} multipliers
 * @return {number}
 */
var maximumScore = function (nums, multipliers) {
  const n = nums.length;
  const m = multipliers.length;
  const memo = [...Array(m)].map((a) => Array(m).fill(0));

  const findScore = (i, left) => {
    if (i === m) return 0;

    let mul = multipliers[i];
    let right = n - 1 - (i - left);

    if (memo[i][left] === 0) {
      memo[i][left] = Math.max(
        mul * nums[left] + findScore(i + 1, left + 1),
        mul * nums[right] + findScore(i + 1, left)
      );
    }

    return memo[i][left];
  };

  return findScore(0, 0);
};
