var postorderTraversal = function (root) {
  if (!root) return [];

  const stack = [],
    output = [];
  let cur = root;

  while (cur !== null || stack.length !== 0) {
    if (cur !== null) {
      output.push(cur.val);
      stack.push(cur);
      cur = cur.right;
    } else {
      cur = stack.pop();
      cur = cur.left;
    }
  }

  return output.reverse();
};
