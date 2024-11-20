function partition(s) {
  const n = s.length,
    dp = Array(n)
      .fill()
      .map(() => Array(n).fill(false)),
    ans = [],
    path = [];

  function dfs(start) {
    if (start === n) ans.push([...path]);

    for (let end = start; end < n; end++) {
      if (s[start] === s[end] && (end - start <= 2 || dp[start + 1][end - 1])) {
        dp[start][end] = true;
        path.push(s.slice(start, end + 1));
        dfs(end + 1);
        path.pop();
      }
    }
  }

  dfs(0);
  return ans;
}
