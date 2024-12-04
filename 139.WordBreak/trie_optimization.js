class TrieNode {
  constructor() {
    this.isWord = false;
    this.children = {};
  }
}

function wordBreak(s, wordDict) {
  let root = new TrieNode();
  for (let word of wordDict) {
    let curr = root;
    for (let c of word) {
      if (!curr.children[c]) {
        curr.children[c] = new TrieNode();
      }
      curr = curr.children[c];
    }
    curr.isWord = true;
  }
  const dp = Array(s.length).fill(false);
  for (let i = 0; i < s.length; i++) {
    if (i === 0 || dp[i - 1]) {
      let curr = root;
      for (let j = i; j < s.length; j++) {
        let c = s[j];
        if (!curr.children[c]) {
          // No words exist
          break;
        }
        curr = curr.children[c];
        if (curr.isWord) {
          dp[j] = true;
        }
      }
    }
  }
  return dp[s.length - 1];
}
