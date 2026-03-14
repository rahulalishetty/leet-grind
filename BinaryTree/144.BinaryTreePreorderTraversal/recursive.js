var preorderTraversal = function (root) {
  if (!root) return [];
  let ans = [root.val];

  ans = ans.concat(preorderTraversal(root.left));
  ans = ans.concat(preorderTraversal(root.right));

  return ans;
};
