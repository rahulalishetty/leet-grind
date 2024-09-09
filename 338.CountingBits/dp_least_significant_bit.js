/**
  Let look at the relation between x and x′=x/2
  x=(1001011101)=(605) 
  x′=(100101110)=(302)
  We can see that x′ is differ than x by one bit, because x′
  can be considered as the result of removing the least significant bit of x.
  Thus, we have the following transition function of pop count P(x):
  P(x)=P(x/2)+(xmod2)
*/

function countBits(n) {
  const ans = new Array(n + 1).fill(0);
  for (let x = 1; x <= n; x++) {
    ans[x] = ans[x >> 1] + (x & 1);
  }
  return ans;
}
