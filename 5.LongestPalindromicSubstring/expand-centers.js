function longestPalindrome(s) {
  let ans = "";

  function expand(left, right) {
    while (left >= 0 && right < s.length && s[left] === s[right]) {
      left--;
      right++;
    }

    return s.slice(left + 1, right);
  }

  for (let i = 0; i < s.length; i++) {
    let odd = expand(i, i);
  }
}
