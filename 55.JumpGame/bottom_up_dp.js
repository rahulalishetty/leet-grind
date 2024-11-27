var canJumpPosition = function (nums) {
  const n = nums.length,
    memo = new Array(nums.length).fill(undefined);
  memo[0] = true;
  for (let i = 0; i < n && memo[i]; i++) {
    let furthestJump = Math.min(i + nums[i], n - 1);
    for (let j = i + 1; j <= furthestJump; j++) {
      memo[j] = true;
    }
  }

  return memo[n - 1] == true;
};
