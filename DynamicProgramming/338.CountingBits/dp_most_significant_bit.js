/**
 * In general, we have the following transition function for popcount P(x):
 * P(x+b)=P(x)+1,b=(2^m)>x
 * With this transition function, we can then apply Dynamic Programming to generate all the pop counts starting from 0.
 */

function countBits(n) {
  const ans = new Array(n + 1).fill(0);
  let x = 0,
    b = 1;

  while (b <= n) {
    while (x < b && x + b <= n) {
      ans[x + b] = ans[x] + 1;
      x++;
    }
    x = 0;
    b <<= 1; // b = 2b
  }

  return ans;
}

console.log(countBits(5));
