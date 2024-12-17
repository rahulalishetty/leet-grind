var grayCode = function (n) {
  let result = [];
  grayCodeHelper(n);
  return result;
  function grayCodeHelper(n) {
    if (n === 0) {
      result.push(0);
      return;
    }
    // derive the n bits sequence from the (n - 1) bits sequence.
    grayCodeHelper(n - 1);
    let currentSequenceLength = result.length;
    // Set the bit at position n - 1 (0 indexed) and assign it to mask.
    let mask = 1 << (n - 1);
    for (let i = currentSequenceLength - 1; i >= 0; i--) {
      // mask is used to set the (n - 1)th bit from the LSB of all the numbers present in the current sequence.
      result.push(result[i] | mask);
    }
    return;
  }
};
