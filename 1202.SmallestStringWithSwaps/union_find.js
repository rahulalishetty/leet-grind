class UnionFind {
  constructor(size) {
    this.root = [];
    this.rank = [];

    for (let i = 0; i < size; i++) {
      this.root[i] = i;
      this.rank[i] = 1;
    }
  }

  find(x) {
    if (this.root[x] === x) {
      return x;
    } else {
      return (this.root[x] = this.find(this.root[x]));
    }
  }

  union(x, y) {
    const rootX = this.find(x);
    const rootY = this.find(y);

    if (rootX !== rootY) {
      if (this.rank[rootX] > this.rank[rootY]) {
        this.root[rootY] = rootX;
      } else if (this.rank[rootX] < this.rank[rootY]) {
        this.root[rootX] = rootY;
      } else {
        this.root[rootY] = rootX;
        this.rank[rootX]++;
      }
    }
  }
}

/**
 * @param {string} s
 * @param {number[][]} pairs
 * @return {string}
 */
var smallestStringWithSwaps = function (s, pairs) {
  if (s.length < 2) return s;
  const dsu = new UnionFind(s.length);
  const map = new Map();
  let result = [];

  for (let [x, y] of pairs) {
    dsu.union(x, y);
  }

  for (let i = 0; i < s.length; i++) {
    const rootIndex = dsu.find(i);
    if (!map.has(rootIndex)) {
      map.set(rootIndex, []);
    }
    map.get(rootIndex).push(s[i]);
  }

  for (let [key, arr] of map) {
    arr.sort((a, b) => b.localeCompare(a));
  }

  for (let i = 0; i < s.length; i++) {
    const rootIndex = dsu.find(i);
    result[i] = map.get(rootIndex).pop();
  }

  return result.join("");
};
