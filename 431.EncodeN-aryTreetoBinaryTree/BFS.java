/*
// Definition for a Node.
class Node {
    public int val;
    public List<Node> children;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
};
*/

/*
// Definition for a binary tree node.
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
   TreeNode(int x) { val = x; }
}
*/

class Pair<U, V> {
  public U first;
  public V second;

  public Pair(U first, V second) {
    this.first = first;
    this.second = second;
  }
}

class BFS {

  // Encodes an n-ary tree to a binary tree.
  public TreeNode encode(Node root) {
    if (root == null) {
      return null;
    }
    TreeNode newRoot = new TreeNode(root.val);
    Pair<TreeNode, Node> head = new Pair<TreeNode, Node>(newRoot, root);

    // Add the first element to kickoff the loop
    Queue<Pair<TreeNode, Node>> queue = new ArrayDeque<Pair<TreeNode, Node>>();
    queue.add(head);

    while (queue.size() > 0) {
      Pair<TreeNode, Node> pair = queue.remove();
      TreeNode bNode = pair.first;
      Node nNode = pair.second;

      // Encoding the children nodes into a list of TreeNode.
      TreeNode prevBNode = null, headBNode = null;
      for (Node nChild : nNode.children) {
        TreeNode newBNode = new TreeNode(nChild.val);
        if (prevBNode == null) {
          headBNode = newBNode;
        } else {
          prevBNode.right = newBNode;
        }
        prevBNode = newBNode;

        Pair<TreeNode, Node> nextEntry = new Pair<TreeNode, Node>(newBNode, nChild);
        queue.add(nextEntry);
      }

      // Attach the list of children to the left node.
      bNode.left = headBNode;
    }

    return newRoot;
  }

  // Decodes your binary tree to an n-ary tree.
  public Node decode(TreeNode root) {
    if (root == null) {
      return null;
    }
    Node newRoot = new Node(root.val, new ArrayList<Node>());

    // adding the first element to kickoff the loop
    Queue<Pair<Node, TreeNode>> queue = new ArrayDeque<Pair<Node, TreeNode>>();
    Pair<Node, TreeNode> head = new Pair<Node, TreeNode>(newRoot, root);
    queue.add(head);

    while (queue.size() > 0) {
      Pair<Node, TreeNode> entry = queue.remove();
      Node nNode = entry.first;
      TreeNode bNode = entry.second;

      // Decoding the children list
      TreeNode firstChild = bNode.left;
      TreeNode sibling = firstChild;
      while (sibling != null) {
        Node nChild = new Node(sibling.val, new ArrayList<Node>());
        nNode.children.add(nChild);

        // prepare the decoding the children of the child, by standing in the queue.
        Pair<Node, TreeNode> nextEntry = new Pair<Node, TreeNode>(nChild, sibling);
        queue.add(nextEntry);

        sibling = sibling.right;
      }
    }

    return newRoot;
  }
}

// Your Codec object will be instantiated and called as such:
// Codec codec = new Codec();
// codec.decode(codec.encode(root));
