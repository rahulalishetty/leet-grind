function countBits(n) {
  const ans = [];

  for (let x = 0; x <= n; x++) {
    ans.push(hammingWeight(x));
  }
}

function hammingWeight(x) {
  let count;
  for (count = 0; x != 0; count++) {
    x &= x - 1; // zeroing out the least significant nonzero bit
  }
  return count;
}
