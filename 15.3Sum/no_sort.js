var threeSum = function (nums) {
  const res = new Set();
  const dups = new Set();
  const seen = new Map();
  for (let i = 0; i < nums.length; ++i)
    if (!dups.has(nums[i])) {
      dups.add(nums[i]);
      for (let j = i + 1; j < nums.length; ++j) {
        let complement = -nums[i] - nums[j];
        if (seen.has(complement) && seen.get(complement) === i) {
          let triplet = [nums[i], nums[j], complement].sort((a, b) => a - b);
          res.add(JSON.stringify(triplet));
        }
        seen.set(nums[j], i);
      }
    }
  return Array.from(res, JSON.parse);
};
