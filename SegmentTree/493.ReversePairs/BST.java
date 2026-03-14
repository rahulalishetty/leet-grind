class BST {
  class Node {
    long val;
    int count;
    int leftCount;
    Node left, right;

    Node(long val) {
      this.val = val;
      this.count = 1;
    }
  }

  public int reversePairs(int[] nums) {
    Node root = null;
    int count = 0;
    for (int i = nums.length - 1; i >= 0; i--) {
      count += search(root, nums[i]);
      root = insert(root, (long) nums[i] * 2);
    }
    return count;
  }

  private int search(Node node, long val) {
    if (node == null)
      return 0;
    if (val < node.val) {
      return node.count + search(node.left, val);
    } else {
      return search(node.right, val);
    }
  }

  private Node insert(Node node, long val) {
    if (node == null)
      return new Node(val);
    if (val == node.val) {
      node.count++;
    } else if (val < node.val) {
      node.leftCount++;
      node.left = insert(node.left, val);
    } else {
      node.right = insert(node.right, val);
    }
    return node;
  }
}
