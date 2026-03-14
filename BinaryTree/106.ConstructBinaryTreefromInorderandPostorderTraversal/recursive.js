/**
 * Definition for a binary tree node.
 * function TreeNode(val, left, right) {
 *     this.val = (val===undefined ? 0 : val)
 *     this.left = (left===undefined ? null : left)
 *     this.right = (right===undefined ? null : right)
 * }
 */
/**
 * @param {number[]} inorder
 * @param {number[]} postorder
 * @return {TreeNode}
 */
var buildTree = function (inorder, postorder) {
  if (postorder.length === 0) return null;
  const head = postorder.pop();
  let root = new TreeNode(head);
  const index = inorder.indexOf(head) + 1;
  const rightInorder = inorder.splice(index);
  inorder.pop();
  const rightPostorder = postorder.splice(
    postorder.length - rightInorder.length
  );
  root.left = buildTree(inorder, postorder);
  root.right = buildTree(rightInorder, rightPostorder);
  return root;
};
