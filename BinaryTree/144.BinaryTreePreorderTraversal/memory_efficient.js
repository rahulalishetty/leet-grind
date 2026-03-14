var preorderTraversal = function (root) {
  let output = [];
  let node = root;
  while (node) {
    if (!node.left) {
      output.push(node.val);
      node = node.right;
    } else {
      let predecessor = node.left;
      while (predecessor.right && predecessor.right != node) {
        predecessor = predecessor.right;
      }
      if (!predecessor.right) {
        output.push(node.val);
        predecessor.right = node;
        node = node.left;
      } else {
        predecessor.right = null;
        node = node.right;
      }
    }
  }
  return output;
};
