var canJumpPosition = function (nums) {
  const n = nums.length,
    memo = new Array(nums.length).fill(undefined);

  function traverse(position) {
    if (memo[position] != undefined) return memo[position];
    if (position == n - 1) return true;

    let furthestJump = Math.min(position + nums[position], n - 1);

    for (
      let nextPosition = position + 1;
      nextPosition <= furthestJump;
      nextPosition++
    ) {
      if (traverse(nextPosition)) {
        return (memo[position] = true);
      }
    }
    return (memo[position] = false);
  }

  return traverse(0);
};
