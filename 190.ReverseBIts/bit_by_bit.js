function bit_by_bit(n) {
  let bits = "",
    trailing_zero_length = 32 - bits.length;

  while (n > 0) {
    bits += n % 2;
    n = Math.floor(n / 2);
  }

  while (trailing_zero_length--) {
    bits += 0;
  }

  return bits.split("").reduce((acc, bit) => {
    acc *= 2;
    acc += Number(bit);
    return acc;
  }, 0);
}

/**
 * 
 * @param {*} n 
 * @returns 
  bit complicated working with 32 bit manipulation in javascript as 31 bit set is a negative number
  The << operator is defined as working on signed 32-bit integers
  The only JavaScript operator that works using unsigned 32-bit integers is >>>. 
  You can exploit this to convert a signed-integer-in-Number.
 */

// TODO: try other methods that work in js
function better_bit_by_bit(n) {
  let ret = 0,
    power = 31;

  while (n > 0) {
    ret += ((n & 1) << power) >>> 0;
    n = n >> 1;
    power--;
  }

  return ret;
}
