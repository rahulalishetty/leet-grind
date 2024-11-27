var canJump = function (nums) {
  // using Number object to simulate enum
  let Index = { GOOD: 1, BAD: 0, UNKNOWN: -1 };
  let memo = new Array(nums.length).fill(Index.UNKNOWN);
  memo[memo.length - 1] = Index.GOOD;
  for (let i = nums.length - 2; i >= 0; i--) {
    let furthestJump = Math.min(i + nums[i], nums.length - 1);
    for (let j = i + 1; j <= furthestJump; j++) {
      if (memo[j] == Index.GOOD) {
        memo[i] = Index.GOOD;
        break;
      }
    }
  }
  return memo[0] == Index.GOOD;
};
