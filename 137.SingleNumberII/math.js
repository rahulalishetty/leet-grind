// s_set = x1 + x2 + ... + xk + y
// s_nums = 3 (x1 + x2 + ... + xk) + y
// y = ((3 * s_set) - s_nums) / 2

var singleNumber = function (nums) {
  // We convert the integer array to a set to remove duplicates
  let numsSet = new Set(nums);
  let sumNums = nums.reduce((a, b) => a + b, 0);
  // We sum the unique elements in the set
  let sumSet = Array.from(numsSet.values()).reduce((a, b) => a + b, 0);
  // We subtract the original sum from the triple of the unique sum
  // This gives us twice the number that only appears once, so we divide it by 2
  return (3 * sumSet - sumNums) / 2;
};
