var minCut = function (s) {
  let cuts = new Array(s.length),
    palindrome = new Array(s.length)
      .fill()
      .map(() => new Array(s.length).fill(false));
  for (let end = 0; end < s.length; end++) {
    let minimumCut = end;
    for (let start = 0; start <= end; start++) {
      // check if substring (start, end) is palindrome
      if (
        s.charAt(start) == s.charAt(end) &&
        (end - start <= 2 || palindrome[start + 1][end - 1])
      ) {
        palindrome[start][end] = true;
        minimumCut = start == 0 ? 0 : Math.min(minimumCut, cuts[start - 1] + 1);
      }
    }
    cuts[end] = minimumCut;
  }
  return cuts[s.length - 1];
};
