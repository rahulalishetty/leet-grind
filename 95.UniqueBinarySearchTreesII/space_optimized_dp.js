var generateTrees = function (n) {
  let dp = new Array(n + 1).fill(0).map(() => new Array());
  dp[0].push(null);
  for (let numberOfNodes = 1; numberOfNodes <= n; numberOfNodes++) {
    for (let i = 1; i <= numberOfNodes; i++) {
      let j = numberOfNodes - i;
      dp[i - 1].forEach((left) => {
        dp[j].forEach((right) => {
          let root = new TreeNode(i, left, clone(right, i));
          dp[numberOfNodes].push(root);
        });
      });
    }
  }
  return dp[n];
  function clone(node, offset) {
    if (!node) {
      return null;
    }
    let clonedNode = new TreeNode(node.val + offset);
    clonedNode.left = clone(node.left, offset);
    clonedNode.right = clone(node.right, offset);
    return clonedNode;
  }
};
