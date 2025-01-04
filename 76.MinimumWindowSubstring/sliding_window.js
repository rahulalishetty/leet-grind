var minWindow = function (s, t) {
  if (s.length === 0 || t.length === 0) {
    return "";
  }
  // Dictionary which keeps a count of all the unique characters in t.
  let dictT = new Map();
  for (let i = 0; i < t.length; i++) {
    let count = dictT.get(t.charAt(i)) || 0;
    dictT.set(t.charAt(i), count + 1);
  }
  // Number of unique characters in t, which need to be present in the desired window.
  let required = dictT.size;
  // Left and Right pointer
  let l = 0,
    r = 0;
  // formed is used to keep track of how many unique characters in t
  // are present in the current window in its desired frequency.
  // e.g. if t is "AABC" then the window must have two A's, one B and one C.
  // Thus formed would be = 3 when all these conditions are met.
  let formed = 0;
  // Dictionary which keeps a count of all the unique characters in the current window.
  let windowCounts = new Map();
  // ans list of the form (window length, left, right)
  let ans = [-1, 0, 0];
  while (r < s.length) {
    // Add one character from the right to the window
    let c = s.charAt(r);
    let count = windowCounts.get(c) || 0;
    windowCounts.set(c, count + 1);
    // If the frequency of the current character added equals to the
    // desired count in t then increment the formed count by 1.
    if (dictT.has(c) && windowCounts.get(c) === dictT.get(c)) {
      formed++;
    }
    // Try and contract the window till the point where it ceases to be 'desirable'.
    while (l <= r && formed === required) {
      c = s.charAt(l);
      // Save the smallest window until now.
      if (ans[0] === -1 || r - l + 1 < ans[0]) {
        ans[0] = r - l + 1;
        ans[1] = l;
        ans[2] = r;
      }
      // The character at the position pointed by the
      // `Left` pointer is no longer a part of the window.
      windowCounts.set(c, windowCounts.get(c) - 1);
      if (dictT.has(c) && windowCounts.get(c) < dictT.get(c)) {
        formed--;
      }
      // Move the left pointer ahead, this would help to look for a new window.
      l++;
    }
    // Keep expanding the window once we are done contracting.
    r++;
  }
  return ans[0] === -1 ? "" : s.substring(ans[1], ans[2] + 1);
};
