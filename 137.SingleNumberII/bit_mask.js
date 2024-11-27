var singleNumber = function (nums) {
  // Initialize seenOnce and seenTwice to 0
  let seenOnce = 0,
    seenTwice = 0;

  // Iterate through nums
  for (let num of nums) {
    // Update using derived equations
    seenOnce = (seenOnce ^ num) & ~seenTwice;
    seenTwice = (seenTwice ^ num) & ~seenOnce;
  }

  // Return integer which appears exactly once
  return seenOnce;
};
