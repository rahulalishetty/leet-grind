var singleNumber = function (nums) {
  nums.sort();
  for (let i = 0; i < nums.length - 1; i += 3) {
    if (nums[i] == nums[i + 1]) {
      continue;
    } else {
      return nums[i];
    }
  }
  return nums[nums.length - 1];
};
