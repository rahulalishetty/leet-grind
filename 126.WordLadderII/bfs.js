/**
 * @param {string} beginWord
 * @param {string} endWord
 * @param {string[]} wordList
 * @return {string[][]}
 */

var findLadders = function (beginWord, endWord, wordList) {
  if (wordList.includes(beginWord)) {
    wordList.splice(wordList.indexOf(beginWord), 1);
  }

  const graphCandidates = [beginWord, ...wordList];
  const n = graphCandidates.length,
    adj = Array(n),
    word_length = beginWord.length,
    visited = Array(n).fill(false),
    visited_neighbors = Array(n).fill(false);
  const endIdx = graphCandidates.indexOf(endWord),
    traversalPath = {},
    curPath = [endWord],
    paths = [];

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

  function backtrack(source, destination) {
    if (source === destination) {
      const path = Array.from(curPath);
      path.reverse();
      paths.push(path);
      return;
    }

    if (!traversalPath[source]) return;

    traversalPath[source].forEach((neighbor) => {
      curPath.push(neighbor);
      backtrack(neighbor, destination);
      curPath.pop();
    });
  }

  let q = [0];
  visited[0] = true;
  visited_neighbors[0] = true;
  while (q.length) {
    const length = q.length,
      cur_visited_neighbors = [];
    for (let i = 0; i < length; i++) {
      const node = q.shift();
      const neighbors = adj[node].neighbors;
      const word = graphCandidates[node];

      if (node === endIdx) {
        backtrack(endWord, beginWord);
        return paths;
      }

      neighbors.forEach((neighbor) => {
        if (!visited_neighbors[neighbor]) {
          const neightbor_word = graphCandidates[neighbor];
          if (!traversalPath[neightbor_word])
            traversalPath[neightbor_word] = [];
          traversalPath[neightbor_word].push(word);
          cur_visited_neighbors.push(neighbor);
        }

        if (!visited[neighbor]) {
          visited[neighbor] = true;
          q.push(neighbor);
        }
      });
    }
    cur_visited_neighbors.forEach((neighbor) => {
      visited_neighbors[neighbor] = true;
    });
  }

  return [];
};
