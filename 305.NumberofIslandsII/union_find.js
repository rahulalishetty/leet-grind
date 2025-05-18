function numIslands2(m, n, positions) {
  const x = [-1, 1, 0, 0],
    y = [0, 0, -1, 1];

  const uf = new UnionFind(m * n),
    answer = [];

  for (const [i, j] of positions) {
    const idx = i * n + j;
    uf.addLand(idx);

    for (let k = 0; k < 4; k++) {
      const dx = i + x[k],
        dy = j + y[k],
        newIdx = dx * n + dy;
      if (dx >= 0 && dx < m && dy >= 0 && dy < n) {
        if (uf.isLand(newIdx)) uf.union(idx, newIdx);
      }
    }

    answer.push(uf.getIslandCount());
  }

  return answer;
}

class UnionFind {
  constructor(size) {
    this.parent = Array.from({ length: size }, () => -1);
    this.rank = Array.from({ length: size }, () => 0);
    this.count = 0;
  }

  addLand(x) {
    if (this.parent[x] >= 0) return;
    this.parent[x] = x;
    this.count++;
  }

  isLand(x) {
    if (this.parent[x] >= 0) return true;
    return false;
  }

  getIslandCount() {
    return this.count;
  }

  find(x) {
    if (this.parent[x] !== x) this.parent[x] = this.find(this.parent[x]);
    return this.parent[x];
  }

  union(x, y) {
    const xSet = this.find(x),
      ySet = this.find(y);
    if (this.rank[xSet] === this.rank[ySet]) {
      return;
    } else if (this.rank[xSet] < this.rank[ySet]) {
      this.parent[xSet] = ySet;
    } else if (this.rank[xSet] > this.rank[ySet]) {
      this.parent[ySet] = xSet;
    } else {
      this.parent[ySet] = xSet;
      this.rank[xSet]++;
    }
    this.count--;
  }
}
