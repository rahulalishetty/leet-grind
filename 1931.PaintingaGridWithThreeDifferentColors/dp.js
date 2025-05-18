function genValidPatterns(m) {
  const res = [];

  function backtrack(pos, path) {
    if (pos === m) {
      res.push([...path]);
      return;
    }

    for (let c = 0; c < 3; c++) {
      if (pos === 0 || path[pos - 1] !== c) {
        path.push(c);
        backtrack(pos + 1, path);
        path.pop();
      }
    }
  }

  backtrack(0, []);
  return res;
}

function areCompatible(p1, p2) {
  for (let i = 0; i < p1.length; i++) {
    if (p1[i] === p2[i]) return false;
  }
  return true;
}

function colorTheGrid(m, n) {
  const MOD = 1e9 + 7;
  const patterns = genValidPatterns(m);
  const patternCount = patterns.length;

  // Map pattern arrays to string keys so we can memoize on them
  const patternToIndex = new Map(patterns.map((p, i) => [p.join(""), i]));

  // Precompute compatible patterns
  const compatible = Array.from({ length: patternCount }, () => []);
  for (let i = 0; i < patternCount; i++) {
    for (let j = 0; j < patternCount; j++) {
      if (areCompatible(patterns[i], patterns[j])) {
        compatible[i].push(j);
      }
    }
  }

  const memo = Array.from({ length: n }, () => Array(patternCount).fill(-1));

  function dp(col, prevIdx) {
    if (col === n) return 1;
    if (memo[col][prevIdx] !== -1) return memo[col][prevIdx];

    let total = 0;
    for (const nextIdx of compatible[prevIdx]) {
      total = (total + dp(col + 1, nextIdx)) % MOD;
    }

    return (memo[col][prevIdx] = total);
  }

  // First column: try every pattern as the starting pattern
  let result = 0;
  for (let i = 0; i < patternCount; i++) {
    result = (result + dp(1, i)) % MOD;
  }

  return result;
}
