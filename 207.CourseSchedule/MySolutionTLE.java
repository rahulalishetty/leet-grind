class Solution {
  private List<List<Integer>> adjList = new ArrayList<>();
  private Set<Integer> visited = new HashSet<>();

  boolean isCyclic(int node) {
    if (visited.contains(node))
      return true;
    visited.add(node);

    for (int i = 0; i < adjList.get(node).size(); i++) {
      if (isCyclic(adjList.get(node).get(i))) {
        return true;
      }
    }

    visited.remove(node);
    return false;
  }

  public boolean canFinish(int numCourses, int[][] prerequisites) {

    for (int i = 0; i < numCourses; i++) {
      adjList.add(new ArrayList<>());
    }

    for (int i = 0; i < prerequisites.length; i++) {
      int u = prerequisites[i][0];
      int v = prerequisites[i][1];

      adjList.get(u).add(v);
    }

    for (int i = 0; i < numCourses; i++) {
      if (!visited.contains(i)) {
        visited.clear();
        if (isCyclic(i)) {
          return false;
        }
      }
    }

    return true;
  }
}
