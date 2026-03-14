var preorderTraversal = function (root) {
  if (!root) return [];

  const stack = [root],
    output = [];

  while (stack.length > 0) {
    const curNode = stack.pop();

    output.push(curNode.val);

    if (curNode.right) {
      stack.push(curNode.right);
    }

    if (curNode.left) {
      stack.push(curNode.left);
    }
  }

  return output;
};
