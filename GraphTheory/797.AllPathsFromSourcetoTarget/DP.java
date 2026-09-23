class Solution {
  private int target;
  private int[][] graph;
  private HashMap<Integer, List<List<Integer>>> memo;

  protected List<List<Integer>> allPathsToTarget(int currNode) {
    // memoization. check the result in the cache first
    if (memo.containsKey(currNode))
      return memo.get(currNode);

    List<List<Integer>> results = new ArrayList<>();
    // base case
    if (currNode == this.target) {
      ArrayList<Integer> path = new ArrayList<>();
      path.add(target);
      results.add(path);
      return results;
    }

    // iterate through the paths starting from each neighbor.
    for (int nextNode : this.graph[currNode]) {
      for (List<Integer> path : allPathsToTarget(nextNode)) {
        ArrayList<Integer> newPath = new ArrayList<>();
        newPath.add(currNode);
        newPath.addAll(path);
        results.add(newPath);
      }
    }
    memo.put(currNode, results);
    return results;
  }

  public List<List<Integer>> allPathsSourceTarget(int[][] graph) {

    this.target = graph.length - 1;
    this.graph = graph;
    this.memo = new HashMap<>();

    return this.allPathsToTarget(0);
  }
}
