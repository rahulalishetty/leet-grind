/**
  Last set bit is the rightmost set bit. Setting that bit to zero with the bit trick, 
  x &= x - 1, leads to the following transition function:
  P(x)=P(x&(x−1))+1
*/

function countBits(n) {
  const ans = new Array(n + 1).fill(0);
  for (let x = 1; x <= n; x++) {
    ans[x] = ans[x & (x - 1)] + 1;
  }
  return ans;
}
