/**
 * Definition for a binary tree node.
 * function TreeNode(val, left, right) {
 *     this.val = (val===undefined ? 0 : val)
 *     this.left = (left===undefined ? null : left)
 *     this.right = (right===undefined ? null : right)
 * }
 */
/**
 * @param {TreeNode} root
 * @return {number[]}
 */
var inorderTraversal = function (root) {
  let output = [];
  let node = root;
  let predecessor;

  while (node) {
    if (!node.left) {
      output.push(node.val);
      node = node.right; // move to next right node
    } else {
      predecessor = node.left;
      while (predecessor.right && predecessor.right != node) {
        // find rightmost
        predecessor = predecessor.right;
      }

      if (!predecessor.right) {
        // establish a link back to the nodeent node
        predecessor.right = node;
        node = node.left;
      } else {
        // outputtore the tree structure
        predecessor.right = null;
        output.push(node.val);
        node = node.right;
      }
    }
  }

  return output;
};
