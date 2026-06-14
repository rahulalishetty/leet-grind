class UnionFind {
  /**
   * Implementation of UnionFind without load-balancing.
   */
  private int[] group;
  private int[] rank;

  public UnionFind(int size) {
    // container to hold the group id for each member
    // Note: the index of member starts from 1,
    // thus we add one more element to the container.
    group = new int[size + 1];
    rank = new int[size + 1];
    for (int i = 0; i < size + 1; ++i) {
      group[i] = i;
      rank[i] = 0;
    }
  }

  /**
   * return the group id that the person belongs to.
   */
  public int find(int person) {
    if (group[person] != person) {
      group[person] = find(group[person]);
    }
    return group[person];
  }

  /**
   * Join the groups together.
   * return:
   * false when the two persons belong to the same group already,
   * otherwise true
   */
  public boolean union(int person1, int person2) {
    int group1 = find(person1);
    int group2 = find(person2);
    if (group1 == group2) {
      return false;
    }

    // attach the group of lower rank to the one with higher rank
    if (rank[group1] > rank[group2]) {
      group[group2] = group1;
    } else if (rank[group1] < rank[group2]) {
      group[group1] = group2;
    } else {
      group[group1] = group2;
      rank[group2] += 1;
    }

    return true;
  }
}

class Kruskal {
  public int minCostToSupplyWater(int n, int[] wells, int[][] pipes) {
    List<int[]> orderedEdges = new ArrayList<>(n + 1 + pipes.length);

    // add the virtual vertex (index with 0) along with the new edges.
    for (int i = 0; i < wells.length; ++i) {
      orderedEdges.add(new int[] { 0, i + 1, wells[i] });
    }

    // add the existing edges
    for (int i = 0; i < pipes.length; ++i) {
      int[] edge = pipes[i];
      orderedEdges.add(edge);
    }

    // sort the edges based on their cost
    Collections.sort(orderedEdges, (a, b) -> a[2] - b[2]);

    // iterate through the ordered edges
    UnionFind uf = new UnionFind(n);
    int totalCost = 0;
    for (int[] edge : orderedEdges) {
      int house1 = edge[0];
      int house2 = edge[1];
      int cost = edge[2];
      // determine if we should add the new edge to the final MST
      if (uf.union(house1, house2)) {
        totalCost += cost;
      }
    }

    return totalCost;
  }
}
