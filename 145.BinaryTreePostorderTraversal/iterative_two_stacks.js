var postorderTraversal = function (root) {
  if (!root) return [];

  const mainStack = [root],
    pathStack = [],
    output = [];
  let cur = root;

  while (mainStack.length !== 0) {
    cur = mainStack.slice(-1);

    if (pathStack.length !== 0 && pathStack.slice(-1) === cur) {
      output.push(cur.val);
      mainStack.pop();
      pathStack.pop();
    } else {
      pathStack.push(cur);

      if (root.right) {
        mainStack.push(root.right);
      }

      if (root.left) {
        mainStack.push(root.left);
      }
    }
  }

  return output;
};
