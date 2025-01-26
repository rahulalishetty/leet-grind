/**
 * @param {string} beginWord
 * @param {string} endWord
 * @param {string[]} wordList
 * @return {number}
 */
var ladderLength = function (beginWord, endWord, wordList) {
  if (wordList.includes(beginWord)) {
    wordList.splice(wordList.indexOf(beginWord), 1);
  }

  const graphCandidates = [beginWord, ...wordList];
  const n = graphCandidates.length,
    adj = Array(n),
    word_length = beginWord.length,
    visited = Array(n).fill(false);
  const endIdx = graphCandidates.indexOf(endWord);

  for (let i = 0; i < n; i++) {
    const neighbors = [],
      word = graphCandidates[i];
    for (let j = 0; j < n; j++) {
      let count = 0,
        counter_word = graphCandidates[j];

      for (let k = 0; k < word_length; k++) {
        if (word[k] !== counter_word[k]) count++;
      }

      if (count === 1) neighbors.push(j);
    }

    adj[i] = { neighbors, word, i };
  }

  let q = [0],
    len = 1;
  visited[0] = true;
  while (q.length) {
    const length = q.length;
    for (let i = 0; i < length; i++) {
      const node = q.shift();
      const neighbors = adj[node].neighbors;

      if (node === endIdx) {
        return len;
      }

      neighbors.forEach((neighbor) => {
        if (!visited[neighbor]) {
          visited[neighbor] = true;
          q.push(neighbor);
        }
      });
    }
    len++;
  }

  return 0;
};
