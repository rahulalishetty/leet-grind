function countDistinctPalSubseq(s) {
  const MOD = 1e9 + 7;
  const n = s.length;
  if (n === 0) return 0;

  // Build prevPos[i][c] = last index ≤ i where s[idx] === c, or -1
  // and nextPos[i][c] = first index ≥ i where s[idx] === c, or -1
  const prevPos = Array.from({ length: n }, () => Array(26).fill(-1));
  const nextPos = Array.from({ length: n }, () => Array(26).fill(-1));

  // Fill prevPos
  {
    const last = Array(26).fill(-1);
    for (let i = 0; i < n; i++) {
      last[s.charCodeAt(i) - 97] = i;
      for (let c = 0; c < 26; c++) {
        prevPos[i][c] = last[c];
      }
    }
  }

  // Fill nextPos
  {
    const next = Array(26).fill(-1);
    for (let i = n - 1; i >= 0; i--) {
      next[s.charCodeAt(i) - 97] = i;
      for (let c = 0; c < 26; c++) {
        nextPos[i][c] = next[c];
      }
    }
  }

  // Memo table: dp[i][j] = count for s[i..j]
  const dp = Array.from({ length: n }, () => Array(n).fill(-1));

  function solve(i, j) {
    if (i > j) return 0;
    if (dp[i][j] !== -1) return dp[i][j];

    let total = 0;
    for (let c = 0; c < 26; c++) {
      const left = nextPos[i][c];
      if (left === -1 || left > j) continue;
      const right = prevPos[j][c];

      if (left === right) {
        // single-letter palindrome "c"
        total = (total + 1) % MOD;
      } else {
        // wrap all inside plus "c" and "cc"
        total = (total + solve(left + 1, right - 1) + 2) % MOD;
      }
    }

    return (dp[i][j] = total);
  }

  return solve(0, n - 1);
}

// Example
console.log(countDistinctPalSubseq("bccb")); // output: 6
