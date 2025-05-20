/**
 * @param {number[][]} logs
 * @param {number} n
 * @return {number}
 */

class DSU {
  constructor(n) {
    this.parent = new Array(n);
    this.rank = new Array(n);
    this.components = n;

    for (let i = 0; i < n; i++) {
      this.parent[i] = i;
      this.rank[i] = 0;
    }
  }

  find(u) {
    if (u == this.parent[u]) return u;
    return (this.parent[u] = this.find(this.parent[u]));
  }

  union(u, v) {
    u = this.find(u);
    v = this.find(v);

    if (u != v) {
      if (this.rank[u] < this.rank[v]) {
        let temp = u;
        u = v;
        v = temp;
      }

      this.parent[v] = u;

      if (this.rank[u] == this.rank[v]) this.rank[u]++;

      this.components--;

      return true;
    }

    return false;
  }

  getComponentSize() {
    return this.components;
  }
}

var earliestAcq = function (logs, n) {
  logs.sort((log1, log2) => log1[0] - log2[0]);
  const dsu = new DSU(n);

  for (const log of logs) {
    let [timestamp, a, b] = log;

    dsu.union(a, b);

    if (dsu.getComponentSize() == 1) return timestamp;
  }

  return -1;
};
