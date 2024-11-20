function subsets(nums) {
  const n = nums.length,
    superset = [],
    subset = [];

  function generateSubsets(i) {
    if (i === n) {
      superset.push([...subset]);
      return;
    }
    generateSubsets(i + 1);

    subset.push(nums[i]);
    generateSubsets(i + 1);
    subset.pop();
  }

  generateSubsets(0);
  return superset;
};
