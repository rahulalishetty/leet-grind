function colorTheGrid(m, n) {
  const MOD = 1e9 + 7;

  // Generate all valid m-row column patterns
  function genValidPatterns(m) {
    const res = [];
    function dfs(pos, lastColor, mask) {
      if (pos === m) {
        res.push(mask);
        return;
      }
      for (let c = 0; c < 3; c++) {
        if (c !== lastColor) {
          dfs(pos + 1, c, (mask << 2) | c);
        }
      }
    }
    dfs(0, -1, 0);
    return res;
  }

  // Check compatibility between two bitmask patterns
  function areCompatible(p1, p2, m) {
    for (let i = 0; i < m; i++) {
      const c1 = (p1 >> (2 * i)) & 3;
      const c2 = (p2 >> (2 * i)) & 3;
      if (c1 === c2) return false;
    }
    return true;
  }

  const patterns = genValidPatterns(m);
  const P = patterns.length;

  // Precompute compatible pattern transitions
  const compatible = Array.from({ length: P }, () => []);
  for (let i = 0; i < P; i++) {
    for (let j = 0; j < P; j++) {
      if (areCompatible(patterns[i], patterns[j], m)) {
        compatible[i].push(j);
      }
    }
  }

  // Bottom-up DP
  let dp = new Array(P).fill(1); // Base: first column
  for (let col = 1; col < n; col++) {
    const next = new Array(P).fill(0);
    for (let i = 0; i < P; i++) {
      for (const j of compatible[i]) {
        next[j] = (next[j] + dp[i]) % MOD;
      }
    }
    dp = next;
  }

  // Sum up all possible ways to end with any pattern
  return dp.reduce((sum, val) => (sum + val) % MOD, 0);
}
