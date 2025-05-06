/**
 * @param {string} s
 * @param {string[]} wordDict
 * @return {string[]}
 */
var wordBreak = function (s, wordDict) {
  function Trie(c, isEnd = false) {
    this.val = c;
    this.children = {};
    this.isEnd = isEnd;
  }

  const root = new Trie(null),
    res = [];

  for (let word of wordDict) {
    let node = root;
    for (let c of word) {
      if (!node.children[c]) {
        node.children[c] = new Trie(c);
      }
      node = node.children[c];
    }
    node.isEnd = true;
  }

  function dfs(node, idx, path) {
    if (idx === s.length) {
      res.push(path.join(" "));
      return;
    }

    for (let i = idx, cur = node; i < s.length; i++) {
      if (!cur.children[s[i]]) {
        return;
      }
      cur = cur.children[s[i]];
      if (cur.isEnd) {
        dfs(root, i + 1, [...path, s.slice(idx, i + 1)], res);
      }
    }
  }

  dfs(root, 0, []);
  return res;
};
