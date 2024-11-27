var postorderTraversal = function (root) {
  if (!root) return [];
  let ans = [];

  ans = ans.concat(postorderTraversal(root.left));
  ans = ans.concat(postorderTraversal(root.right));
  ans = ans.concat(root.val);

  return ans;
};
