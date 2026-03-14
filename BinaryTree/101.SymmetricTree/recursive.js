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
 * @return {boolean}
 */
var isSymmetric = function (root) {
  function isMirror(treeOne, treeTwo) {
    if (treeOne === null && treeTwo === null) return true;
    if (treeOne === null || treeTwo === null) return false;

    return (
      treeOne.val === treeTwo.val &&
      isMirror(treeOne.right, treeTwo.left) &&
      isMirror(treeOne.left, treeTwo.right)
    );
  }

  return isMirror(root, root);
};
