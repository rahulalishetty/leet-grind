function majorityElement(nums) {
  let candidate,
    count = 0;

  for (const [, value] of nums.entries()) {
    if (count == 0) {
      candidate = value;
    }
    count += candidate == value ? 1 : -1;
  }

  return candidate;
}

console.log(majorityElement([3, 2, 3]));
console.log(majorityElement([2, 2, 1, 1, 1, 2, 2]));
