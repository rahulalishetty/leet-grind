var singleNumber = function (nums) {
  // Count (modulo 3) bits
  let msb = 0,
    lsb = 0;
  // Process Every Num and update count bits
  for (let num of nums) {
    var new_lsb = (~msb & ~lsb & num) | (lsb & ~num);
    var new_msb = (lsb & num) | (msb & ~num);
    lsb = new_lsb;
    msb = new_msb;
  }
  // Return lsb as the answer
  return lsb;
};
