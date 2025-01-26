var minCut = function (s) {
  let cutsDp = new Array(s.length).fill(0);
  for (let i = 1; i < s.length; i++) {
    cutsDp[i] = i;
  }

  function findMinimumCuts(startIndex, endIndex) {
    for (
      let start = startIndex, end = endIndex;
      start >= 0 && end < s.length && s.charAt(start) == s.charAt(end);
      start--, end++
    ) {
      let newCut = start == 0 ? 0 : cutsDp[start - 1] + 1;
      cutsDp[end] = Math.min(cutsDp[end], newCut);
    }
  }

  for (let mid = 0; mid < s.length; mid++) {
    findMinimumCuts(mid, mid);
    findMinimumCuts(mid - 1, mid);
  }

  return cutsDp[s.length - 1];
};
