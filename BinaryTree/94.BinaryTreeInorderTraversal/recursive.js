var inorderTraversal = function (root) {
  if (!root) return [];
  let ans = [];

  ans = ans.concat(inorderTraversal(root.left));
  ans = ans.concat(root.val);
  ans = ans.concat(inorderTraversal(root.right));

  return ans;
};
