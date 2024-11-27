var levelOrder = function (root) {
  if (!root) return [];
  const output = [];

  function traverse(root, level) {
    if (output[level]) {
      output[level].push(root.val);
    } else {
      output[level] = [root.val];
    }

    if (root.left) traverse(root.left, level + 1);
    if (root.right) traverse(root.right, level + 1);
  }

  traverse(root, 0);
  return output;
};
