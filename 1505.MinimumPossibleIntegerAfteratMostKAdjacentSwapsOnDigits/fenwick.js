/**
 * @param {string} num
 * @param {number} k
 * @return {string}
 */
var minInteger = function (num, k) {
  const n = num.length;
  const buckets = Array.from({ length: 10 }, () => []);
  // store 1-based indices
  for (let i = 0; i < n; i++) buckets[num.charCodeAt(i) - 48].push(i + 1);

  const head = new Array(10).fill(0);

  class BIT {
    constructor(n) {
      this.n = n;
      this.t = new Int32Array(n + 1);
    }
    add(i, v) {
      for (; i <= this.n; i += i & -i) this.t[i] += v;
    }
    sum(i) {
      let s = 0;
      for (; i > 0; i -= i & -i) s += this.t[i];
      return s;
    }
  }

  const bit = new BIT(n);
  for (let i = 1; i <= n; i++) bit.add(i, 1); // all positions present

  const res = [];
  for (let pick = 0; pick < n; pick++) {
    for (let d = 0; d <= 9; d++) {
      const h = head[d];
      if (h >= buckets[d].length) continue;
      const pos = buckets[d][h]; // original 1-based position
      const cost = bit.sum(pos) - 1; // #remaining before pos
      if (cost <= k) {
        k -= cost;
        res.push(String.fromCharCode(48 + d));
        bit.add(pos, -1); // remove this position
        head[d] = h + 1; // consume
        break;
      }
    }
  }
  return res.join("");
};
