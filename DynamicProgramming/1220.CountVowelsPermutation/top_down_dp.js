/**
 * @param {number} n
 * @return {number}
 */
var countVowelPermutation = function (n) {
  const vowels = ["a", "e", "i", "o", "u"],
    mod = 1e9 + 7,
    dp = [...Array(n)].map((e) => Array(6).fill(0));

  function checkRules(prev, next) {
    switch (prev) {
      case "a": {
        return next === "e";
      }
      case "e": {
        return next === "a" || next === "i";
      }
      case "i": {
        return next !== "i";
      }
      case "o": {
        return next === "i" || next === "u";
      }
      case "u": {
        return next === "a";
      }
    }
  }

  function find(i, prev) {
    if (i === n) return 1;

    const prevIdx = vowels.indexOf(prev) + 1;
    if (dp[i][prevIdx] !== -1) return dp[i][prevIdx];

    let count = 0;

    for (let j = 0; j < 5; j++) {
      const next = vowels[j];
      if (prev === "" || checkRules(prev, next)) {
        count = (count + find(i + 1, next)) % mod;
      }
    }

    return (dp[i][prevIdx] = count);
  }

  return find(0, "");
};
