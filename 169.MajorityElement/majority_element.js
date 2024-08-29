function majorityElement(nums) {
  const n = nums.length,
    cache = {};
  const m_element_threshold = Math.ceil(n / 2);

  for (const [, value] of nums.entries()) {
    if (cache.hasOwnProperty(value)) {
      cache[value]++;
    } else {
      cache[value] = 1;
    }
    if (cache[value] === m_element_threshold) return value;
  }
}

console.log(majorityElement([3, 2, 3]));
console.log(majorityElement([2, 2, 1, 1, 1, 2, 2]));
