var jump = function (nums) {
  // The starting range of the first jump is [0, 0]
  let answer = 0,
    n = nums.length;
  let curEnd = 0,
    curFar = 0;
  for (let i = 0; i < n - 1; ++i) {
    // Update the farthest reachable index of this jump.
    curFar = Math.max(curFar, i + nums[i]);
    // If we finish the starting range of this jump,
    // Move on to the starting range of the next jump.
    if (i === curEnd) {
      ++answer;
      curEnd = curFar;
    }
  }
  return answer;
};
