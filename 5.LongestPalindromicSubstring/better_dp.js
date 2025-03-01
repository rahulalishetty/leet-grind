function longestPalindrome(s) {
  let n = s.length;
  let dp = Array.from({ length: n }, () => new Array(n).fill(false));
  let ans = [0, 0];

  for (let i = 0; i < n; i++) {
    dp[i][i] = true;
  }

  for (let i = 0; i < n - 1; i++) {
    if (s[i] === s[i + 1]) {
      dp[i][i + 1] = true;
      ans = [i, i + 1];
    }
  }

  for (let diff = 2; diff < n; diff++) {
    for (let i = 0; i < n - diff; i++) {
      let j = i + diff;
      if (s[i] === s[j] && dp[i + 1][j - 1]) {
        dp[i][j] = true;
        ans = [i, j];
      }
    }
  }

  let i = ans[0];
  let j = ans[1];
  return s.slice(i, j + 1);
}
