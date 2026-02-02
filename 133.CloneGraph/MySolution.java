/*
// Definition for a Node.
class Node {
    public int val;
    public List<Node> neighbors;
    public Node() {
        val = 0;
        neighbors = new ArrayList<Node>();
    }
    public Node(int _val) {
        val = _val;
        neighbors = new ArrayList<Node>();
    }
    public Node(int _val, ArrayList<Node> _neighbors) {
        val = _val;
        neighbors = _neighbors;
    }
}
*/

class Solution {
  private Map<Integer, Node> visited = new HashMap<>();

  public Node cloneGraph(Node node) {
    if (node == null)
      return node;

    Node curNode = new Node(node.val);
    visited.put(node.val, curNode);

    List<Node> neighbors = node.neighbors;

    for (Node neighbor : neighbors) {
      if (!visited.containsKey(neighbor.val)) {
        cloneGraph(neighbor);
      }

      Node newNeighbor = visited.get(neighbor.val);
      if (!curNode.neighbors.contains(newNeighbor))
        curNode.neighbors.add(newNeighbor);
      if (!newNeighbor.neighbors.contains(curNode))
        newNeighbor.neighbors.add(curNode);
    }

    return curNode;
  }
}
