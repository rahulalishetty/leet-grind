/**
 * Definition for a binary tree node.
 * function TreeNode(val, left, right) {
 *     this.val = (val===undefined ? 0 : val)
 *     this.left = (left===undefined ? null : left)
 *     this.right = (right===undefined ? null : right)
 * }
 */
/**
 * @param {number[]} preorder
 * @param {number[]} inorder
 * @return {TreeNode}
 */
var buildTree = function (preorder, inorder) {
  if (preorder.length === 0) return null;
  const [head] = preorder.splice(0, 1);
  let root = new TreeNode(head);
  const index = inorder.indexOf(head) + 1;
  const rightInorder = inorder.splice(index);
  inorder.pop();
  const rightPreorder = preorder.splice(preorder.length - rightInorder.length);
  root.left = buildTree(preorder, inorder);
  root.right = buildTree(rightPreorder, rightInorder);
  return root;
};
