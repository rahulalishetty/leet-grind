var jump = function (nums) {
  const n = nums.length,
    memo = new Array(nums.length).fill(Infinity);
  memo[0] = 0;

  for (let i = 0; i < n; i++) {
    const furthestJump = Math.min(n - 1, nums[i] + i);

    for (let j = i + 1; j <= furthestJump; j++) {
      memo[j] = Math.min(memo[j], memo[i] + 1);
    }
  }

  return memo[n - 1];
};
