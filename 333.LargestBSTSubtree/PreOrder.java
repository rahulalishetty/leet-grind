class Solution {
  // Function to check if given tree is a valid Binary Search Tree or not.
  private boolean isValidBST(TreeNode root) {
    // An empty tree is a valid Binary Search Tree.
    if (root == null) {
      return true;
    }

    // Find the max node in the left subtree of current node.
    int leftMax = findMax(root.left);

    // If the left subtree has a node greater than or equal to the current node,
    // then it is not a valid Binary Search Tree.
    if (leftMax >= root.val) {
      return false;
    }

    // Find the min node in the right subtree of current node.
    int rightMin = findMin(root.right);

    // If the right subtree has a value less than or equal to the current node,
    // then it is not a valid Binary Search Tree.
    if (rightMin <= root.val) {
      return false;
    }

    // If the left and right subtrees of current tree are also valid BST.
    // then the whole tree is a BST.
    if (isValidBST(root.left) && isValidBST(root.right)) {
      return true;
    }

    return false;
  }

  private int findMax(TreeNode root) {
    // Max node in a empty tree should be smaller than parent.
    if (root == null) {
      return Integer.MIN_VALUE;
    }

    // Check the maximum node from the current node, left and right subtree of the
    // current tree
    return Math.max(Math.max(root.val, findMax(root.left)), findMax(root.right));
  }

  private int findMin(TreeNode root) {
    // Min node in a empty tree should be larger than parent.
    if (root == null) {
      return Integer.MAX_VALUE;
    }

    // Check the minimum node from the current node, left and right subtree of the
    // current tree
    return Math.min(Math.min(root.val, findMin(root.left)), findMin(root.right));
  }

  private int countNodes(TreeNode root) {
    // Empty tree has 0 nodes.
    if (root == null) {
      return 0;
    }

    // Add nodes in left and right subtree.
    // Add 1 and return total size.
    return 1 + countNodes(root.left) + countNodes(root.right);
  }

  public int largestBSTSubtree(TreeNode root) {
    if (root == null) {
      return 0;
    }

    // If current subtree is a validBST, its children will have smaller size BST.
    if (isValidBST(root)) {
      return countNodes(root);
    }

    // Find BST in left and right subtrees of current nodes.
    return Math.max(largestBSTSubtree(root.left), largestBSTSubtree(root.right));
  }
}

/**
 * # Complexity Summary — Largest BST Subtree (Naive Approach)
 *
 * ## Time Complexity
 *
 * - **isValidBST**
 * For each node, min/max values are computed by traversing subtrees → **O(N)**
 * per node
 * Across all nodes → **O(N²)**
 *
 * - **countNodes**
 * Traverses a subtree → **O(N)**
 *
 * - **largestBSTSubtree**
 * For each of **N** nodes:
 * - BST validation → **O(N²)**
 * - Node counting → **O(N)**
 **
 * Overall Time Complexity:** **O(N³)**
 *
 * ---
 *
 * ## Space Complexity
 *
 * - Recursion stack depth equals tree height **H**
 * - Worst case (skewed tree): **H = N**
 **
 * Space Complexity:** **O(N)**
 */
