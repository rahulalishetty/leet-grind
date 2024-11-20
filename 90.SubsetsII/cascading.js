var subsetsWithDup = function (nums) {
  nums.sort((a, b) => a - b);
  let subsets = [[]];
  let subsetSize = 0;
  for (let i = 0; i < nums.length; i++) {
    // subsetSize refers to the size of the subset in the previous step.
    // This value also indicates the starting index of the subsets generated in this step.
    let startingIndex = i >= 1 && nums[i] == nums[i - 1] ? subsetSize : 0;
    subsetSize = subsets.length;
    for (let j = startingIndex; j < subsetSize; j++) {
      let currentSubset = [...subsets[j]];
      currentSubset.push(nums[i]);
      subsets.push(currentSubset);
    }
  }
  return subsets;
};
