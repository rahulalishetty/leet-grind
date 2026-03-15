function partition(s) {
  const n = s.length,
    ans = [];

  function isPalindrome(substring) {
    let left = 0,
      right = substring.length - 1;
    while (left < right) {
      if (substring[left++] !== substring[right--]) return false;
    }
    return true;
  }

  function find(substring, path) {
    if (substring.length === 0) {
      ans.push([...path]);
      return;
    }

    for (let i = 0; i < substring.length; i++) {
      const cur_substring = substring.slice(0, i + 1);

      if (isPalindrome(cur_substring)) {
        find(substring.slice(i + 1), path.concat(cur_substring));
      }
    }
  }

  find(s, []);
  return ans;
}
