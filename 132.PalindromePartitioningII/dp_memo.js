/**
 * @param {string} s
 * @return {number}
 */
var minCut = function (s) {
  const n = s.length,
    memoCuts = Array.from({ length: n }, () => Array(n).fill(-1)),
    memoPalindrome = Array.from({ length: n }, () => Array(n).fill(null));

  function isPalindrome(start, end) {
    if (start >= end) return true;
    if (memoPalindrome[start][end] !== null) return memoPalindrome[start][end];
    memoPalindrome[start][end] =
      s[start] === s[end] && isPalindrome(start + 1, end - 1);
    return memoPalindrome[start][end];
  }

  function find(start, end) {
    if (start === end || isPalindrome(s, start, end)) {
      return 0;
    }

    if (memoCuts[start][end] !== -1) {
      return memoCuts[start][end];
    }

    let cuts = Infinity;
    for (let idx = start; idx <= end; idx++) {
      if (isPalindrome(start, idx)) {
        cuts = Math.min(cuts, 1 + find(idx + 1, end));
      }
    }

    return (memoCuts[start][end] = cuts);
  }

  return find(0, n - 1);
};
