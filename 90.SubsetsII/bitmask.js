var subsetsWithDup = function (nums) {
  var n = nums.length;
  // Sort the generated subset. This will help to identify duplicates.
  nums.sort();
  var subsets = [];
  var seen = new Set(); // To store the previously seen sets.
  var maxNumberOfSubsets = Math.pow(2, n);
  for (var subsetIndex = 0; subsetIndex < maxNumberOfSubsets; subsetIndex++) {
    // Append subset corresponding to that bitmask.
    var currentSubset = [];
    var hashcode = "";
    for (var j = 0; j < n; j++) {
      // Generate the bitmask
      var mask = 1 << j;
      var isSet = mask & subsetIndex;
      if (isSet != 0) {
        currentSubset.push(nums[j]);
        // Generate the hashcode by creating a comma separated string of numbers in the currentSubset.
        hashcode += nums[j] + ",";
      }
    }
    if (!seen.has(hashcode)) {
      subsets.push(currentSubset);
      seen.add(hashcode);
    }
  }
  return subsets;
};
