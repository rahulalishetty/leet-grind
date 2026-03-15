var canJumpPosition = function (nums) {
  const n = nums.length;

  function traverse(position) {
    if (position == n - 1) return true;

    let furthestJump = Math.min(position + nums[position], n - 1);

    for (
      let nextPosition = position + 1;
      nextPosition <= furthestJump;
      nextPosition++
    ) {
      if (traverse(nextPosition)) {
        return true;
      }
    }
    return false;
  }

  return traverse(0);
};
