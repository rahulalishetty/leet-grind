var maxDepth = function (root) {
  let stack = [];
  if (root != null) stack.push({ node: root, depth: 1 });
  let depth = 0;
  while (stack.length != 0) {
    let { node, depth: currentDepth } = stack.pop();
    if (node != null) {
      depth = Math.max(depth, currentDepth);
      stack.push({ node: node.left, depth: currentDepth + 1 });
      stack.push({ node: node.right, depth: currentDepth + 1 });
    }
  }
  return depth;
};
