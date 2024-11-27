var singleNumber = function (nums) {
  // Loner
  let loner = 0;
  // Iterate over all bits
  for (let shift = 0; shift < 32; shift++) {
    let bit_sum = 0;
    // For this bit, iterate over all integers
    for (let num of nums) {
      // Compute the bit of num, and add it to bit_sum
      bit_sum += (num >> shift) & 1;
    }
    // Compute the bit of loner and place it
    let loner_bit = bit_sum % 3;
    loner |= loner_bit << shift;
  }
  return loner;
};
