/**
 * @param {number} n
 * @return {number}
 */
var numTilings = function (n) {
  const mod = 1e9 + 7,
    pCache = Array(n + 1).fill(-1),
    fCache = Array(n + 1).fill(-1);

  function p(n) {
    if (pCache[n] !== -1) return pCache[n];

    let val;
    if (n === 2) val = 1;
    else val = (p(n - 1) + f(n - 2)) % mod;

    return (pCache[n] = val);
  }

  function f(n) {
    if (fCache[n] !== -1) return fCache[n];

    let val;
    if (n === 1) val = 1;
    else if (n === 2) val = 2;
    else {
      val = (f(n - 1) + f(n - 2) + 2 * p(n - 1)) % mod;
    }

    return (fCache[n] = val);
  }

  return f(n);
};
