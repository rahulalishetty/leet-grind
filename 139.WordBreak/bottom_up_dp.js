/**
 * @param {string} s
 * @param {string[]} wordDict
 * @return {boolean}
 */

var wordBreak = function (s, wordDict) {
  if (wordDict.length === 0) return false;
  const n = wordDict.length,
    m = s.length,
    dp = Array(m + 1).fill(false);
  dp[0] = true;

  for (let i = 1; i <= m; i++) {
    for (const word of wordDict) {
      let j,
        k = i - 1;
      for (j = 0; j < word.length; j++) {
        if (s[k] === word[j]) k++;
        else break;
      }
      if (j === word.length && dp[i - 1]) dp[k] = true;
    }
  }
  // console.log(dp);
  return dp[m];
};
